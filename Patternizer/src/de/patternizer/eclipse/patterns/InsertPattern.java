package de.patternizer.eclipse.patterns;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchWindow;

import de.patternizer.eclipse.patterns.helpers.InsertionHelper;
import de.patternizer.eclipse.patterns.singleton.InsertSingleton;

/**
 * Abstract base class that serves as focal launching point for each featured
 * pattern. Implementing classes go into their own
 * {@code de.patternizer.eclipse.patterns.<pattern>} subpackage.
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class InsertPattern
{
	
	//FIELDS
	protected IWorkbenchWindow window = null;
	protected String patternName = "UNDEFINED";
	
	
	
	//CONSTRUCTORS
	public InsertPattern(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	
	
	
	//ABSTRACT METHODS
	/**
	 * Abstract factory method that is responsible for returning an instance of the
	 * passed {@code Class} parameter.
	 * <p>
	 * This is required because we enumerate all implementations with
	 * {@code org.reflections} in a {@code List}, order them by priority and present
	 * them to the user in the config dialog. The config dialog then returns an
	 * {@code int}-based 0-index into the list. To actually
	 * {@link PatternImplType#execute(PatternConfigData, InsertionHelper) execute()}
	 * the particular pattern insertion, we need an instance - which this factory
	 * method delivers.
	 */
	public abstract PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass);
	
	/**
	 * Factory method that returns config data of the appropriate subclass, eg.
	 * {@link de.patternizer.eclipse.patterns.singleton.SingletonConfigData
	 * SingletonConfigData} for singletons.
	 * 
	 * @return A {@link PatternConfigData} subclass that encapsulates data that is
	 *         relevant specifically to any implementation of that pattern
	 */
	public abstract PatternConfigData createConfigData(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations);
	
	/**
	 * Factory method that returns a class responsible for handling the GUI for
	 * {@link PatternImplType}-specific config data. For example, the
	 * initialization-on-demand holder singleton implementation offers a Text widget
	 * to customize the name of the holder class, a feature that other singleton
	 * implementations don't require. The returned object is of the appropriate
	 * subclass, eg.
	 * {@link de.patternizer.eclipse.patterns.singleton.SingletonConfigPagePlugin
	 * SingletonConfigPagePlugin} for singletons.
	 * 
	 * @return A {@link PatternConfigPagePlugin} subclass that manipulates the
	 *         config dialog by adding/enabling or removing/disabling
	 *         {@code PatternImplType}-specific config data
	 */
	public abstract PatternConfigPagePlugin createPatternConfigPagePlugin();
	
	
	
	
	//MAIN METHODS
	/**
	 * This method handles the configuration of the pattern insertion, usually by
	 * opening a config dialog for the user. Most importantly, the configuration
	 * data includes a selected pattern implementation type (eg, simple singleton
	 * vs. lazy initialization singleton vs synchronized singleton, etc) that
	 * extends {@link PatternImplType}
	 * 
	 * @param event
	 * @param patternImplementations
	 * @return A {@link PatternConfigData} subtype. May return {@code null} in case
	 *         the user aborts the process in the config dialog or in case of an
	 *         error.
	 * @throws ExecutionException
	 */
	public PatternConfigData configurePatternInsertion(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations)
			throws ExecutionException
	{
		PatternConfigData configData = createConfigData(event, patternImplementations);
		if (!openConfigDialog(event, configData)) return null;
		
		return configData;
	}
	
	/**
	 * This method handles the actual pattern insertion, based on the configuration
	 * selected by the user, by calling
	 * {@link PatternImplType#execute(PatternConfigData, InsertionHelper)} on a
	 * subclass of {@code PatternImplType} The chosen pattern in the form selected
	 * during insertion configuration.
	 * 
	 * @param event
	 * @param configData
	 */
	public void insertPattern(ExecutionEvent event, PatternConfigData configData)
	{
		InsertionHelper insertionHelper = new InsertionHelper(window);
		insertionHelper.init(event);
		
		Class<? extends PatternImplType> implTypeClass = configData.getSelectedImplTypeClass();
		PatternImplType patternImplType = createPatternImplType(implTypeClass);
		configData.setSelectedImplTypeInstance(patternImplType);
		
		patternImplType.execute(configData, insertionHelper);
		
		writeChangesFromASTToSourceFile(insertionHelper);
	}
	
	
	//HELPER METHODS
	/***
	 * Self-explanatory.
	 * 
	 * @return False, if configuration canceled/aborted. True, otherwise.
	 */
	private boolean openConfigDialog(ExecutionEvent event, PatternConfigData configData) throws ExecutionException
	{
		PatternConfigWizard wizard = new PatternConfigWizard();
		wizard.setPatternConfigData(configData);
		wizard.setPatternName(patternName);
		PatternConfigPagePlugin configPagePlugin = createPatternConfigPagePlugin();
		configData.setPatternConfigPagePlugin(configPagePlugin);
		wizard.setPatternConfigPageHandler(configPagePlugin);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		int dialogResult = dialog.open();
		if (dialogResult != Window.OK) return false;
		return true;
	}
	
	/**
	 * The {@link InsertPattern} subclasses (eg, {@link InsertSingleton} call
	 * pattern implementation types, which in turn only modify a file's AST in
	 * memory. This method is responsible for finally writing the changes back into
	 * the .java file.
	 * 
	 * @param insertionHelper
	 */
	public void writeChangesFromASTToSourceFile(InsertionHelper insertionHelper)
	{
		ICompilationUnit unit = insertionHelper.getICU();
		CompilationUnit cu = insertionHelper.getCU();
		Document document = insertionHelper.getDocument();		
		
		TextEdit edits = cu.rewrite(document, unit.getJavaProject().getOptions(true));
		try
		{
			edits.apply(document);
			
			String newSource = document.get();
			unit.getBuffer().setContents(newSource);
		}
		catch (MalformedTreeException | BadLocationException | JavaModelException e)
		{
			// TODO reminder to handle the exceptions; also autoformat the changed file
		}
	}
	
	

	
}
