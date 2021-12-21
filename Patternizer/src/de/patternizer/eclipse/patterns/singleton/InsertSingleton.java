package de.patternizer.eclipse.patterns.singleton;

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

import de.patternizer.eclipse.patterns.IPatternImplType;
import de.patternizer.eclipse.patterns.IPatternInserting;
import de.patternizer.eclipse.patterns.InsertionHelper;

/**
 * Responsible for:
 * <ul>
 * <li>managing the Pattern Insertion dialog,</li>
 * <li>applying the appropriate pattern implementation (aka variant of the
 * pattern) to AST; and,</li>
 * <li>writing the AST changes made to file.</li>
 * </ul>
 *
 *
 * @author Alexander Kalinowski
 *
 */
public class InsertSingleton implements IPatternInserting
{
	private IWorkbenchWindow window = null;
	
	InsertSingleton(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	@Override
	public SingletonConfigData configurePatternInsertion(ExecutionEvent event) throws ExecutionException
	{
		SingletonConfigData configData = new SingletonConfigData();
		configData.setSingletonInstanceIdentifier("_______singletonInstance");
		if (!openConfigDialog(event, configData)) return null;
		return configData;
	}
	
	@Override
	public void insertSingleton(ExecutionEvent event, SingletonConfigData configData)
	{
		// use inner class to keep parameter lists short and concise
		InsertionHelper insertionHelper = new InsertionHelper(window);
		insertionHelper.init(event);
		
		IPatternImplType singletonImplType = configData.getSingletonImplType();
		
		singletonImplType.execute(configData, insertionHelper);
		
		writeChangesFromASTToSourceFile(insertionHelper);
	}
	
	/***
	 * Returns False, if configuration canceled/aborted. True, otherwise.
	 */
	private boolean openConfigDialog(ExecutionEvent event, SingletonConfigData configData) throws ExecutionException
	{
		SingletonConfigWizard wizard = new SingletonConfigWizard();
		wizard.setSingletonConfigData(configData);
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
	
}
