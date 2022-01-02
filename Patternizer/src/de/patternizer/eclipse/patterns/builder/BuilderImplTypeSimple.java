package de.patternizer.eclipse.patterns.builder;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

public class BuilderImplTypeSimple extends BuilderImplType
{
	
	//FIELDS
	public static int PRIORITY = 2;
	public static final String DESCRIPTION = "Simple (Readable but for non-thread safe, non-resource intensive objects only)";
	
	
	
	
	//CONSTRUCTORS
	public BuilderImplTypeSimple(BuilderInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	//METHODS
	@Override
	public void execute(PatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof BuilderConfigData)) return;
		
		BuilderConfigData bConfigData = (BuilderConfigData) configData;		
		if (!bConfigData.isConstructorsRemoving()) insertionMethod.privatizeConstructorsInAST(insertionHelper);
		List<FieldDeclaration> topClassFieldList = insertionMethod.getFields(insertionHelper);
		insertionMethod.privatizeFields(topClassFieldList, insertionHelper, true);	
		if (bConfigData.isSettersRemoving()) insertionMethod.removeMethodsFromTopClass(topClassFieldList, insertionHelper, bConfigData);
		TypeDeclaration builderCLass = insertionMethod.addBuilderClassToAST(insertionHelper, bConfigData);		
		insertionMethod.addFieldsToBuilderClass(builderCLass, topClassFieldList, insertionHelper, bConfigData);				
		insertionMethod.addBuilderConstructorToTopClass(builderCLass, topClassFieldList, insertionHelper, bConfigData);
		insertionMethod.addMethodsToBuilderClass(builderCLass, topClassFieldList, insertionHelper, bConfigData);		
	}

	@Override
	public boolean isSimpleInsertion()
	{
		return true;
	}
		
}
