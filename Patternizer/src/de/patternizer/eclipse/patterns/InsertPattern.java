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
	
	// FIELDS
	protected IWorkbenchWindow window = null;
	protected String patternName = "UNDEFINED";
	
	
	
	
	// CONSTRUCTORS
	public InsertPattern(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	
	
	
	
	// ABSTRACT METHODS
	/**
	 * Abstract factory method that returns a pattern inserting instance for the
	 * user selected pattern implementation as denoted by passed
	 * {@code Class parameter}.
	 * <p>
	 * For example, if the user had opted to insert a singleton pattern in the
	 * context menu and then selected a lazy initialization singleton pattern in the
	 * ensuing config dialog, this method would be called on {@code InsertPattern}
	 * subclass {@link de.patternizer.eclipse.patterns.singleton.InsertSingleton
	 * InsertSingleton} and return an instance of {@code PatternImplType} subclass
	 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeLazy
	 * SingletonImplTypeLazy}.
	 * 
	 * @param implTypeClass a {@code Class} object that denotes the specific
	 *                      selected pattern implementation
	 * 						
	 * @returns an instance of <b>implTypeClass</b> (therefore a subclass of
	 *          {@link PatternImplType}) responsible for inserting the selected
	 *          pattern implementation
	 */
	public abstract PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass);
	
	/**
	 * Abstract factory method that returns config data of the appropriate subclass,
	 * eg. {@link de.patternizer.eclipse.patterns.singleton.SingletonConfigData
	 * SingletonConfigData} for singletons.
	 * 
	 * @param event                  the Eclipse event that triggered this command
	 *                               execution
	 * @param patternImplementations a {@code Class} list of all enumerated classes
	 *                               that are capable of inserting the user-selected
	 *                               pattern via a specific implementation (eg, Lazy
	 *                               Singleton, Synchronized Singleton, etc)
	 * @return A {@link PatternConfigData} subclass that encapsulates configuration
	 *         data that is required for inserting a pattern of the user-selected
	 *         type
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
	
	
	
	
	
	
	// MAIN METHODS
	/**
	 * This method handles the configuration of the pattern insertion, usually by
	 * opening a config dialog for the user. Most importantly, the configuration
	 * data includes a selected pattern implementation type (eg, simple singleton
	 * vs. lazy initialization singleton vs synchronized singleton, etc) that
	 * extends {@link PatternImplType}
	 * 
	 * @param event                  the Eclipse event that triggered this command
	 *                               execution
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
	 * @param event      the Eclipse event that triggered this command execution
	 * @param configData
	 * @see de.patternizer.eclipse.patterns.singleton.InsertSingleton
	 *      InsertSingleton for an example implementation
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
	
	
	
	
	
	
	// HELPER METHODS
	/***
	 * Opens the pattern insertion config dialog.
	 * 
	 * @param event      the Eclipse event that triggered this command execution
	 * @param configData an instance of a {@code PatternConfigData} subclass (eg,
	 *                   {@link de.patternizer.eclipse.patterns.singleton.SingletonConfigData
	 *                   SingletonConfigData} for singleton insertions)
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
	 * This method is responsible for finally writing any changes made back into the
	 * .java file. This is necessary because all changes made in
	 * {@link #insertPattern(ExecutionEvent, PatternConfigData)} prior to calling
	 * this method only manipulate the AST in memory.
	 * 
	 * @param insertionHelper helper class
	 */
	public void writeChangesFromASTToSourceFile(InsertionHelper insertionHelper)
	{
		ICompilationUnit unit = insertionHelper.getICU();
		CompilationUnit cu = insertionHelper.getCU();
		Document document = insertionHelper.getDocument();
		
		System.out.println(insertionHelper.getTopClassDeclaration());
		
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
