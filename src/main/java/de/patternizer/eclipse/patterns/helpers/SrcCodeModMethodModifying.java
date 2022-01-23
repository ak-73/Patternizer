package de.patternizer.eclipse.patterns.helpers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

//FIXME inquire as to the nature of the unsaved changes problems and clean up the sysouts and stuff

public class SrcCodeModMethodModifying implements SrcCodeModMethod
{
	//FIELDS
	private InsertionData insertionData;
	
	
	
	//CONSTRUCTORS
	/*public SrcCodeModMethodModifying(InsertionData insertionData)
	{
		
	}*/
	
	
	
	//METHODS
	@Override
	public void startRecording(InsertionData insertionData)
	{
		try
		{
			insertionData.getICU().becomeWorkingCopy(null);			
			insertionData.getCU().recordModifications();			
		}
		catch (IllegalArgumentException | JavaModelException e)
		{
			e.printStackTrace();
		}				
	}

	@Override
	public void writeRecordingsToAst(InsertionData insertionData)
	{
		//System.out.println("Class: " + insertionData.classDeclaration);
		
		TextEdit edits = insertionData.getCU().rewrite(insertionData.getDocument(), insertionData.getICU().getJavaProject().getOptions(true));
		
		//System.out.println("insertionData.document: " + insertionData.document.get());
		
		try
		{
			edits.apply(insertionData.getDocument());
			//System.out.println("insertionData.document: " + insertionData.document.get());
			
			String newSource = insertionData.getDocument().get();			
			//IBuffer newBuffer = insertionData.getICU().getBuffer();
			//For SOME REASON the updated the buffer for java files other than the currently selected one doesn't show up in eclipse
			//UNLESS I happen to include this sysout line
			//System.out.println("Buffer (Pre): " + newBuffer.getContents());
			//if (newBuffer.isClosed()) System.out.println("Closed");
			//if (newBuffer.isReadOnly()) System.out.println("ReadOnly");
			insertionData.getICU().getBuffer().setContents(newSource);			
			//System.out.println("Buffer (Post): " + newBuffer.getContents());
			//System.out.println("ICU (Pre-Reconcile): " + insertionData.getICU());
			//System.out.println("ICU WC: " + insertionData.getICU().isWorkingCopy());
			//insertionData.icu.save(null, false);
			//insertionData.icu.makeConsistent(null);
			//System.out.println("ICU (Post-Cons): " + insertionData.icu);
			insertionData.getICU().reconcile(ICompilationUnit.NO_AST, false, null, null);
			//insertionData.icu.commitWorkingCopy(true, null);
			//System.out.println("ICU (Post-Reconcile): " + insertionData.getICU());
			//System.out.println("ICU WC: " + insertionData.getICU().isWorkingCopy());
			//System.out.println("Unsaved: " + insertionData.getICU().getBuffer().hasUnsavedChanges());	
			//CompilationUnit foo = insertionData.cu;
			//foo.recordModifications();
			//org.eclipse.jdt.internal.core.Buffer buff = new Buffer(null, null, false);
			//buff.setContents(null);
			//buff.save(null, false);			
			//org.eclipse.jdt.internal.core.CompilationUnit compu = null;
			//compu.save(null, false);
			//compu.becomeWorkingCopy(null);			
		}
		catch (MalformedTreeException | BadLocationException | JavaModelException e)
		{
			e.printStackTrace();
		}
		
	}

	//GETTERS & SETTERS
	public InsertionData getInsertionData()
	{
		return insertionData;
	}

	public void setInsertionData(InsertionData insertionData)
	{
		this.insertionData = insertionData;
	}
	
}
