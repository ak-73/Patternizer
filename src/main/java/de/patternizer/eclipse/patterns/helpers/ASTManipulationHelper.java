package de.patternizer.eclipse.patterns.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
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

//TODO clean up

public class ASTManipulationHelper
{
	
	// HELPER METHODS
	public static FieldDeclaration createSimpleFieldDeclaration(AST ast, String fieldName, String fieldtypeName, List<IExtendedModifier> newModifiers,
			boolean defaultInit)
	{
		// field name
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(fieldName));
		
		// field initializer
		if (defaultInit)
		{
			ClassInstanceCreation instanceCreation = createClassInstanceCreation(ast, fieldtypeName);
			fragment.setInitializer(instanceCreation);
		}
		else fragment.setInitializer(ast.newNullLiteral());
		
		// field type
		FieldDeclaration singletonField = ast.newFieldDeclaration(fragment);
		singletonField.setType(ast.newSimpleType(ast.newSimpleName(fieldtypeName)));
		
		// field modifiers
		@SuppressWarnings("unchecked") // API doc says that's the type
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
	
	
	public static void privatizeConstructors(InsertionDataDefault insertionHelper)
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
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPrivate()))); // easiest this way
			modifierList.add(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		}
	}
	
	public static TypeDeclaration createClassDeclaration(String typeName, List<IExtendedModifier> newModifiers, TypeDeclaration parentClassDecl, AST ast)
	{
		TypeDeclaration classDecl = ast.newTypeDeclaration();
		classDecl.setName(ast.newSimpleName(typeName));
		@SuppressWarnings("unchecked") // API doc says that's the type
		List<IExtendedModifier> modifiers = classDecl.modifiers();
		modifiers.addAll(newModifiers);
		
		if (parentClassDecl != null)
		{
			@SuppressWarnings("unchecked") // API doc says that's the type
			List<BodyDeclaration> bDecl = parentClassDecl.bodyDeclarations();
			bDecl.add(classDecl);
		}
		
		return classDecl;
	}
	
	public static List<FieldDeclaration> enumAllFields(TypeDeclaration parentClassDeclaration, InsertionDataDefault insertionHelper)
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
	
	public static void privatizeFields(List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper, boolean finalize)
	{
		for (FieldDeclaration field : fieldList)
		{
			@SuppressWarnings("unchecked") // according to the javadoc of modifiers() this should be safe
			List<IExtendedModifier> modifierList = field.modifiers();
			
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPublic())));
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isProtected())));
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isPrivate()))); // easiest this way
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
	
	
	
	public static ITypeHierarchy getCurrentTypeHierarchy(InsertionDataDefault insertionHelper)
	{
		ICompilationUnit icu = insertionHelper.getICU();
		IType primaryType = icu.findPrimaryType();
		IProgressMonitor pMonitor = new NullProgressMonitor();
		ITypeHierarchy typeHierarchy = null;
		try
		{
			typeHierarchy = primaryType.newTypeHierarchy(pMonitor);
		}
		catch (JavaModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getAllOpenICUsInProject(insertionHelper);
		
		return typeHierarchy;
	}
	
	public static boolean isInWorkspace(String fullyQualifiedType)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IJavaModel javaModel = JavaCore.create(workspace.getRoot());
		
		List<IJavaProject> projects = new ArrayList<IJavaProject>();
		try
		{
			projects = Arrays.asList(javaModel.getJavaProjects());
		}
		catch (JavaModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (IJavaProject project : projects)
		{
			// System.out.println("Project: " + project.getElementName());
			
			IType type = null;
			try
			{
				type = project.findType(fullyQualifiedType);
			}
			catch (JavaModelException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (type != null && type.getCompilationUnit() != null)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public static List<ICompilationUnit> getAllOpenICUsInProject(InsertionDataDefault insertionHelper)
	{
		ICompilationUnit icu = insertionHelper.getICU();
		IJavaProject project = icu.getJavaProject();		
		
		List<IPackageFragment> packageFrags = null;
		try
		{
			packageFrags = Arrays.asList(project.getPackageFragments()).stream().filter(packageFrag ->
			{
				boolean isSourceFile = false;
				try
				{
					isSourceFile = (packageFrag.getKind() == IPackageFragmentRoot.K_SOURCE);
				}
				catch (JavaModelException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return isSourceFile;
			}).collect(Collectors.toList());
		}
		catch (JavaModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<ICompilationUnit> units = new ArrayList<ICompilationUnit>();
		
		for (IPackageFragment frag : packageFrags)
		{
			try
			{
				ICompilationUnit[] fragUnits = frag.getCompilationUnits();
				if (fragUnits != null)
				{
					units.addAll(Arrays.asList(fragUnits));
				}
				
			}
			catch (JavaModelException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return units;		
	}
	
	public static void removeInitializers(List<FieldDeclaration> fieldList)
	{
		for (FieldDeclaration fieldDecl : fieldList)
		{
			@SuppressWarnings("unchecked")
			List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();
			for (VariableDeclarationFragment fragment : fragmentList)
			{
				fragment.setInitializer(null);
			}
		}
	}



	public static void removeFinalModifier(List<FieldDeclaration> builderFields)
	{
		for (FieldDeclaration fieldDecl : builderFields)
		{
			@SuppressWarnings("unchecked")
			List<IExtendedModifier> modifierList = fieldDecl.modifiers();
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isFinal())));
			
		}
	}
	
	public static ASTNode cloneASTNodeWithSubtreeInSameAST(ASTNode oldNode, AST oldAST)
	{
		AST newAST = AST.newAST(oldAST.apiLevel(), false);
		ASTNode tempNode = ASTNode.copySubtree(newAST, oldNode);
		ASTNode newNode = ASTNode.copySubtree(oldAST, tempNode);
		return newNode;
	}
	
	@SuppressWarnings("unchecked")
	public static List<FieldDeclaration> cloneFieldsInSameAST(List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper)
	{
		AST newAST = AST.newAST(insertionHelper.getAST().apiLevel(), false);
		List<FieldDeclaration> tempFieldList = ASTNode.copySubtrees(newAST, fieldList);
		List<FieldDeclaration> newFieldList = ASTNode.copySubtrees(insertionHelper.getAST(), tempFieldList);
		return newFieldList;
	}
}
