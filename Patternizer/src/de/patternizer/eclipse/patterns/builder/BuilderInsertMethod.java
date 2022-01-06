package de.patternizer.eclipse.patterns.builder;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.PatternInsertMethod;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * This interface defines all the methods that need to be implemented in order
 * to satisfy the demands of all Builder pattern implementations (subclasses
 * of {@link BuilderImplType}. Implementing classes therefore may differ <i>by
 * which means</i> they make the required changes to source.
 * 
 * @author Alexander Kalinowski
 *
 */
public interface BuilderInsertMethod extends PatternInsertMethod
{
	/**
	 * Turn all pre-existing constructors in the class we're applying this pattern to {@code private}.
	 * @param insertionHelper
	 */
	void privatizeConstructorsInAST(InsertionHelper insertionHelper);
	
	/**
	 * Make all fields of the class that we're applying this pattern to both {@code private} and {@code final}.
	 * @param fieldList
	 * @param insertionHelper
	 */
	void privatizeAndFinalizeFields(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper);
	
	/**
	 * This method optionally removes various methods (Constructors, setters, etc.) from the enclosing class.
	 * @param fieldList
	 * @param insertionHelper
	 * @param configData
	 */
	void removeMethodsFromTopClass(List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);
	
	/**
	 * Add an empty, nested Builder class to the source of the class we're applying the pattern to.
	 * @param insertionHelper
	 */
	TypeDeclaration addBuilderClassToAST(InsertionHelper insertionHelper, BuilderConfigData configData);
	
	/**
	 * Copy the fields from the class we're transforming to the nested Builder class, removing any {@code final} modifier and optionally the initializers from the copies.
	 * @param insertionHelper
	 */
	void addFieldsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);
	
	/**
	 * Add setters plus a build method to the nested Builder class. The setters have the same name as the fields they're mutating and return the current Builder object for fluent initialization. The build method returns a newly constructed instance of the outer class by invoking a constructor that takes the Builder class instance as an argument.  
	 * @param builderClass
	 * @param fieldList
	 * @param insertionHelper
	 * @param configData
	 */
	void addMethodsToBuilderClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);
	
	/**
	 * Add a constructor to the class we're applying this pattern to that takes an instance of our Builder class as a parameter and initializes the outer classes fields according to the nested Builder classes fields.
	 * @param builderClass
	 * @param fieldList
	 * @param insertionHelper
	 * @param configData
	 */
	void addBuilderConstructorToTopClass(TypeDeclaration builderClass, List<FieldDeclaration> fieldList, InsertionHelper insertionHelper, BuilderConfigData configData);	
	
}
