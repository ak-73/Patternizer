package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.InsertionHelper;

public class SingletonImplTypeHolder extends PatternImplType
{
	public static int PRIORITY = 8;
	public static final String DESCRIPTION = "Initalization-On-Demand Holder (Thread safe and lean.)";
	
	public SingletonImplTypeHolder(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	@Override
	public void execute(PatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addHolderClassToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
		
}
