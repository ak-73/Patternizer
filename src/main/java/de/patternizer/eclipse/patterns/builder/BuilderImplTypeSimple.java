package de.patternizer.eclipse.patterns.builder;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;

/**
 * This class uses an instance of a {@link BuilderInsertMethod} subclass to
 * insert a simple implementation of the Builder pattern into source.
 * 
 * @author Alexander Kalinowski
 *
 */
public class BuilderImplTypeSimple extends BuilderImplType
{
	
	// FIELDS
	public static int PRIORITY = 2;
	public static final String DESCRIPTION = "Simple";
	
	
	
	
	// CONSTRUCTORS
	public BuilderImplTypeSimple(BuilderInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	// METHODS
	/**
	 * This method conducts the Simple Builder pattern insertion process by calling
	 * the appropriate methods on a {@link BuilderInsertMethod} subclass in the
	 * appropriate order.
	 */
	@Override
	public void execute(PatternConfigData configData, InsertionDataDefault insertionHelper)
	{
		if (!(configData instanceof BuilderConfigData)) return;
		BuilderConfigData bConfigData = (BuilderConfigData) configData;
		
		if (!bConfigData.isConstructorsRemoving()) insertionMethod.privatizeConstructorsInAST(insertionHelper);
		List<FieldDeclaration> topClassFieldList = ASTManipulationHelper.enumAllFields(insertionHelper.getClassDeclaration(), insertionHelper);
		insertionMethod.privatizeAndFinalizeFields(topClassFieldList, insertionHelper);
		if (bConfigData.isSettersRemoving()) insertionMethod.removeMethodsFromTopClass(topClassFieldList, insertionHelper, bConfigData);
		TypeDeclaration builderCLass = insertionMethod.addBuilderClassToAST(insertionHelper, bConfigData);
		insertionMethod.addFieldsToBuilderClass(builderCLass, topClassFieldList, insertionHelper, bConfigData);
		insertionMethod.addBuilderConstructorToTopClass(builderCLass, topClassFieldList, insertionHelper, bConfigData);
		insertionMethod.addMethodsToBuilderClass(builderCLass, topClassFieldList, insertionHelper, bConfigData);
	}
	
	
}
