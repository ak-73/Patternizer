package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.InsertionHelper;
import de.patternizer.eclipse.patterns.PatternImplType;

public class SingletonImplTypeSync extends PatternImplType
{
	public static int PRIORITY = 6;
	public static final String DESCRIPTION = "Synchronized (Readable and thread safe but big overhead. Do not use if time-critical!)";
	
	public SingletonImplTypeSync(SingletonInsertMethod insertionMethod)
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
		return "Synchronized (Readable and thread safe but big overhead. Do not use if time-critical!)";
	}
	*/	
}
