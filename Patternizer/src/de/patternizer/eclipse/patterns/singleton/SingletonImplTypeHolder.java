package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

public class SingletonImplTypeHolder extends SingletonImplType
{
	//FIELDS
	public static int PRIORITY = 8;
	public static final String DESCRIPTION = "Initalization-On-Demand Holder (Thread safe and lean.)";
	
	
	
	//CONSTRUCTORS
	public SingletonImplTypeHolder(SingletonInsertMethod insertionMethod)
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
		insertionMethod.addHolderClassToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
		
	
	public boolean isHolderInsertion()
	{
		return true;
	}
}
