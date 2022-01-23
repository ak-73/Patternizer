package de.patternizer.eclipse.patterns.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.InsertionData;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;
import de.patternizer.eclipse.patterns.helpers.SrcCodeModMethod;

public class VisitorInsertMethodProgrammatically implements VisitorInsertMethod
{
	
	// METHODS
	@Override
	public void insertVisitorAcceptHierarchical(InsertionDataDefault insertionHelper, VisitorConfigData configData)
	{
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IType visitorBaseType = configData.getVisitorBaseType();
		ITypeHierarchy visitorHierarchy = createTypeHierarchy(visitorBaseType, progressMonitor);
		ArrayList<IType> visitorSubtypes = createAffectedTypesList(visitorBaseType, visitorHierarchy);
		
		IType visiteeBaseType = configData.getVisiteeBaseType();
		ITypeHierarchy visiteeHierarchy = createTypeHierarchy(visiteeBaseType, progressMonitor);
		ArrayList<IType> visiteeSubtypes = createAffectedTypesList(visiteeBaseType, visiteeHierarchy);
		
		// create handy insertion data for each visitor source code file and turn on
		// recording for each
		// violates the single responsibility principle but it's simple enough
		SrcCodeModMethod modMethod = configData.getPatternSpecificModMethod();
		ArrayList<InsertionData> visitorDataList = getInsertionDataAndStartRecord(visitorSubtypes, modMethod);
		
		insertAllVisitAndAcceptMethods(visitorDataList, visitorBaseType, visiteeSubtypes, modMethod);
		
		// violates the single responsibility principle but it's simple enough
		insertPrePostVisitMethodsAndStopRecord(visitorDataList, visiteeBaseType, modMethod);
		
	}
	
	
	
	
	
	
	// HELPER METHODS
	// GENERAL
	ITypeHierarchy createTypeHierarchy(IType baseType, IProgressMonitor progressMonitor)
	{
		ITypeHierarchy hierarchy = null;
		try
		{
			hierarchy = baseType.newTypeHierarchy(progressMonitor);
		}
		catch (JavaModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hierarchy;
	}
	
	/**
	 * Returns a list with the base type as first element and all its subtypes following.
	 * @param baseType
	 * @param typeHierarchy
	 * @return a list with the base type as first element and all its subtypes following
	 */
	ArrayList<IType> createAffectedTypesList(IType baseType, ITypeHierarchy typeHierarchy)
	{
		ArrayList<IType> visitorSubtypes = new ArrayList<IType>(Arrays.asList(typeHierarchy.getAllSubtypes(baseType)));
		visitorSubtypes.add(0, baseType);
		return visitorSubtypes;
	}
	
	// violates the single responsibility principle but it's simple enough
	ArrayList<InsertionData> getInsertionDataAndStartRecord(ArrayList<IType> types, SrcCodeModMethod modMethod)
	{
		ArrayList<InsertionData> dataList = new ArrayList<InsertionData>();
		for (IType currentType : types)
		{
			InsertionData currentData = new InsertionData(currentType);
			dataList.add(currentData);
			if (modMethod != null) modMethod.startRecording(currentData);
		}
		return dataList;
	}
	
	void insertAllVisitAndAcceptMethods(ArrayList<InsertionData> visitorDataList, IType visitorBaseType, ArrayList<IType> visiteeSubtypes,
			SrcCodeModMethod modMethod)
	{
		for (IType currentVisiteeType : visiteeSubtypes)
		{
			//for each visitee, insert a visitor-accepting method into it
			InsertionData currentVisiteeData = new InsertionData(currentVisiteeType);
			InsertionData visitorData = new InsertionData(visitorBaseType);
			insertAcceptanceMethIntoVisitee(currentVisiteeData, visitorData, modMethod);
			
			//for the current visitee, insert a corresponding visit method into each member of the visitor hierarchy
			for (InsertionData currentVisitorData : visitorDataList)
			{
				insertMethodIntoVisitor("visit", false, currentVisitorData, currentVisiteeData);
			}
		}
	}
	
	// violates the single responsibility principle but it's simple enough
	void insertPrePostVisitMethodsAndStopRecord(ArrayList<InsertionData> visitorDataList, IType visiteeBaseType, SrcCodeModMethod modMethod)
	{
		for (InsertionData currentVisitorData : visitorDataList)
		{
			InsertionData visiteeData = new InsertionData(visiteeBaseType);
			insertMethodIntoVisitor("preVisit", true, currentVisitorData, visiteeData);
			insertMethodIntoVisitor("postVisit", false, currentVisitorData, visiteeData);
			if (modMethod != null) modMethod.writeRecordingsToAst(currentVisitorData);
		}
	}
	
	
	
	
	
	
	
	// VISITOR
	@SuppressWarnings("unchecked")
	void insertMethodIntoVisitor(String methodName, boolean returnBoolean, InsertionData visitorData, InsertionData visiteeData)
	{
		MethodDeclaration visitMeth = createVisitorMethodDecl(methodName, returnBoolean, visitorData, visiteeData);
		
		Block methodBody = createVisitorMethodBody(returnBoolean, visitorData);
		visitMeth.setBody(methodBody);
		
		visitorData.getClassDeclaration().bodyDeclarations().add(visitMeth);
	}
	
	@SuppressWarnings("unchecked")
	MethodDeclaration createVisitorMethodDecl(String methodName, boolean returnBoolean, InsertionData visitorData, InsertionData visiteeData)
	{
		// method name
		MethodDeclaration visitMeth = visitorData.getAST().newMethodDeclaration();
		visitMeth.setName(visitorData.getAST().newSimpleName(methodName));
		// modifiers
		List<IExtendedModifier> modifiers = ASTManipulationHelper.createModifierList(visitorData.getAST(), ModifierKeyword.PUBLIC_KEYWORD);
		visitMeth.modifiers().addAll(modifiers);
		// return type
		if (returnBoolean) visitMeth.setReturnType2(visitorData.getAST().newPrimitiveType(PrimitiveType.BOOLEAN));
		else visitMeth.setReturnType2(visitorData.getAST().newPrimitiveType(PrimitiveType.VOID));
		// method param
		List<SingleVariableDeclaration> paramList = new ArrayList<SingleVariableDeclaration>();
		SingleVariableDeclaration paramDecl = visitorData.getAST().newSingleVariableDeclaration();
		SimpleName visiteeTypeName = visitorData.getAST().newSimpleName(visiteeData.getClassDeclaration().getName().toString());
		paramDecl.setType(visitorData.getAST().newSimpleType(visiteeTypeName));
		paramDecl.setName(visitorData.getAST().newSimpleName("visitee"));
		paramList.add(paramDecl);
		visitMeth.parameters().addAll(paramList);
		return visitMeth;
	}
	
	@SuppressWarnings("unchecked")
	Block createVisitorMethodBody(boolean returnBoolean, InsertionData visitorData)
	{
		Block methodBody = visitorData.getAST().newBlock();
		if (returnBoolean)
		{
			ReturnStatement returnStatement = visitorData.getAST().newReturnStatement();
			returnStatement.setExpression(visitorData.getAST().newBooleanLiteral(true));
			methodBody.statements().add(returnStatement);
		}
		return methodBody;
	}
	
	
	
	
	
	
	
	// VISITEE
	void insertAcceptanceMethIntoVisitee(InsertionData visiteeData, InsertionData visitorData, SrcCodeModMethod patternSpecificModMethod)
	{
		// exactly one per method so we can start recording/writing in here
		if (patternSpecificModMethod != null) patternSpecificModMethod.startRecording(visiteeData);
		insertAcceptMethodDeclaration(visiteeData, visitorData);
		if (patternSpecificModMethod != null) patternSpecificModMethod.writeRecordingsToAst(visiteeData);
	}
	
	
	@SuppressWarnings("unchecked")
	void insertAcceptMethodDeclaration(InsertionData visiteeData, InsertionData visitorData)
	{
		MethodDeclaration acceptMeth = createVisitorAcceptingMethodDecl(visiteeData, visitorData);
		
		Block methodBody = createVisitorAcceptingMethodBody(visiteeData);
		acceptMeth.setBody(methodBody);
		
		visiteeData.getClassDeclaration().bodyDeclarations().add(acceptMeth);
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	Block createVisitorAcceptingMethodBody(InsertionData visiteeData)
	{
		Block methodBody = visiteeData.getAST().newBlock();
		
		// INSERT: if (visitor == null) then throw new IllegalArgumentException()
		IfStatement ifNullStatement = ASTManipulationHelper.createEqualsIfStatement(visiteeData.getAST(), visiteeData.getAST().newSimpleName("visitor"),
				visiteeData.getAST().newNullLiteral());
		ThrowStatement throwStatement = visiteeData.getAST().newThrowStatement();
		throwStatement.setExpression(ASTManipulationHelper.createClassInstanceCreation(visiteeData.getAST(), "IllegalArgumentException"));
		ifNullStatement.setThenStatement(throwStatement);
		methodBody.statements().add(ifNullStatement);
		
		// INSERT if (visitor.preVisit == true)
		MethodInvocation invokePreVisit = visiteeData.getAST().newMethodInvocation();
		invokePreVisit.setExpression(visiteeData.getAST().newSimpleName("visitor"));
		invokePreVisit.setName(visiteeData.getAST().newSimpleName("preVisit"));
		invokePreVisit.arguments().add(visiteeData.getAST().newThisExpression());
		IfStatement ifPreVisitTrueStatement = ASTManipulationHelper.createEqualsIfStatement(visiteeData.getAST(), invokePreVisit,
				visiteeData.getAST().newBooleanLiteral(true));
		
		// INSERT: (THEN) visitor.visit(this);
		MethodInvocation invokeVisit = visiteeData.getAST().newMethodInvocation();
		invokeVisit.setExpression(visiteeData.getAST().newSimpleName("visitor"));
		invokeVisit.setName(visiteeData.getAST().newSimpleName("visit"));
		invokeVisit.arguments().add(visiteeData.getAST().newThisExpression());
		ExpressionStatement invokeVisitStatement = visiteeData.getAST().newExpressionStatement(invokeVisit);
		ifPreVisitTrueStatement.setThenStatement(invokeVisitStatement);
		methodBody.statements().add(ifPreVisitTrueStatement);
		
		// INSERT: visitor.postVisit(this);
		MethodInvocation invokePostVisit = visiteeData.getAST().newMethodInvocation();
		invokePostVisit.setExpression(visiteeData.getAST().newSimpleName("visitor"));
		invokePostVisit.setName(visiteeData.getAST().newSimpleName("postVisit"));
		invokePostVisit.arguments().add(visiteeData.getAST().newThisExpression());
		ExpressionStatement invokePostVisitStatement = visiteeData.getAST().newExpressionStatement(invokePostVisit);
		methodBody.statements().add(invokePostVisitStatement);
		return methodBody;
	}
	
	
	
	@SuppressWarnings("unchecked")
	MethodDeclaration createVisitorAcceptingMethodDecl(InsertionData visiteeData, InsertionData visitorData)
	{
		// name
		MethodDeclaration acceptMeth = visiteeData.getAST().newMethodDeclaration();
		acceptMeth.setName(visiteeData.getAST().newSimpleName("accept"));
		// modifiers
		List<IExtendedModifier> modifiers = ASTManipulationHelper.createModifierList(visiteeData.getAST(), ModifierKeyword.PUBLIC_KEYWORD);
		acceptMeth.modifiers().addAll(modifiers);
		// return
		acceptMeth.setReturnType2(visiteeData.getAST().newPrimitiveType(PrimitiveType.VOID));
		// method param
		List<SingleVariableDeclaration> paramList = new ArrayList<SingleVariableDeclaration>();
		SingleVariableDeclaration paramDecl = visiteeData.getAST().newSingleVariableDeclaration();
		SimpleName visitorTypeName = visiteeData.getAST().newSimpleName(visitorData.getClassDeclaration().getName().toString());
		paramDecl.setType(visiteeData.getAST().newSimpleType(visitorTypeName));
		paramDecl.setName(visiteeData.getAST().newSimpleName("visitor"));
		paramList.add(paramDecl);
		acceptMeth.parameters().addAll(paramList);
		return acceptMeth;
	}
	
	
	
	
}

