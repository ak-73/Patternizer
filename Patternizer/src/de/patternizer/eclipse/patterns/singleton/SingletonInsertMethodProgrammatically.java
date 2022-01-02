package de.patternizer.eclipse.patterns.singleton;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * This is a terrible way to manipulate the AST but for a proof-of-concept it should do. Since it's hidden behind an 
 * interface it should be easy enough to implement this via deserialization (plus adaptation) in the future instead.  
 * @author Alexander Kalinowski
 *
 */
public class SingletonInsertMethodProgrammatically implements SingletonInsertMethod
{
	
	//CONSTRUCTORS
	SingletonInsertMethodProgrammatically()
	{
		
	}

	
	
	
	
	//METHODS
	@Override
	public void privatizeConstructorsInAST(InsertionHelper insertionHelper)
	{
		ASTManipulationHelper.privatizeConstructors(insertionHelper);
	}
	
	
	
	@Override
	public boolean addSingletonFieldToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		SingletonImplType singletonImplType = (SingletonImplType) configData.getSelectedImplTypeInstance();
		
		//setup field modifiers and create field declaration
		List<IExtendedModifier> modifiers = new ArrayList<IExtendedModifier>();	
		boolean defaultInit = false;
		modifiers.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		modifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));	
		if (singletonImplType.isSimpleInsertion())
		{
			defaultInit = true;
			modifiers.add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));			
		}		
		FieldDeclaration singletonField = ASTManipulationHelper.createSimpleFieldDeclaration(ast, configData.getSingletonInstanceIdentifier(), topClassDeclaration.getName().toString(), modifiers, defaultInit);
		
		//insertion into AST	
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<BodyDeclaration> bDecl = topClassDeclaration.bodyDeclarations();
		bDecl.add(singletonField);
		
		return true;
	}


	
	@Override
	public boolean addHolderClassToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		ICompilationUnit unit = insertionHelper.getICU();
		AST ast = insertionHelper.getAST();
		TypeDeclaration topClassDeclaration = insertionHelper.getTopClassDeclaration();
		
		
		IType primaryType = unit.findPrimaryType();
		// TODO Objects.requireNonNull(primaryType);
		if (primaryType == null) return false;
		
		//Holder class declaration (plus addition into topclass in helper method)
		List<IExtendedModifier> holderModifiers = new ArrayList<IExtendedModifier>();	
		holderModifiers.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		holderModifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
		TypeDeclaration holderClass = ASTManipulationHelper.createClassDeclaration(configData.getHolderClassIdentifier(), holderModifiers, topClassDeclaration, ast);	
		
		//static field inside Holder
		List<IExtendedModifier> holderFieldModifiers = new ArrayList<IExtendedModifier>();
		holderFieldModifiers.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		holderFieldModifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));	
		holderFieldModifiers.add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));		
		FieldDeclaration singletonField2 = ASTManipulationHelper.createSimpleFieldDeclaration(ast, configData.getSingletonInstanceIdentifier(), topClassDeclaration.getName().toString(), holderFieldModifiers, true);				
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<BodyDeclaration> holderDecl = holderClass.bodyDeclarations();
		holderDecl.add(singletonField2);			
		return true;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		AST ast = insertionHelper.getAST();
		TypeDeclaration topCLassDeclaration = insertionHelper.getTopClassDeclaration();
		SingletonImplType singletonImplType = (SingletonImplType) configData.getSelectedImplTypeInstance();
		
		
		MethodDeclaration getInstantMeth = ast.newMethodDeclaration();
		getInstantMeth.setName(ast.newSimpleName("_______getInstance"));
		getInstantMeth.setReturnType2(ast.newSimpleType(ast.newSimpleName(topCLassDeclaration.getName().toString())));
		getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		if (singletonImplType.isSyncInsertion()) getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.SYNCHRONIZED_KEYWORD));
		getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
		
		Block body = ast.newBlock();
		
		//IF statement
		if (singletonImplType.isLazyInitInsertion() || singletonImplType.isSyncInsertion())
		{
			IfStatement ifStatement = ast.newIfStatement();
			InfixExpression ifClause = ast.newInfixExpression();
			SimpleName singletonObjectName = ast.newSimpleName(configData.getSingletonInstanceIdentifier());
			ifClause.setLeftOperand(singletonObjectName);
			ifClause.setRightOperand(ast.newNullLiteral());
			ifClause.setOperator(Operator.EQUALS);
			ifStatement.setExpression(ifClause);
			
			Block ifBlock = ast.newBlock();
			Assignment assignment = ast.newAssignment();
			SimpleName singletonObjectName2 = ast.newSimpleName(configData.getSingletonInstanceIdentifier());
			assignment.setLeftHandSide(singletonObjectName2);
			assignment.setOperator(Assignment.Operator.ASSIGN);
			ClassInstanceCreation instanceCreation = ast.newClassInstanceCreation();
			instanceCreation.setType(ast.newSimpleType(ast.newSimpleName(topCLassDeclaration.getName().toString())));
			assignment.setRightHandSide(instanceCreation);
			ifBlock.statements().add(ast.newExpressionStatement(assignment));
			ifStatement.setThenStatement(ifBlock);		
			body.statements().add(ifStatement);
		}
		
		//return statement
		ReturnStatement returnStatement = ast.newReturnStatement();		
		if (singletonImplType.isHolderInsertion())
		{
			QualifiedName holderFieldName = ast.newQualifiedName(ast.newSimpleName(configData.getHolderClassIdentifier()), ast.newSimpleName(configData.getSingletonInstanceIdentifier()));
			returnStatement.setExpression(holderFieldName);
		}
		else returnStatement.setExpression(ast.newSimpleName(configData.getSingletonInstanceIdentifier()));
		
		body.statements().add(returnStatement);
		
		getInstantMeth.setBody(body);
		
		topCLassDeclaration.bodyDeclarations().add(getInstantMeth);
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	
}
