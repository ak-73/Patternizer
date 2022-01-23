package de.patternizer.eclipse.patterns.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;
import de.patternizer.eclipse.patterns.helpers.MethodVisitor;

/**
 * This class manipulates abstract syntax trees <i>programmatically</i> in order
 * to make changes necessary for inserting various Builder implementations into
 * source.
 * 
 * 
 * @author Alexander Kalinowski
 *
 */
public class BuilderInsertMethodProgrammatically implements BuilderInsertMethod
{

	// CONSTRUCTORS
	BuilderInsertMethodProgrammatically()
	{
		
	}
	
	
	
	// METHODS
	@Override
	public void privatizeConstructorsInAST(InsertionDataDefault insertionHelper)
	{
		// we're pushing source modifications of a more general nature (useful for many
		// patterns) into a helper class
		ASTManipulationHelper.privatizeConstructors(insertionHelper);
	}
	
	@Override
	public TypeDeclaration addBuilderClassToAST(InsertionDataDefault insertionHelper, BuilderConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getClassDeclaration();
		
		// Builder class declaration (plus addition into topclass in helper method)
		List<IExtendedModifier> builderModifiers = ASTManipulationHelper.createModifierList(ast, ModifierKeyword.PUBLIC_KEYWORD, ModifierKeyword.STATIC_KEYWORD);
		return ASTManipulationHelper.createClassDeclaration(configData.getBuilderClassIdentifier(), builderModifiers, topClassDeclaration, ast);
	}
	
	
	@Override
	public void privatizeAndFinalizeFields(List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper)
	{
		// we're pushing source modifications of a more general nature (useful for many
		// patterns) into a helper class
		ASTManipulationHelper.privatizeFields(fieldList, insertionHelper, true);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addFieldsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper,
			BuilderConfigData configData)
	{
		
		// the copySubtrees approach does not work. for SOME reason copySubtrees does
		// NOT copy modifiers to newFieldList in spite of promising a deep(!) copy of
		// AST subtrees. Modifier is an ASTNode descendant, so it should work. Maybe
		// because IExtendedModifier is not? Weird.
		//
		// Even worse: when you manipulate the copies to have the right modifiers,
		// writing the AST to file produces static nested class modifiers that mirror
		// the topclass field modifiers - in spite of topClassDeclaration.toString()
		// producing the correct results right before writing.
		//***********************
		// FOR MORE SEE HERE:
		// https://stackoverflow.com/questions/70591904/unexpected-behaviour-when-writing-modifications-by-astnode-copysubtrees-to-fil?noredirect=1#comment124788732_70591904
		// and here:
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=319448
		//***********************	
		
		// guessing (apparently correctly) that it has to do with cloning ASTNodes
		// within the same AST we're cloning to a fresh AST first and then cloning the
		// clones back to the original AST (encapsulated in a method to protect the rest
		// of our code from changes)
		//INSERT: original fields into builder class
		List<FieldDeclaration> newFieldList = ASTManipulationHelper.cloneFieldsInSameAST(fieldList, insertionHelper);
		List<FieldDeclaration> builderFields = builderClass.bodyDeclarations();
		builderFields.addAll(newFieldList);
		
		ASTManipulationHelper.removeFinalModifier(builderFields);		
		if (configData.isInitializersRemoving()) ASTManipulationHelper.removeInitializers(fieldList);		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addMethodsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper,
			BuilderConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getClassDeclaration();
		
		//INSERT: one myField() method for each myField field member
		insertMethodsForVarDeclFragments(builderClass, fieldList, configData, ast);
	
		//INSERT: build() method
		String buildMethodIdentifier = configData.getBuildMethodIdentifier();
		MethodDeclaration buildMethod = createBuildMethodDecl(ast, topClassDeclaration, buildMethodIdentifier);		
		Block buildMethodBody = createBuildMethodBody(ast, topClassDeclaration);		
		buildMethod.setBody(buildMethodBody);
		builderClass.bodyDeclarations().add(buildMethod);
	}




	
	@SuppressWarnings("unchecked")
	@Override
	public void addBuilderConstructorToTopClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper,
			BuilderConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getClassDeclaration();
		String builderClassAsParamName = Character.toLowerCase(builderClass.getName().toString().charAt(0)) + builderClass.getName().toString().substring(1);
		
		//INSERT: Builder(TopClass) constructor declaration
		MethodDeclaration method = createBuilderConstructorDecl(topClassDeclaration, ast, builderClass, builderClassAsParamName);						
		Block methodBody = createBuilderConstructorBody(ast, builderClassAsParamName, fieldList);
		
