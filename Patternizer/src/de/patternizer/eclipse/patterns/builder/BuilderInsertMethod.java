package de.patternizer.eclipse.patterns.builder;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.PatternInsertMethod;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;


public interface BuilderInsertMethod extends PatternInsertMethod
{
	//TODO method to handle top class fields? set to private and remove initializers?
	
	void privatizeConstructorsInAST(InsertionHelper insertionHelper);
	
	TypeDeclaration addBuilderClassToAST(InsertionHelper insertionHelper, BuilderConfigData configData);
	
	void addFieldsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);
	
	void addMethodsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);
	
	void addBuilderConstructorToTopClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);

	List<FieldDeclaration> getFields(InsertionHelper insertionHelper);

	void privatizeFields(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, boolean finalize);

	void removeMethodsFromTopClass(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);
	
	

	
	
	//boolean addSingletonFieldToAST(InsertionHelper insertionHelper, BuilderConfigData configData);
	
	//boolean addHolderClassToAST(InsertionHelper insertionHelper, BuilderConfigData configData);
	
	//boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, BuilderConfigData configData);	
}
