package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;

/**
 * This class uses an instance of a {@link SingletonInsertMethod} subclass to
 * insert the Initialization-on-demand holder implementation of the singleton
 * pattern into source.
 * 
 * @author Alexander Kalinowski
 *
 */
public class SingletonImplTypeHolder extends SingletonImplType
{
	// FIELDS
	public static int PRIORITY = 8;
	public static final String DESCRIPTION = "Initalization-On-Demand Holder (Thread safe and lean.)";
	
	
	
	// CONSTRUCTORS
	public SingletonImplTypeHolder(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	// METHODS
	@Override
	/**
	 * This method conducts the Initialization-on-demand holder singleton insertion
	 * process by calling the appropriate methods on a {@link SingletonInsertMethod}
	 * subclass in the appropriate order.
	 */
	public void execute(PatternConfigData configData, InsertionDataDefault insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addHolderClassToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	
	
}
