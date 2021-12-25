package de.patternizer.eclipse.patterns.builder;

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

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.InsertionHelper;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeHolder;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeLazy;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSimple;
import de.patternizer.eclipse.patterns.singleton.SingletonInsertMethodProgrammatically;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSync;
import de.patternizer.eclipse.patterns.singleton.SingletonConfigData;
import de.patternizer.eclipse.patterns.singleton.SingletonConfigPagePlugin;
import de.patternizer.eclipse.patterns.PatternConfigWizard;
import de.patternizer.eclipse.patterns.PatternImplManager;

public class InsertBuilder implements InsertPattern
{
	private IWorkbenchWindow window = null;
	
	public InsertBuilder(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	@Override
	public SingletonConfigData configurePatternInsertion(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations ) throws ExecutionException
	{
		SingletonConfigData configData = new SingletonConfigData();
		configData.setSingletonInstanceIdentifier("_______singletonInstance");
		configData.setImplTypeList(patternImplementations);
		//InsertSingletonProgrammatically insertionMethod = new InsertSingletonProgrammatically();
		//configData.setInsertionMethod(insertionMethod);
		
		if (!openConfigDialog(event, configData)) return null;
		
		return configData;
	}
	
	@Override
	public void insertPattern(ExecutionEvent event, PatternConfigData configData)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		
		// use inner class to keep parameter lists short and concise
		InsertionHelper insertionHelper = new InsertionHelper(window);
		insertionHelper.init(event);			
		
		Class<? extends PatternImplType> implTypeClass = PatternImplManager.getImplClassByIndex("Singleton", configData.getSelectedImplTypeIndex());
		PatternImplType singletonImplType = InsertBuilder.createPatternImplType(implTypeClass);
		
		singletonImplType.execute(configData, insertionHelper);
		
		writeChangesFromASTToSourceFile(insertionHelper);
	}
	
	/***
	 * Returns False, if configuration canceled/aborted. True, otherwise.
	 */
	private boolean openConfigDialog(ExecutionEvent event, SingletonConfigData configData) throws ExecutionException
	{
		PatternConfigWizard wizard = new PatternConfigWizard();
		wizard.setPatternConfigData(configData);
		wizard.setPatternName("Singleton");
		SingletonConfigPagePlugin configPagePlugin = new SingletonConfigPagePlugin();
		configData.setPatternConfigPagePlugin(configPagePlugin);
		wizard.setPatternConfigPageHandler(configPagePlugin);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		int dialogResult = dialog.open();
		if (dialogResult != Window.OK) return false;
		return true;
	}
	
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
		catch (MalformedTreeException e)
		{
			// TODO reminder to handle the exceptions; also autoformat the changed file
		}
		catch (BadLocationException e)
		{
			
		}
		catch (JavaModelException e)
		{
			
		}
	}
	
	public static PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass)
	{
		if (implTypeClass.getName().equals("SingletonImplTypeSimple")) return new SingletonImplTypeSimple( new SingletonInsertMethodProgrammatically());
		else if (implTypeClass.getName().equals("SingletonImplTypeLazy")) return new SingletonImplTypeLazy( new SingletonInsertMethodProgrammatically()); 
		else if (implTypeClass.getName().equals("SingletonImplTypeSync")) return new SingletonImplTypeSync( new SingletonInsertMethodProgrammatically());		
		else if (implTypeClass.getName().equals("SingletonImplTypeHolder")) return new SingletonImplTypeHolder( new SingletonInsertMethodProgrammatically());

		return null;
	}
}

