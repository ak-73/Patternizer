package de.patternizer.eclipse.patterns.builder;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.InsertionHelper;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.singleton.SingletonInsertMethod;
import de.patternizer.eclipse.patterns.singleton.SingletonConfigData;

public class BuilderImplTypeLazy extends PatternImplType
{
	public static int PRIORITY = 4;
	public static final String DESCRIPTION = "Lazy Initialization (Use for more resource intensive but non-thread safe objects)";
	
	public BuilderImplTypeLazy(SingletonInsertMethod insertionMethod)
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
