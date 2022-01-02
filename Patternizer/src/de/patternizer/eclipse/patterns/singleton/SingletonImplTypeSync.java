package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

public class SingletonImplTypeSync extends SingletonImplType
{
	
	//FIELDS
	public static int PRIORITY = 6;
	public static final String DESCRIPTION = "Synchronized (Readable and thread safe but big overhead. Do not use if time-critical!)";
	
	
	
	
	//CONSTRUCTORS
	public SingletonImplTypeSync(SingletonInsertMethod insertionMethod)
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
	public boolean isSyncInsertion()
	{
		return true;
	}	
}