		method.setBody(methodBody);
		topClassDeclaration.bodyDeclarations().add(method);
	}


	
	@SuppressWarnings("unchecked")
	@Override
	public void removeMethodsFromTopClass(List<FieldDeclaration> fieldList, InsertionDataDefault insertionHelper, BuilderConfigData configData)
	{
		CompilationUnit cu = insertionHelper.getCU();
		TypeDeclaration topClassDeclaration = insertionHelper.getClassDeclaration();
		
		MethodVisitor methodVisitor = new MethodVisitor();
		cu.accept(methodVisitor);
		
		List<MethodDeclaration> methodList = methodVisitor.getMethods();
		List<MethodDeclaration> killList = createMethodKillList(fieldList, configData, methodList); //list of methods that will have to be removed, depending on user-selected options
		
		topClassDeclaration.bodyDeclarations().removeAll(killList);
	}

	
	
	
	
	
	
	
	
	
	
	//HELPERS (fairly specific to the builder pattern)
	/**
	 * Figure out list of methods that will have to be removed, depending on user-selected options.
	 * @param fieldList
	 * @param configData
	 * @param methodList
	 * @return
	 */
	List<MethodDeclaration> createMethodKillList(List<FieldDeclaration> fieldList, BuilderConfigData configData, List<MethodDeclaration> methodList)
	{
		List<MethodDeclaration> killList = new ArrayList<MethodDeclaration>(); //list of methods that will have to be removed, depending on user-selected options
		
		for (MethodDeclaration method : methodList)
		{
			//case: constructors
			if (method.isConstructor())
			{
				if (configData.isConstructorsRemoving()) killList.add(method);
				continue;
			}
			
			//case: getters and setters
			String methodName = method.getName().toString();
			if (configData.isGettersRemoving() || configData.isSettersRemoving())
			{
				boolean matched = addGettersSettersToKillList(fieldList, configData, killList, method, methodName);
				if (matched) continue;
			}
			
			
			//case: every other method
			if (configData.isOtherMethodsRemoving()) killList.add(method);
			
		}
		return killList;
	}



	/**
	 * If a getter or setter and scheduled for removal, add to killlist.
	 * @param fieldList
	 * @param configData
	 * @param killList
	 * @param method
	 * @param methodName
	 * @return
	 */
	boolean addGettersSettersToKillList(List<FieldDeclaration> fieldList, BuilderConfigData configData, List<MethodDeclaration> killList,
			MethodDeclaration method, String methodName)
	{
		for (FieldDeclaration fieldDecl : fieldList)
		{			
			@SuppressWarnings("unchecked")
			List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();				
			for (VariableDeclarationFragment fragment : fragmentList)
			{
				String capitalizedFieldName = getCapitalizedFieldName(fragment);
				
				//add getters
				if (methodName.equals("get" + capitalizedFieldName))
				{
					if (configData.isGettersRemoving()) killList.add(method);
					return true;
				}
				
				//add setters
				if (methodName.equals("set" + capitalizedFieldName))
				{
					if (configData.isSettersRemoving()) killList.add(method);
					return true;
				}					
			}						
		}
		return false;
	}



	/**
	 * For constructing getter/setter names out of field names.
	 * @param fragment
	 * @return
	 */
	String getCapitalizedFieldName(VariableDeclarationFragment fragment)
	{
		return Character.toUpperCase(fragment.getName().toString().charAt(0)) + fragment.getName().toString().substring(1);
	}
	
	
	@SuppressWarnings("unchecked")
	Block createBuildMethodBody(AST ast, TypeDeclaration topClassDeclaration)
	{
		Block buildMethodBody = ast.newBlock();
		
		//INSERT: return new Topclass(this);
		//Constructor invocation		
		ClassInstanceCreation instanceCreation = ast.newClassInstanceCreation();
		SimpleName topClassName = (SimpleName) ASTManipulationHelper.cloneASTNodeWithSubtreeInSameAST(topClassDeclaration.getName(), ast);
		Type topClassType = ast.newSimpleType(topClassName);
		instanceCreation.setType(topClassType);
		instanceCreation.arguments().add(ast.newThisExpression());
		
		//return
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(instanceCreation);
		
		buildMethodBody.statements().add(returnStatement);		
		return buildMethodBody;
	}



	@SuppressWarnings("unchecked")
	MethodDeclaration createBuildMethodDecl(AST ast, TypeDeclaration topClassDeclaration, String buildMethodIdentifier)
	{
		//name
		MethodDeclaration buildMethod = ast.newMethodDeclaration();
		buildMethod.setName(ast.newSimpleName(buildMethodIdentifier));
		//modifiers
		buildMethod.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		//return
		buildMethod.setReturnType2(ast.newSimpleType(ast.newSimpleName(topClassDeclaration.getName().toString())));
		
		return buildMethod;
	}



	@SuppressWarnings("unchecked")
	void insertMethodsForVarDeclFragments(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, BuilderConfigData configData, AST ast)
	{
		for (FieldDeclaration fieldDecl : fieldList)
		{
			List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();
			for (VariableDeclarationFragment fragment : fragmentList)
			{
				//INSERT for each field: field(FieldType val) {field = val;} 
				MethodDeclaration method = createMethDeclForVarDeclFragment(configData, ast, fieldDecl, fragment);				
				Block methodBody = createMethBodyForVarDeclFragment(ast, fragment);				
				method.setBody(methodBody);
				builderClass.bodyDeclarations().add(method);
			}
			
		}
	}
	@SuppressWarnings("unchecked")
	Block createMethBodyForVarDeclFragment(AST ast, VariableDeclarationFragment fragment)
	{
		//INSERT: fieldName = val;
		Block methodBody = ast.newBlock();
		Assignment assignment = ast.newAssignment();
		SimpleName fieldName = (SimpleName) ASTManipulationHelper.cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
		assignment.setLeftHandSide(fieldName);
		assignment.setRightHandSide(ast.newSimpleName("val"));
		methodBody.statements().add(ast.newExpressionStatement(assignment));
		
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(ast.newThisExpression());
		methodBody.statements().add(returnStatement);
		return methodBody;
	}



	@SuppressWarnings("unchecked")
	MethodDeclaration createMethDeclForVarDeclFragment(BuilderConfigData configData, AST ast, FieldDeclaration fieldDecl,
			VariableDeclarationFragment fragment)
	{
		//name
		MethodDeclaration method = ast.newMethodDeclaration();
		SimpleName methodName = (SimpleName) ASTManipulationHelper.cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
		method.setName(methodName);
		//modifiers
		method.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		//return
		method.setReturnType2(ast.newSimpleType(ast.newSimpleName(configData.getBuilderClassIdentifier())));
		//parameter
		List<SingleVariableDeclaration> paramList = new ArrayList<SingleVariableDeclaration>();
		SingleVariableDeclaration paramDecl = ast.newSingleVariableDeclaration();
		Type newType = (Type) ASTManipulationHelper.cloneASTNodeWithSubtreeInSameAST(fieldDecl.getType(), ast);
		paramDecl.setType(newType);
		paramDecl.setName(ast.newSimpleName("val"));
		paramList.add(paramDecl);
		method.parameters().addAll(paramList);
		
		return method;
	}
	
	
	@SuppressWarnings("unchecked")
	Block createBuilderConstructorBody(AST ast, String builderInstanceIdentifier, List<FieldDeclaration> topClassFieldList)
	{
		Block methodBody = ast.newBlock();
		for (FieldDeclaration fieldDecl : topClassFieldList)
		{
			List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();
			for (VariableDeclarationFragment fragment : fragmentList)
			{
				//INSERT for each fieldName: fieldName = builder.fieldName;
				SimpleName fragmentName = (SimpleName) ASTManipulationHelper.cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
				Assignment assignment = ast.newAssignment();				
				assignment.setLeftHandSide(fragmentName);
				Name builderFieldName = ast.newName(builderInstanceIdentifier + "." + fragmentName);
				assignment.setRightHandSide(builderFieldName);
				methodBody.statements().add(ast.newExpressionStatement(assignment));
			}
		}
		return methodBody;
	}



	@SuppressWarnings("unchecked")
	MethodDeclaration createBuilderConstructorDecl(TypeDeclaration topClassDeclaration, AST ast, TypeDeclaration builderClass, String builderClassAsParamName)
	{
		//name/type
		MethodDeclaration method = ast.newMethodDeclaration();
		method.setConstructor(true);
		SimpleName methodName = (SimpleName) ASTManipulationHelper.cloneASTNodeWithSubtreeInSameAST(topClassDeclaration.getName(), ast);
		method.setName(methodName);
		//modifiers
		method.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		//parameter
		List<SingleVariableDeclaration> paramList = new ArrayList<SingleVariableDeclaration>();
		SingleVariableDeclaration paramDecl = ast.newSingleVariableDeclaration();
		Type paramType = ast.newSimpleType(ast.newSimpleName(builderClass.getName().toString()));
		paramDecl.setType(paramType);
		paramDecl.setName(ast.newSimpleName(builderClassAsParamName));
		paramList.add(paramDecl);
		method.parameters().addAll(paramList);
		
		return method;
	}
}
