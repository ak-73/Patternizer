package de.patternizer.eclipse.patterns.singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.helpers.FieldVisitor;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;
import de.patternizer.eclipse.patterns.helpers.MethodVisitor;
import de.patternizer.eclipse.patterns.helpers.PatternImplManager;

class SingletonInsertMethodProgrammaticallyTest
{
	public CompilationUnit cu = null;
	public AST ast = null;
	public ICompilationUnit icu = null;
	public TypeDeclaration topClassDeclaration = null;
	
	InsertionDataDefault insertionHelperFake = null;	
	SingletonConfigData configData = null;
	
	SingletonInsertMethodProgrammatically sut = null;
	
	
	
	
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
		
		configData = new SingletonConfigData();
		
		sut = new SingletonInsertMethodProgrammatically();
	}
	
	
	
	
	
	
	@Test
	void test_addSingletonFieldToAST_FieldFound()
	{
		configData.setSingletonInstanceIdentifier("foobar");
		
		sut.addSingletonFieldToAST(insertionHelperFake, configData);
		
		FieldVisitor fieldVisitor = new FieldVisitor();		
		cu.accept(fieldVisitor);		
		//@formatter:off
		List<FieldDeclaration> fieldList = 	fieldVisitor.getFields()
													.stream()
													.filter(fieldDecl -> fieldDecl.getParent().equals(topClassDeclaration))
													.filter(fieldDecl -> ((VariableDeclarationFragment) fieldDecl.fragments().get(0)).getName().toString().equals("foobar"))
													.collect(Collectors.toList());
		//@formatter:on	
		
		Assertions.assertTrue(fieldList.size() == 1);
	}
	
	
	@Test
	void test_addHolderClassToAST_ClassFound()
	{
		configData.setHolderClassIdentifier("Foobar");
		
		sut.addHolderClassToAST(insertionHelperFake, configData);
		
		@SuppressWarnings("unchecked")
		//@formatter:off
		List<BodyDeclaration> typeList = 	((List<BodyDeclaration>) topClassDeclaration.bodyDeclarations())
											.stream()
											.filter(bodyDecl -> (bodyDecl instanceof TypeDeclaration))
											.filter(bodyDecl -> ((TypeDeclaration) bodyDecl).getName().toString().equals("Foobar"))
											.collect(Collectors.toList());
		//@formatter:on	
		
		Assertions.assertTrue(typeList.size() == 1);
	}
	
	
	@Test
	void test_addCreateInstanceMethodToAST_MethodFound()
	{
		configData.setFactoryMethodIdentifier("foobar");			
		List<Class<? extends PatternImplType>> patternImplementations = PatternImplManager.enumPatternImplTypeListByPattern("Singleton");		
		configData.setImplTypeList(patternImplementations);			
		configData.setSelectedImplTypeIndex(1); //this index should refer to SingletonImplTypeLazy class!	
		configData.setSelectedImplTypeInstance(new SingletonImplTypeLazy(sut));		
		Assertions.assertTrue(configData.getSelectedImplTypeInstance() instanceof SingletonImplTypeLazy, "This unit test was designed with SingletonImplTypeLazy in mind. Please make sure that the selection index points to it! ");
		
		sut.addCreateInstanceMethodToAST(insertionHelperFake, configData);
	
		MethodVisitor methodVisitor = new MethodVisitor();
		cu.accept(methodVisitor);		
		//@formatter:off
		List<MethodDeclaration> methList = 	methodVisitor.getMethods()
											.stream()
											.filter(methodDecl -> methodDecl.getName().toString().equals("foobar"))
											.filter(methodDecl -> methodDecl.parameters().isEmpty())
											.collect(Collectors.toList());
		//@formatter:on
		
		Assertions.assertTrue(methList.size() == 1);
		
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

}
