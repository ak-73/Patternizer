package de.patternizer.eclipse.patterns.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;
import de.patternizer.eclipse.patterns.helpers.MethodVisitor;

public class BuilderInsertMethodProgrammatically implements BuilderInsertMethod
{
	
	BuilderInsertMethodProgrammatically()
	{
		
	}
	
	// METHODS
	@Override
	public void privatizeConstructorsInAST(InsertionHelper insertionHelper)
	{
		ASTManipulationHelper.privatizeConstructors(insertionHelper);
	}
	
	@Override
	public TypeDeclaration addBuilderClassToAST(InsertionHelper insertionHelper, BuilderConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		
		// Holder class declaration (plus addition into topclass in helper method)
		List<IExtendedModifier> holderModifiers = new ArrayList<IExtendedModifier>();
		holderModifiers.add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		holderModifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
		return ASTManipulationHelper.createClassDeclaration(configData.getBuilderClassIdentifier(), holderModifiers, topClassDeclaration, ast);
	}
	
	@Override
	public List<FieldDeclaration> getFields(InsertionHelper insertionHelper)
	{
		return ASTManipulationHelper.enumAllFields(insertionHelper.getTopClassDeclaration(), insertionHelper);
	}
	
	@Override
	public void privatizeFields(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, boolean finalize)
	{
		ASTManipulationHelper.privatizeFields(fieldList, insertionHelper, finalize);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addFieldsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData)
	{
		/*
		 * // the copySubtrees approach does not work // for SOME reason copySubtrees
		 * does NOT copy modifiers to newFieldList // in spite of promising a deep(!)
		 * copy of AST subtrees // Modifier is an ASTNode descendant, so it should work.
		 * Maybe because // IExtendedModifier is not? Weird. // Even worse: when you
		 * manipulate the copies to have the right modifiers, // writing the AST to file
		 * produces static nested class modifiers that mirror // the topclass field
		 * modifiers - in spite of topClassDeclaration.toString() // producing the
		 * correct results right before writing //List<FieldDeclaration> newFieldList =
		 * ASTNode.copySubtrees(insertionHelper.getAST(), fieldList);
		 * builderCLass.bodyDeclarations().addAll(newFieldList); // Safe because at this
		 * point there are only FieldDeclarations in it: List<FieldDeclaration>
		 * builderFields = builderCLass.bodyDeclarations();
		 * 
		 * for (FieldDeclaration fieldDecl : builderFields) { List<IExtendedModifier>
		 * modifierList = fieldDecl.modifiers(); modifierList.removeIf(item ->
		 * item.isModifier() && ((((Modifier) item).isFinal()))); }
		 */
		
		// guessing (correctly) that it has to do with cloning ASTNodes within the same
		// AST we're cloning to a fresh AST first and then cloning the clones back to
		// the original AST
		List<FieldDeclaration> newFieldList = cloneFieldsInSameAST(fieldList, insertionHelper);
		List<FieldDeclaration> builderFields = builderClass.bodyDeclarations();
		builderFields.addAll(newFieldList);
		
		for (FieldDeclaration fieldDecl : builderFields)
		{
			List<IExtendedModifier> modifierList = fieldDecl.modifiers();
			modifierList.removeIf(item -> item.isModifier() && ((((Modifier) item).isFinal())));
			
		}
		
		if (configData.isInitializersRemoving())
		{
			for (FieldDeclaration fieldDecl : fieldList)
			{
				List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();
				for (VariableDeclarationFragment fragment : fragmentList)
				{
					fragment.setInitializer(null);
				}				
			}
		}
		
	}

	private List<FieldDeclaration> cloneFieldsInSameAST(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper)
	{
		AST newAST = AST.newAST(insertionHelper.getAST().apiLevel(), false);
		List<FieldDeclaration> tempFieldList = ASTNode.copySubtrees(newAST, fieldList);
		List<FieldDeclaration> newFieldList = ASTNode.copySubtrees(insertionHelper.getAST(), tempFieldList);
		return newFieldList;
	}
	
	private List<ASTNode> cloneASTNodeSubtreeInSameAST(List<ASTNode> nodeList, AST oldAST)
	{
		AST newAST = AST.newAST(oldAST.apiLevel(), false);
		List<ASTNode> tempFieldList = ASTNode.copySubtrees(newAST, nodeList);
		List<ASTNode> newFieldList = ASTNode.copySubtrees(oldAST, tempFieldList);
		return newFieldList;
	}
	
