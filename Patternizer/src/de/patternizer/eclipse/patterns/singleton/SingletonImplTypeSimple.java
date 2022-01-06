package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * This class uses an instance of a {@link SingletonInsertMethod} subclass to
 * insert the Simple implementation of the singleton pattern into source.
 * 
 * @author Alexander Kalinowski
 *
 */
public class SingletonImplTypeSimple extends SingletonImplType
{
	
	// FIELDS
	public static int PRIORITY = 2;
	public static final String DESCRIPTION = "Simple (Readable but for non-thread safe, non-resource intensive objects only)";
	
	
	
	
	// CONSTRUCTORS
	public SingletonImplTypeSimple(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	// METHODS
	/**
	 * This method conducts the Simple Singleton pattern insertion process by
	 * calling the appropriate methods on a {@link SingletonInsertMethod} subclass
	 * in the appropriate order.
	 */
	@Override
	public void execute(PatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addSingletonFieldToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	
	
}
