package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.IPatternConfigData;
import de.patternizer.eclipse.patterns.IPatternImplType;
import de.patternizer.eclipse.patterns.InsertionHelper;

public class InsertLazySingleton implements IPatternImplType
{
	ISimpleSingletonInsertionMethod insertionMethod = null;
	
	InsertLazySingleton(ISimpleSingletonInsertionMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
	
	@Override
	public void execute(IPatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		SingletonConfigData sConfigData = (SingletonConfigData) configData;		
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addSingletonFieldToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	@Override
	public String getDescription()
	{
		return "Lazy Initialization (Use for more resource intensive but non-thread safe objects)";
	}
	
}
