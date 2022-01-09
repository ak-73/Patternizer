package de.patternizer.eclipse.patterns.helpers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertionHelper
{
	
	// FIELDS
	private static Logger logger = LoggerFactory.getLogger(InsertionHelper.class);
	private IWorkbenchWindow window = null;
	
	private ICompilationUnit icu = null;
	private Document document = null;
	private CompilationUnit cu = null;
	private AST ast = null;
	private TypeDeclaration topClassDeclaration;
	
	
	
	// CONSTRUCTORS
	public InsertionHelper(IWorkbenchWindow window)
	{
		this.window = window;
	}
	
	
	
	
	
	
	
	// METHODS
	
	/**
	 * Initializes the helper object.
	 */
	// just initializing all the members. it all flows from the event object and we
	// just have to make sure everything is initialized properly
	// we're logging errors internally as they happen and display a generic error
	// message to the user
	// returning a boolean seems quaint but it keeps the central insertPattern()
	// methods crisp and clean, compared to try-catch.
	public boolean init(ExecutionEvent event)
	{
		// icu
		icu = getICompilationUnit(event);
		if (icu == null)
		{
			MessageDialog.openError(window.getShell(), "Insert Pattern Error!",
					"An error prevents retrieving a valid ICompilationUnit implementation. Execution of the command has been aborted. See log for more details.");
			return false;
			// throw new IllegalStateException("An error prevents retrieving a valid
			// ICompilationUnit implementation. Execution of the command has been aborted.
			// See log for more details.");
		}
		
		// document
		document = initDocument(icu);
		if (document == null)
		{
			displayErrorDialog("Could not retrieve a valid Document object. Execution of the command has been aborted. See log for more details.");
			return false;
		}
		
		// cu
		cu = parse(icu);
		if (cu == null)
		{
			displayErrorDialog("Could not parse a valid CompilationUnit object. Execution of the command has been aborted. See log for more details.");
			return false;
		}
		
		// ast
		ast = cu.getAST();
		try
		{
			cu.recordModifications();
		}
		catch (IllegalArgumentException e)
		{
			logger.error("Cannot turn on recording of modifications for compilation unit: " + e.getMessage());
			displayErrorDialog("Could not parse a valid CompilationUnit object. Execution of the command has been aborted. See log for more details.");
			return false;
		}
		
		topClassDeclaration = getTopClassDeclaration(icu, cu, ast);
		if (topClassDeclaration == null)
		{
			displayErrorDialog("Could not parse a valid CompilationUnit object. Execution of the command has been aborted. See log for more details.");
			return false;
		}
		
		return true;
	}
	
	
	public CompilationUnit getCu()
	{
		return cu;
	}







	public void setCu(CompilationUnit cu)
	{
		this.cu = cu;
	}







	public AST getAst()
	{
		return ast;
	}







	public void setAst(AST ast)
	{
		this.ast = ast;
	}







	public void setTopClassDeclaration(TypeDeclaration topClassDeclaration)
	{
		this.topClassDeclaration = topClassDeclaration;
	}







	//HELPER METHODS
	private void displayErrorDialog(String errorMessage)
	{
		if (window != null) MessageDialog.openError(window.getShell(), "Insert Pattern Error!", errorMessage);
	}
	
	
	// nothing deep
	private Document initDocument(ICompilationUnit icu)
	{
		String source;
		try
		{
			source = icu.getSource();
		}
		catch (JavaModelException e)
		{
			logger.error("getSource() could not access the associated source: {}", e);
			return null;
		}
		if (source == null)
		{
			logger.error("getSource() succeeded but could not find an associated source.");
			return null;
		}
		Document document = new Document(source);
		return document;
	}
	
	// nothing deep either
	private static CompilationUnit parse(ICompilationUnit unit)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS17);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu; // parse
	}
	
	// nothing deep either
	private ICompilationUnit getICompilationUnit(ExecutionEvent event)
	{
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditor(event);
		if (editor == null)
		{
			logger.error("getActiveEditor() returned NULL. param event = {}", event);
			return null;
		}
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		if (typeRoot == null)
		{
			logger.error("getEditorInputTypeRoot({}) returned NULL. subparam editor = {}. editor.getEditorInput() = {}.", editor, editor.getEditorInput());
			return null;
		}
		ICompilationUnit icu = (typeRoot).getAdapter(ICompilationUnit.class);
		if (icu == null)
		{
			logger.error("getAdapter() returned NULL. typeRoot instance = {}.", typeRoot);
			return null;
		}
		return icu;
	}
	
	/*
	 * Gets the AST declaration node of the top level class.
	 */
	private TypeDeclaration getTopClassDeclaration(ICompilationUnit unit, CompilationUnit cu, AST ast)
	{
		IType primaryType = unit.findPrimaryType();
		if (primaryType == null)
		{
			logger.error("findPrimaryType() returned NULL.");
			return null;
		}
		
		@SuppressWarnings("unchecked") // according to the javadoc of types() this should be safe
		List<AbstractTypeDeclaration> typedeclarations = cu.types();
		AbstractTypeDeclaration topClassDeclaration = null;
		for (AbstractTypeDeclaration typeDeclaration : typedeclarations)
		{
			SimpleName typeName = typeDeclaration.getName();
			// TODO check if the underlying assumption for this comparison holds true
			if (typeName.toString().equals(primaryType.getTypeQualifiedName()))
			{
				topClassDeclaration = typeDeclaration;
				break;
			}
		}
		
		if (topClassDeclaration == null)
		{
			logger.error("getTopClassTypeDeclaration() found no match for primaryType.");
			return null;
		}
		if (!(topClassDeclaration instanceof TypeDeclaration))
		{
			logger.error("getTopClassTypeDeclaration() found a match for primaryType but it could not be converted to class TypeDeclaration.");
			return null;
		}
		
		return (TypeDeclaration) topClassDeclaration;
	}
	
	
	
	
	// Getters & Setters
	public ICompilationUnit getICU()
	{
		return icu;
	}
	
	public Document getDocument()
	{
		return document;
	}
	
	public CompilationUnit getCU()
	{
		return cu;
	}
	
	public AST getAST()
	{
		return ast;
	}
	
	public TypeDeclaration getTopClassDeclaration()
	{
		return topClassDeclaration;
	}
}
