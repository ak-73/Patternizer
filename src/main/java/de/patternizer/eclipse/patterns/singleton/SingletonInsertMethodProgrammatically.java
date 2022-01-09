package de.patternizer.eclipse.patterns.singleton;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * This class manipulates abstract syntax trees <i>programmatically</i> in order
 * to make changes necessary for inserting various singleton implementations
 * into source.
 * 
 * 
 * @author Alexander Kalinowski
 *
 */
// (This is an atrocious way to manipulate the AST but for a proof-of-concept it
// should do. Since it's hidden behind an interface it should be easy enough to
// implement this via deserialization (plus adaptation) in the future instead.)
public class SingletonInsertMethodProgrammatically implements SingletonInsertMethod
{
	
	// CONSTRUCTORS
	SingletonInsertMethodProgrammatically()
	{
		
	}
	
	
	
	
	
	// METHODS (ABSTRACT METHOD IMPLEMENTATIONS)
	@Override
	public void privatizeConstructorsInAST(InsertionHelper insertionHelper)
	{
		// we're pushing source modifications of a more general nature (useful for many
		// patterns) into a helper class
		ASTManipulationHelper.privatizeConstructors(insertionHelper);
	}
	
	
	
	@Override
	public boolean addSingletonFieldToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		SingletonImplType singletonImplType = (SingletonImplType) configData.getSelectedImplTypeInstance();
		
		// setup field modifiers and create field declaration
		boolean defaultInit = false;
		List<IExtendedModifier> modifiers = ASTManipulationHelper.createModifierList(ast, ModifierKeyword.PRIVATE_KEYWORD, ModifierKeyword.STATIC_KEYWORD);
		if (singletonImplType instanceof SingletonImplTypeSimple)
		{
			defaultInit = true;
			modifiers.add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));
		}
		FieldDeclaration singletonField = ASTManipulationHelper.createSimpleFieldDeclaration(ast, configData.getSingletonInstanceIdentifier(),
				topClassDeclaration.getName().toString(), modifiers, defaultInit);
		
		// insertion into AST
		ASTManipulationHelper.addToType(topClassDeclaration, singletonField);
		
		return true;
	}
	
	
	
	
	
	@Override
	public boolean addHolderClassToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		
		// Holder class declaration (plus addition into topclass in helper method)
		List<IExtendedModifier> holderModifiers = ASTManipulationHelper.createModifierList(ast, ModifierKeyword.PRIVATE_KEYWORD,
				ModifierKeyword.STATIC_KEYWORD);
		TypeDeclaration holderClass = ASTManipulationHelper.createClassDeclaration(configData.getHolderClassIdentifier(), holderModifiers, topClassDeclaration,
				ast);
		
		// static field inside Holder
		List<IExtendedModifier> holderFieldModifiers = ASTManipulationHelper.createModifierList(ast, ModifierKeyword.PRIVATE_KEYWORD,
				ModifierKeyword.STATIC_KEYWORD, ModifierKeyword.FINAL_KEYWORD);
		FieldDeclaration singletonField = ASTManipulationHelper.createSimpleFieldDeclaration(ast, configData.getSingletonInstanceIdentifier(),
				topClassDeclaration.getName().toString(), holderFieldModifiers, true);
		
		// insertion into Holder
		ASTManipulationHelper.addToType(holderClass, singletonField);
		
		
		return true;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		SingletonImplType singletonImplType = (SingletonImplType) configData.getSelectedImplTypeInstance();
		String holderName = configData.getHolderClassIdentifier();
		String singletonInstanceName = configData.getSingletonInstanceIdentifier();
		
		// method declaration
		MethodDeclaration getInstantMeth = createMethodDeclCreateInstance(ast, configData.getFactoryMethodIdentifier(), topClassDeclaration, singletonImplType);
		
		// method body
		Block body = ast.newBlock();
		
		// method body: IF statement
		if (singletonImplType instanceof SingletonImplTypeLazy || singletonImplType instanceof SingletonImplTypeSync)
		{
			IfStatement ifStatement = createIfStatementInCreateInstanceMethod(ast, singletonInstanceName, topClassDeclaration);
			body.statements().add(ifStatement);
		}
		
		// method body: return statement
		ReturnStatement returnStatement = ast.newReturnStatement();
		if (singletonImplType instanceof SingletonImplTypeHolder)
		{
			
			QualifiedName holderFieldName = ast.newQualifiedName(ast.newSimpleName(holderName), ast.newSimpleName(singletonInstanceName));
			returnStatement.setExpression(holderFieldName);
		}
		else returnStatement.setExpression(ast.newSimpleName(singletonInstanceName));
		body.statements().add(returnStatement);
		
		// insertion
		getInstantMeth.setBody(body);
		topClassDeclaration.bodyDeclarations().add(getInstantMeth);
		
		return true;
	}
	
	
	
	
	
	// HELPER METHODS
	@SuppressWarnings("unchecked")
	private IfStatement createIfStatementInCreateInstanceMethod(AST ast, String singletonInstanceIdentifier, TypeDeclaration topClassDeclaration)
	{
		//IF clause
		SimpleName singletonObjectName = ast.newSimpleName(singletonInstanceIdentifier);
		IfStatement ifStatement = ASTManipulationHelper.createEqualsIfStatement(ast, singletonObjectName, ast.newNullLiteral());
		
		//IF body
		Block ifBlock = ast.newBlock();
		Assignment assignment = ASTManipulationHelper.createDefaultConstructorAssignment(ast, singletonInstanceIdentifier, topClassDeclaration);
		ifBlock.statements().add(ast.newExpressionStatement(assignment));
		
		//insertion
		ifStatement.setThenStatement(ifBlock);
		return ifStatement;
	}
	
	
	@SuppressWarnings("unchecked")
	private MethodDeclaration createMethodDeclCreateInstance(AST ast, String factoryMethodIdentifier , TypeDeclaration topClassDeclaration, SingletonImplType singletonImplType)
	{
		//method name
		MethodDeclaration getInstantMeth = ast.newMethodDeclaration();
		getInstantMeth.setName(ast.newSimpleName(factoryMethodIdentifier));
		
		//return type
		getInstantMeth.setReturnType2(ast.newSimpleType(ast.newSimpleName(topClassDeclaration.getName().toString())));
		
		//modifiers
		List<IExtendedModifier> modifiers = ASTManipulationHelper.createModifierList(ast, ModifierKeyword.PUBLIC_KEYWORD, ModifierKeyword.STATIC_KEYWORD);
		getInstantMeth.modifiers().addAll(modifiers);		
		if (singletonImplType instanceof SingletonImplTypeSync) getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.SYNCHRONIZED_KEYWORD));
		
		return getInstantMeth;
	}
	
	
	
}
