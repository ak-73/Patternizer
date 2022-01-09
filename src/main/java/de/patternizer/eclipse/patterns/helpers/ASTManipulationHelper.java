package de.patternizer.eclipse.patterns.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class ASTManipulationHelper
{
	
	//HELPER METHODS	
	public static FieldDeclaration createSimpleFieldDeclaration(AST ast, String fieldName, String fieldtypeName, List<IExtendedModifier> newModifiers, boolean defaultInit)
	{
		//field name
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();		
		fragment.setName(ast.newSimpleName(fieldName));
		
		//field initializer
		if (defaultInit)
		{
			ClassInstanceCreation instanceCreation = createClassInstanceCreation(ast, fieldtypeName);
			fragment.setInitializer(instanceCreation);
		}
		else fragment.setInitializer(ast.newNullLiteral());
		
		//field type
		FieldDeclaration singletonField = ast.newFieldDeclaration(fragment);
		singletonField.setType(ast.newSimpleType(ast.newSimpleName(fieldtypeName)));	
		
		//field modifiers
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<IExtendedModifier> modifiers = singletonField.modifiers();
		modifiers.addAll(newModifiers);
		
		return singletonField;
	}


	public static ClassInstanceCreation createClassInstanceCreation(AST ast, String fieldtypeName)
	{
		ClassInstanceCreation instanceCreation = ast.newClassInstanceCreation();
		instanceCreation.setType(ast.newSimpleType(ast.newSimpleName(fieldtypeName)));
		return instanceCreation;
	}	
	
	
	public static void privatizeConstructors(InsertionHelper insertionHelper)
	{
		// TODO "refactor" all pre-existing external calls to the now-private
		// constructors??
		
		CompilationUnit cu = insertionHelper.getCU();
		AST ast = insertionHelper.getAST();
		
		MethodVisitor methodVisitor = new MethodVisitor();
		cu.accept(methodVisitor);
		
		//@formatter:off
		List<MethodDeclaration> constructorList = 	methodVisitor.getMethods()
													.stream()
													.filter(methodDecl -> methodDecl.isConstructor())
													.collect(Collectors.toList());
		//@formatter:on
		
		for (MethodDeclaration constructor : constructorList)
		{
			@SuppressWarnings("unchecked") // according to the javadoc of modifiers() this should be safe
			List<IExtendedModifier> modifierList = constructor.modifiers();
			
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPublic())));
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isProtected())));
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPrivate())));	//easiest this way
			modifierList.add(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		}
	}
	
	public static TypeDeclaration createClassDeclaration(String typeName, List<IExtendedModifier> newModifiers, TypeDeclaration parentClassDecl, AST ast)
	{
		TypeDeclaration classDecl = ast.newTypeDeclaration();
		classDecl.setName(ast.newSimpleName(typeName));		
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<IExtendedModifier> modifiers = classDecl.modifiers();
		modifiers.addAll(newModifiers);
		
		if (parentClassDecl != null)
		{
			@SuppressWarnings("unchecked") //API doc says that's the type
			List<BodyDeclaration> bDecl = parentClassDecl.bodyDeclarations();
			bDecl.add(classDecl);
		}
		
		return classDecl;
	}
	
	public static List<FieldDeclaration> enumAllFields(TypeDeclaration parentClassDeclaration, InsertionHelper insertionHelper)
	{
		FieldVisitor fieldVisitor = new FieldVisitor();
		CompilationUnit cu = insertionHelper.getCU();
		
		cu.accept(fieldVisitor);
		
		//@formatter:off
		List<FieldDeclaration> fieldList = 	fieldVisitor.getFields()
													.stream()
													.filter(fieldDecl -> fieldDecl.getParent().equals(parentClassDeclaration))
													.collect(Collectors.toList());
		//@formatter:on
		
		return fieldList;
	}
	
	public static void privatizeFields(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, boolean finalize)
	{
		for (FieldDeclaration field : fieldList)
		{
			@SuppressWarnings("unchecked") // according to the javadoc of modifiers() this should be safe
			List<IExtendedModifier> modifierList = field.modifiers();
			
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPublic())));
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isProtected())));
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPrivate())));	//easiest this way
			modifierList.add(insertionHelper.getAST().newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
			if (finalize) modifierList.add(insertionHelper.getAST().newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
		}
	
	}
	
	public static List<IExtendedModifier> createModifierList(AST ast, ModifierKeyword... modifiers)
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();	
		
		for (ModifierKeyword modifier : modifiers)
		{
			modifierList.add(ast.newModifier(modifier));
		}
		
		return modifierList;
	}
	
	public static void addToType(TypeDeclaration classDecl, BodyDeclaration bDecl)
	{
		@SuppressWarnings("unchecked")
		List<BodyDeclaration> bodyList = classDecl.bodyDeclarations();
		bodyList.add(bDecl);
	}
	
	public static IfStatement createEqualsIfStatement(AST ast, Expression leftOperand, Expression rightOperand)
	{
		IfStatement ifStatement = ast.newIfStatement();
		InfixExpression ifClause = ast.newInfixExpression();
		ifClause.setLeftOperand(leftOperand);
		ifClause.setRightOperand(rightOperand);
		ifClause.setOperator(Operator.EQUALS);
		ifStatement.setExpression(ifClause);
		return ifStatement;
	}
	
	
	public static Assignment createDefaultConstructorAssignment(AST ast, String varname, TypeDeclaration topClassDeclaration)
	{
		Assignment assignment = ast.newAssignment();
		SimpleName singletonObjectName2 = ast.newSimpleName(varname);
		assignment.setLeftHandSide(singletonObjectName2);
		assignment.setOperator(Assignment.Operator.ASSIGN);
		ClassInstanceCreation instanceCreation = ASTManipulationHelper.createClassInstanceCreation(ast, topClassDeclaration.getName().toString());
		assignment.setRightHandSide(instanceCreation);
		return assignment;
	}
}
