package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.InsertionHelper;

public class SingletonImplTypeSimple extends PatternImplType
{
	public static int PRIORITY = 2;
	public static final String DESCRIPTION = "Simple (Readable but for non-thread safe, non-resource intensive objects only)";
	
	public SingletonImplTypeSimple(SingletonInsertMethod insertionMethod)
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
		return "Simple (Readable but for non-thread safe, non-resource intensive objects only)";
	}
	*/
	
}
