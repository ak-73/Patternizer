package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

public class SingletonImplTypeSimple extends SingletonImplType
{
	
	//FIELDS
	public static int PRIORITY = 2;
	public static final String DESCRIPTION = "Simple (Readable but for non-thread safe, non-resource intensive objects only)";
	
	
	
	
	//CONSTRUCTORS
	public SingletonImplTypeSimple(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	//METHODS
	@Override
	public void execute(PatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addSingletonFieldToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}

	@Override
	public boolean isSimpleInsertion()
	{
		return true;
	}
	
}
