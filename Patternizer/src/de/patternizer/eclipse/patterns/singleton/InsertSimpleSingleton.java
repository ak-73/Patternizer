package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.IPatternConfigData;
import de.patternizer.eclipse.patterns.IPatternImplType;
import de.patternizer.eclipse.patterns.InsertionHelper;

public class InsertSimpleSingleton implements IPatternImplType
{
	
	ISimpleSingletonInsertionMethod insertionMethod = null;
	
	InsertSimpleSingleton(ISimpleSingletonInsertionMethod insertionMethod)
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
		return "Simple (Readable but for non-thread safe, non-resource intensive objects only)";
	}
}
