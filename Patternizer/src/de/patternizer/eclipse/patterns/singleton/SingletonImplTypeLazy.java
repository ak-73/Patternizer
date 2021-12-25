package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.InsertionHelper;


public class SingletonImplTypeLazy extends PatternImplType
{
	public static int PRIORITY = 4;
	public static final String DESCRIPTION = "Lazy Initialization (Use for more resource intensive but non-thread safe objects)";
	
	public SingletonImplTypeLazy(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	@Override
	public void execute(PatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		SingletonConfigData sConfigData = (SingletonConfigData) configData;		
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addSingletonFieldToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	/*
	@Override
	public String getDescription()
	{
		return "Lazy Initialization (Use for more resource intensive but non-thread safe objects)";
	}
	*/
	
	
}
