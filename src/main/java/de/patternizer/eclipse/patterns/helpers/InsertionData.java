package de.patternizer.eclipse.patterns.helpers;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertionData
{
	// FIELDS
	protected static Logger logger = LoggerFactory.getLogger(InsertionData.class);
	protected IType type = null;
	protected ICompilationUnit icu = null;
	protected Document document = null;
	protected CompilationUnit cu = null;
	protected AST ast = null;		
	protected TypeDeclaration classDeclaration = null;
	
	//CONSTRUCTORS
	public InsertionData(IType visiteeType)
	{
		init(visiteeType);
	}		
	
	
	//METHODS
	public Document initDocument(ICompilationUnit icu)
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
	
	public TypeDeclaration getTopClassDeclaration(ICompilationUnit unit, CompilationUnit cu, AST ast)
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

	//HELPERS
	protected void init(IType visiteeType)
	{
		if (visiteeType != null)
		{
			type = visiteeType;
			icu = visiteeType.getCompilationUnit();		
			document = initDocument(icu);
			cu = InsertionDataDefault.parse(icu);
			if (cu  == null) throw new IllegalStateException("InsertionHelper.parse() returned a NULL CompilationUnit for visiteeICU " + icu.getElementName());		
			ast = cu.getAST();		
			classDeclaration = getTopClassDeclaration(icu, cu, ast);
		}
	}

	
	//GETTERS & SETTERS
	public IType getType()
	{
		return type;
	}


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


	public TypeDeclaration getClassDeclaration()
	{
		return classDeclaration;
	}
}
