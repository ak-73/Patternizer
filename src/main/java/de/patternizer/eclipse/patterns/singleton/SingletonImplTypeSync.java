package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;

/**
 * This class uses an instance of a {@link SingletonInsertMethod} subclass to
 * insert the Synchronized implementation of the singleton pattern into source.
 * 
 * @author Alexander Kalinowski
 *
 */
public class SingletonImplTypeSync extends SingletonImplType
{
	
	// FIELDS
	public static int PRIORITY = 6;
	public static final String DESCRIPTION = "Synchronized (Readable and thread safe but big overhead. Do not use if time-critical!)";
	
	
	
	
	// CONSTRUCTORS
	public SingletonImplTypeSync(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	// METHODS
	@Override
	/**
	 * This method conducts the synchronized singleton insertion process by calling
	 * the appropriate methods on a {@link SingletonInsertMethod} subclass in the
	 * appropriate order.
	 */
	public void execute(PatternConfigData configData, InsertionDataDefault insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addSingletonFieldToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	
}
