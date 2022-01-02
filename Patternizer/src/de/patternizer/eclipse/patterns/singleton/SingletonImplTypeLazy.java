package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;


public class SingletonImplTypeLazy extends SingletonImplType
{
	
	//FIELDS
	public static int PRIORITY = 4;
	public static final String DESCRIPTION = "Lazy Initialization (Use for more resource intensive but non-thread safe objects)";
	
	
	
	//CONSTRUCTORS
	public SingletonImplTypeLazy(SingletonInsertMethod insertionMethod)
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
	public boolean isLazyInitInsertion()
	{
		return true;
	}
	
}
