package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.IPatternConfigData;
import de.patternizer.eclipse.patterns.IPatternImplType;
import de.patternizer.eclipse.patterns.InsertionHelper;

public class InsertHolderSingleton implements IPatternImplType
{
	ISimpleSingletonInsertionMethod insertionMethod = null;
	
	InsertHolderSingleton(ISimpleSingletonInsertionMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
	
	@Override
	public void execute(IPatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addHolderClassToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	@Override
	public String getDescription()
	{
		return "Initalization-On-Demand Holder (Thread safe and lean.)";
	}
	
}
