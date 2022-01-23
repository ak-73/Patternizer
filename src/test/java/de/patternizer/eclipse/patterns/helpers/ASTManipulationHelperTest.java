package de.patternizer.eclipse.patterns.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ASTManipulationHelperTest
{
	public CompilationUnit cu = null;
	public AST ast = null;
	public ICompilationUnit icu = null;
	public TypeDeclaration topClassDeclaration = null;
	
	InsertionDataDefault insertionHelperFake = null;
	
	@BeforeEach
	public void parseSampleJavaFile()
	{		
		String currentDirectory = System.getProperty("user.dir");
		Path pathToFile = Paths.get(currentDirectory);
		pathToFile = pathToFile.resolve("src").resolve("test").resolve("resources").resolve("SampleClass.java");
		
		// File sampleJavaFile = new File(pathToFile.toUri());
		char[] sourceArray = null;
		try
		{
			byte[] fileContent = Files.readAllBytes(pathToFile);
			String fileContentAsString = new String(fileContent, "UTF-8");
			sourceArray = fileContentAsString.toCharArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
		parser.setSource(sourceArray);
				
		cu = (CompilationUnit) parser.createAST(null);
		ast = cu.getAST();
		topClassDeclaration = getTopClassDeclaration("SampleClass", cu, ast);
		
		insertionHelperFake = new InsertionDataDefault(null);
		insertionHelperFake.setAst(ast);
		insertionHelperFake.setCu(cu);
		insertionHelperFake.setTopClassDeclaration(topClassDeclaration);
	}
	

	
	
	
	
	@Test
	void test_createSimpleFieldDeclaration_InitializerSourceString()
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();
		
		FieldDeclaration newFieldDecl = ASTManipulationHelper.createSimpleFieldDeclaration(ast, "foobar", "SampleClass", modifierList, true);
		
		Assertions.assertTrue(newFieldDecl.toString().equals("SampleClass foobar=new SampleClass();\n"), "Field declaration doesn't match expectations:\n" + newFieldDecl);				
	}
	
	@Test
	void test_createSimpleFieldDeclaration_NullInitializerSourceString()
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();
		
		FieldDeclaration newFieldDecl = ASTManipulationHelper.createSimpleFieldDeclaration(ast, "foobar", "SampleClass", modifierList, false);
		
		Assertions.assertTrue(newFieldDecl.toString().equals("SampleClass foobar=null;\n"), "Field declaration doesn't match expectations:\n" + newFieldDecl.toString());				
	}
	

	
	@Test
	@SuppressWarnings("unchecked")
	void test_createSimpleFieldDeclaration_AddedToCLass()
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();
		
		FieldDeclaration newFieldDecl = ASTManipulationHelper.createSimpleFieldDeclaration(ast, "foobar", "SampleClass", modifierList, true);
		//TODO this is nonsense, fix it later
		topClassDeclaration.bodyDeclarations().add(newFieldDecl); 
		
		FieldVisitor fieldVisitor = new FieldVisitor();
		cu.accept(fieldVisitor);
		
		//@formatter:off
		List<FieldDeclaration> fieldList = 	fieldVisitor.getFields()
													.stream()
													.filter(fieldDecl -> fieldDecl.getParent().equals(topClassDeclaration))
													.collect(Collectors.toList());
		//@formatter:on	
		
		Assertions.assertTrue(fieldList.contains(newFieldDecl), "Created field declaration not found in class!");
	}
	

	
	@Test
	void test_privatizeConstructors_OnlyPrivate()
	{
		ASTManipulationHelper.privatizeConstructors(insertionHelperFake);
		
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
			Assertions.assertTrue(isDeclaredPrivate(constructor), "Privatization unseccessful. Constructor: " + constructor.toString());			
		}
	}

	
	@Test
	void test_createClassDeclaration_SourceString()
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();
		
		TypeDeclaration newClass = ASTManipulationHelper.createClassDeclaration("Foobar", modifierList, null, ast);
		
		Assertions.assertTrue(newClass.toString().equals("class Foobar {\n}\n"));	
	}

	
	@Test
	void test_createClassDeclaration_AddedToClass()
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();
		
		TypeDeclaration newClass = ASTManipulationHelper.createClassDeclaration("Foobar", modifierList, topClassDeclaration, ast);
		
		Assertions.assertTrue(topClassDeclaration.bodyDeclarations().contains(newClass));	
	}
	
	
	@Test
	void test_privatizeFields_OnlyPrivate()
	{		
		List<FieldDeclaration> topClassFieldList = ASTManipulationHelper.enumAllFields(topClassDeclaration, insertionHelperFake);
		
		ASTManipulationHelper.privatizeFields(topClassFieldList, insertionHelperFake, false);
		
		FieldVisitor fieldVisitor = new FieldVisitor();		
		cu.accept(fieldVisitor);		
		//@formatter:off
		List<FieldDeclaration> fieldList = 	fieldVisitor.getFields()
													.stream()
													.filter(fieldDecl -> fieldDecl.getParent().equals(topClassDeclaration))
													.collect(Collectors.toList());
		//@formatter:on
		
		for (FieldDeclaration field : fieldList)
		{
			Assertions.assertTrue(isDeclaredPrivate(field), "Privatization unseccessful. Field: " + field.toString());		
		}
	}
	
	@Test
	void test_privatizeFields_PrivateAndFinal()
	{
		List<FieldDeclaration> topClassFieldList = ASTManipulationHelper.enumAllFields(topClassDeclaration, insertionHelperFake);
		
		ASTManipulationHelper.privatizeFields(topClassFieldList, insertionHelperFake, true);
		
		FieldVisitor fieldVisitor = new FieldVisitor();		
		cu.accept(fieldVisitor);		
		//@formatter:off
		List<FieldDeclaration> fieldList = 	fieldVisitor.getFields()
													.stream()
													.filter(fieldDecl -> fieldDecl.getParent().equals(topClassDeclaration))
													.collect(Collectors.toList());
		//@formatter:on
		
		for (FieldDeclaration field : fieldList)
		{
			Assertions.assertTrue(isDeclaredPrivate(field), "Privatization unseccessful. Field: " + field.toString());
			Assertions.assertTrue(isDeclaredFinal(field), "Privatization unseccessful. Field: " + field.toString());	
		}
	}
	
	@Test
	void test_createModifierList_CommonModifiersList()
	{
		List<IExtendedModifier> modifierList = ASTManipulationHelper.createModifierList(ast, ModifierKeyword.ABSTRACT_KEYWORD, ModifierKeyword.DEFAULT_KEYWORD, ModifierKeyword.FINAL_KEYWORD, ModifierKeyword.PRIVATE_KEYWORD, ModifierKeyword.PROTECTED_KEYWORD, ModifierKeyword.PUBLIC_KEYWORD, ModifierKeyword.STATIC_KEYWORD, ModifierKeyword.SYNCHRONIZED_KEYWORD);
		Assertions.assertTrue(modifierList.size()==8);
		Assertions.assertTrue(modifierList.get(0).toString().equals("abstract"), "Expected abstract but got " + modifierList.get(0).toString());
		Assertions.assertTrue(modifierList.get(1).toString().equals("default"), "Expected default but got " + modifierList.get(1).toString());
		Assertions.assertTrue(modifierList.get(2).toString().equals("final"), "Expected final but got " + modifierList.get(2).toString());
		Assertions.assertTrue(modifierList.get(3).toString().equals("private"), "Expected private but got " + modifierList.get(3).toString());
		Assertions.assertTrue(modifierList.get(4).toString().equals("protected"), "Expected protected but got " + modifierList.get(4).toString());
		Assertions.assertTrue(modifierList.get(5).toString().equals("public"), "Expected public but got " + modifierList.get(5).toString());
		Assertions.assertTrue(modifierList.get(6).toString().equals("static"), "Expected static but got " + modifierList.get(6).toString());
		Assertions.assertTrue(modifierList.get(7).toString().equals("synchronized"), "Expected synchronized but got " + modifierList.get(7).toString());			
	}
	
	@Test
	void test_addToType_SimpleField()
	{
		List<IExtendedModifier> modifierList = new ArrayList<IExtendedModifier>();
		FieldDeclaration newFieldDecl = ASTManipulationHelper.createSimpleFieldDeclaration(ast, "foobar", "SampleClass", modifierList, true);
		ASTManipulationHelper.addToType(topClassDeclaration, newFieldDecl);
		Assertions.assertTrue(topClassDeclaration.bodyDeclarations().contains(newFieldDecl), "New FieldDeclaration of " + ((VariableDeclarationFragment) newFieldDecl.fragments().get(0)).getName() + " not found in:\n" + topClassDeclaration.toString());			
	}
	
	@Test
	void test_createEqualsIfStatement_FoobarNull()
	{
		Expression left = ast.newSimpleName("foobar");
		Expression right = ast.newNullLiteral();
		IfStatement ifStatement = ASTManipulationHelper.createEqualsIfStatement(ast, left, right);
		Assertions.assertTrue(ifStatement.toString().equals("if (foobar == null) {\n}\n"));
	}
	
	@Test
	void test_createDefaultConstructorAssignment_FoobarNull()
	{
		Assignment assignment = ASTManipulationHelper.createDefaultConstructorAssignment(ast, "foobar", topClassDeclaration);		
		Assertions.assertTrue(assignment.toString().equals("foobar=new SampleClass()"));
	}
	
	
	//HELPER METHODS
	private TypeDeclaration getTopClassDeclaration(String className, CompilationUnit cu, AST ast)
	{		
		@SuppressWarnings("unchecked") // according to the javadoc of types() this should be safe
		List<AbstractTypeDeclaration> typedeclarations = cu.types();
		AbstractTypeDeclaration topClassDeclaration = null;
		for (AbstractTypeDeclaration typeDeclaration : typedeclarations)
		{
			SimpleName typeName = typeDeclaration.getName();
			// TODO check if the underlying assumption for this comparison holds true
			if (typeName.toString().equals(className))
			{
				topClassDeclaration = typeDeclaration;
				break;
			}
		}
		
		if (topClassDeclaration == null)
		{
			System.out.println("getTopClassTypeDeclaration() found no match for primaryType.");
			//logger.error("getTopClassTypeDeclaration() found no match for primaryType.");
			return null;
		}
		if (!(topClassDeclaration instanceof TypeDeclaration))
		{
			System.out.println("getTopClassTypeDeclaration() found a match for primaryType but it could not be converted to class TypeDeclaration.");
			//logger.error("getTopClassTypeDeclaration() found a match for primaryType but it could not be converted to class TypeDeclaration.");
			return null;
		}
		
		return (TypeDeclaration) topClassDeclaration;
	}
	
	
	private boolean isDeclaredPrivate(BodyDeclaration member)
	{
		@SuppressWarnings("unchecked") // according to the javadoc of modifiers() this should be safe
		List<IExtendedModifier> modifierList = member.modifiers();
		
		boolean onlyPrivateFound = false;
		for (IExtendedModifier mod : modifierList)
		{
			if (mod.toString().contains("public") || mod.toString().contains("protected"))
			{
				onlyPrivateFound = false;
				break;
			}			
			if (mod.toString().equals("private")) onlyPrivateFound = true;
		}
		return onlyPrivateFound;
	}
	
	private boolean isDeclaredFinal(BodyDeclaration member)
	{
		@SuppressWarnings("unchecked") // according to the javadoc of modifiers() this should be safe
		List<IExtendedModifier> modifierList = member.modifiers();
		
		boolean finalFound = false;
		for (IExtendedModifier mod : modifierList)
		{
			if (mod.toString().equals("final")) finalFound = true;
		}
		return finalFound;
	}
}
