package de.patternizer.eclipse.patterns.singleton;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import de.patternizer.eclipse.helpers.MethodVisitor;
import de.patternizer.eclipse.patterns.InsertionHelper;

/**
 * This is a terrible way to manipulate the AST but for a proof-of-concept it should do. Since it's hidden behind an 
 * interface it should be easy enough to implement this via deserialization (plus adaptation) in the future instead.  
 * @author Alexander Kalinowski
 *
 */
public class InsertSingletonProgrammatically implements ISimpleSingletonInsertionMethod
{
	// private static Logger logger = LoggerFactory.getLogger(
	// InsertSingletonProgrammatically.class );
	// private IWorkbenchWindow window = null;
	
	//TODO handle case of no explicit constructor defined
	@Override
	public void privatizeConstructorsInAST(InsertionHelper insertionHelper)
	{
		// this method enumerates all methods first
		// and for every method that is a Constructor...
		// ...its modifier list gets iterated over. If the Constructor is public or
		// protected it gets set to private instead, if it's otoh package-private,
		// private gets added.
		// TODO "refactor" all pre-existing external calls to the now-private
		// constructors
		
		CompilationUnit cu = insertionHelper.getCU();
		AST ast = insertionHelper.getAST();
		
		MethodVisitor visitor = new MethodVisitor();
		cu.accept(visitor);
		for (MethodDeclaration method : visitor.getMethods())
		{
			if (method.isConstructor())
			{
				@SuppressWarnings("unchecked") // according to the javadoc of modifiers() this should be safe
				List<IExtendedModifier> modifierList = method.modifiers();
				Modifier newMod = ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD);
				for (IExtendedModifier listMember : modifierList)
				{
					if (listMember instanceof Modifier)
					{
						Modifier modifier = (Modifier) listMember;
						if (modifier.isProtected() || modifier.isPublic())
						{
							int index = modifierList.indexOf(modifier);
							modifierList.set(index, newMod);
						}
					}
				}
				if (!(modifierList.contains(newMod)))
				{
					modifierList.add(newMod);
				}
				
			}
		}
	}
	
	//TODO handle name collisions
	@Override
	public boolean addSingletonFieldToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		ICompilationUnit unit = insertionHelper.getICU();
		AST ast = insertionHelper.getAST();
		TypeDeclaration singletonTypeDeclaration = insertionHelper.getSingletonTypeDeclaration();
		
		IType primaryType = unit.findPrimaryType();
		// TODO Objects.requireNonNull(primaryType);
		if (primaryType == null) return false;
		
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(configData.getSingletonInstanceIdentifier()));
		if (configData.isSimpleInsertion())
		{
			ClassInstanceCreation instanceCreation = ast.newClassInstanceCreation();
			instanceCreation.setType(ast.newSimpleType(ast.newSimpleName(primaryType.getTypeQualifiedName())));
			fragment.setInitializer(instanceCreation);
		}
		else fragment.setInitializer(ast.newNullLiteral());				
		FieldDeclaration singletonField = ast.newFieldDeclaration(fragment);
		singletonField.setType(ast.newSimpleType(ast.newSimpleName(primaryType.getTypeQualifiedName())));
		
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<IExtendedModifier> modifiers = singletonField.modifiers();
		modifiers.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		modifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));	
		if (configData.isSimpleInsertion()) modifiers.add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));	
			
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<BodyDeclaration> bDecl = singletonTypeDeclaration.bodyDeclarations();
		bDecl.add(singletonField);
		
		return true;
	}
	
	//TODO handle name collisions
	@Override
	public boolean addHolderClassToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		ICompilationUnit unit = insertionHelper.getICU();
		AST ast = insertionHelper.getAST();
		TypeDeclaration singletonTypeDeclaration = insertionHelper.getSingletonTypeDeclaration();
		
		
		IType primaryType = unit.findPrimaryType();
		// TODO Objects.requireNonNull(primaryType);
		if (primaryType == null) return false;
		
		//Holder class declaration
		TypeDeclaration holderClass = ast.newTypeDeclaration();
		holderClass.setName(ast.newSimpleName("LazyHolder"));
		
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<IExtendedModifier> holderModifiers = holderClass.modifiers();
		holderModifiers.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		holderModifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));	
		
		//static field inside Holder
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(configData.getSingletonInstanceIdentifier()));
		ClassInstanceCreation instanceCreation = ast.newClassInstanceCreation();
		instanceCreation.setType(ast.newSimpleType(ast.newSimpleName(primaryType.getTypeQualifiedName())));
		fragment.setInitializer(instanceCreation);		
		FieldDeclaration singletonField = ast.newFieldDeclaration(fragment);
		singletonField.setType(ast.newSimpleType(ast.newSimpleName(primaryType.getTypeQualifiedName())));
		
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<IExtendedModifier> holderFielModifiers = singletonField.modifiers();
		holderFielModifiers.add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));
		holderFielModifiers.add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));	
		holderFielModifiers.add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));
		
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<BodyDeclaration> holderDecl = holderClass.bodyDeclarations();
		holderDecl.add(singletonField);
		
		@SuppressWarnings("unchecked") //API doc says that's the type
		List<BodyDeclaration> bDecl = singletonTypeDeclaration.bodyDeclarations();
		bDecl.add(holderClass);
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, SingletonConfigData configData)
	{
		ICompilationUnit unit = insertionHelper.getICU();
		AST ast = insertionHelper.getAST();
		TypeDeclaration singletonTypeDeclaration = insertionHelper.getSingletonTypeDeclaration();
		
		IType primaryType = unit.findPrimaryType();
		if (primaryType == null) return false;
		
		MethodDeclaration getInstantMeth = ast.newMethodDeclaration();
		getInstantMeth.setName(ast.newSimpleName("_______getInstance"));
		getInstantMeth.setReturnType2(ast.newSimpleType(ast.newSimpleName(primaryType.getTypeQualifiedName())));
		getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
		if (configData.isSyncInsertion()) getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.SYNCHRONIZED_KEYWORD));
		getInstantMeth.modifiers().add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
		
		Block body = ast.newBlock();
		
		//IF statement
		if (configData.isLazyInitInsertion() || configData.isSyncInsertion())
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
			instanceCreation.setType(ast.newSimpleType(ast.newSimpleName(primaryType.getTypeQualifiedName())));
			assignment.setRightHandSide(instanceCreation);
			ifBlock.statements().add(ast.newExpressionStatement(assignment));
			ifStatement.setThenStatement(ifBlock);		
			body.statements().add(ifStatement);
		}
		
		//return statement
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(ast.newSimpleName(configData.getSingletonInstanceIdentifier()));
		QualifiedName holderFieldName = ast.newQualifiedName(ast.newSimpleName("LazyHolder"), ast.newSimpleName(configData.getSingletonInstanceIdentifier()));
		returnStatement.setExpression(holderFieldName);
		body.statements().add(returnStatement);
		
		getInstantMeth.setBody(body);
		
		singletonTypeDeclaration.bodyDeclarations().add(getInstantMeth);
		
		return true;
	}
	
	@Override
	public boolean isSimpleSingletonImplemented()
	{
		return true;
	}
	
}