	@SuppressWarnings("unchecked")
	private ASTNode cloneASTNodeWithSubtreeInSameAST(ASTNode oldNode, AST oldAST)
	{
		AST newAST = AST.newAST(oldAST.apiLevel(), false);
		ASTNode tempNode = ASTNode.copySubtree(newAST, oldNode);
		ASTNode newNode = ASTNode.copySubtree(oldAST, tempNode);
		return newNode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addMethodsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		
		for (FieldDeclaration fieldDecl : fieldList)
		{
			List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();
			for (VariableDeclarationFragment fragment : fragmentList)
			{
				MethodDeclaration method = ast.newMethodDeclaration();
				SimpleName methodName = (SimpleName) cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
				method.setName(methodName);
				method.setReturnType2(ast.newSimpleType(ast.newSimpleName(configData.getBuilderClassIdentifier())));
				List<SingleVariableDeclaration> paramList = new ArrayList<SingleVariableDeclaration>();
				SingleVariableDeclaration paramDecl = ast.newSingleVariableDeclaration();
				//Type newType = ast.newSimpleType(ast.newSimpleName(fieldDecl.getType().toString()));
				Type newType = (Type) cloneASTNodeWithSubtreeInSameAST(fieldDecl.getType(), ast);
				paramDecl.setType(newType);
				//SimpleName paramName = (SimpleName) cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
				paramDecl.setName(ast.newSimpleName("val"));
				paramList.add(paramDecl);
				method.parameters().addAll(paramList);
								
				Block methodBody = ast.newBlock();				
				Assignment assignment = ast.newAssignment();
				SimpleName fieldName = (SimpleName) cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
				assignment.setLeftHandSide(fieldName);
				assignment.setRightHandSide(ast.newSimpleName("val"));
				methodBody.statements().add(ast.newExpressionStatement(assignment));
				
				ReturnStatement returnStatement = ast.newReturnStatement();
				returnStatement.setExpression(ast.newThisExpression());
				methodBody.statements().add(returnStatement);
				
				method.setBody(methodBody);
				builderClass.bodyDeclarations().add(method);
			}
			
		}
		
		MethodDeclaration buildMethod = ast.newMethodDeclaration();
		buildMethod.setName(ast.newSimpleName(configData.getBuildMethodIdentifier()));
		buildMethod.setReturnType2(ast.newSimpleType(ast.newSimpleName(topClassDeclaration.getName().toString())));
		buildMethod.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		
		Block buildMethodBody = ast.newBlock();
		ClassInstanceCreation instanceCreation = ast.newClassInstanceCreation();
		SimpleName topClassName = (SimpleName) cloneASTNodeWithSubtreeInSameAST(topClassDeclaration.getName(), ast);
		Type topClassType = ast.newSimpleType(topClassName);
		instanceCreation.setType(topClassType);
		instanceCreation.arguments().add(ast.newThisExpression());
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(instanceCreation);		
		buildMethodBody.statements().add(returnStatement);
		
		buildMethod.setBody(buildMethodBody);
		builderClass.bodyDeclarations().add(buildMethod);
	}
	
	@Override
	public void addBuilderConstructorToTopClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		String builderClassAsParamName = Character.toLowerCase(builderClass.getName().toString().charAt(0)) + builderClass.getName().toString().substring(1);
		
		
		MethodDeclaration method = ast.newMethodDeclaration();
		method.setConstructor(true);
		SimpleName methodName = (SimpleName) cloneASTNodeWithSubtreeInSameAST(topClassDeclaration.getName(), ast);
		method.setName(methodName);		
		method.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		List<SingleVariableDeclaration> paramList = new ArrayList<SingleVariableDeclaration>();
		SingleVariableDeclaration paramDecl = ast.newSingleVariableDeclaration();
		Type paramType = ast.newSimpleType(ast.newSimpleName(builderClass.getName().toString()));
		paramDecl.setType(paramType);
		SimpleName paramName = ast.newSimpleName(builderClassAsParamName);
		paramDecl.setName(paramName);
		paramList.add(paramDecl);
		method.parameters().addAll(paramList);	
		
		
				
		Block methodBody = ast.newBlock();
		for (FieldDeclaration fieldDecl : fieldList)
		{
			List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();
			for (VariableDeclarationFragment fragment : fragmentList)
			{
				Assignment assignment = ast.newAssignment();				
				SimpleName fragmentName = (SimpleName) cloneASTNodeWithSubtreeInSameAST(fragment.getName(), ast);
				assignment.setLeftHandSide(fragmentName);
				Name builderFieldName = ast.newName(builderClassAsParamName + "." + fragmentName);
				assignment.setRightHandSide(builderFieldName);
				methodBody.statements().add(ast.newExpressionStatement(assignment));
			}						
		}
		
		method.setBody(methodBody);		
		topClassDeclaration.bodyDeclarations().add(method);	
	}

	@Override
	public void removeMethodsFromTopClass(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData)
	{
		CompilationUnit cu = insertionHelper.getCU();
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		
		MethodVisitor methodVisitor = new MethodVisitor();
		cu.accept(methodVisitor);
		
		List<MethodDeclaration> methodList = methodVisitor.getMethods();				
		List<MethodDeclaration> killList = new ArrayList<MethodDeclaration>();
		
		for (MethodDeclaration method : methodList)
		{			
			if (method.isConstructor())
			{
				if (configData.isConstructorsRemoving()) killList.add(method);
				continue;
			}
			
			String methodName = method.getName().toString();
			boolean matched = false;
			
			for (FieldDeclaration fieldDecl : fieldList)
			{
				List<VariableDeclarationFragment> fragmentList = fieldDecl.fragments();				
				for (VariableDeclarationFragment fragment : fragmentList)
				{
					String capitalizedFieldName = Character.toUpperCase(fragment.getName().toString().charAt(0)) + fragment.getName().toString().substring(1);
					
					if (methodName.equals("set" + capitalizedFieldName))
					{
						matched = true;
						if (configData.isSettersRemoving())
						{
							killList.add(method);							
						}	
						break;	
					}					
					if (methodName.equals("get" + capitalizedFieldName))
					{
						matched = true;
						if (configData.isGettersRemoving())
						{
							killList.add(method);							
						}
						break;						
					}															
				}
				
				if (matched) break;
								
			}
			
			if (!matched && configData.isOtherMethodsRemoving())
			{
				killList.add(method);
				matched = true;
				break;
			}
			
		}
		
		topClassDeclaration.bodyDeclarations().removeAll(killList);
				


	}
	
}
