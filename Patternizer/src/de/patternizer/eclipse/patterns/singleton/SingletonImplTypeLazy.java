package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * This class uses an instance of a {@link SingletonInsertMethod} subclass to
 * insert the Lazy initialization implementation of the singleton pattern into
 * source.
 * 
 * @author Alexander Kalinowski
 *
 */
public class SingletonImplTypeLazy extends SingletonImplType
{
	
	// FIELDS
	public static int PRIORITY = 4;
	public static final String DESCRIPTION = "Lazy Initialization (Use for more resource intensive but non-thread safe objects)";
	
	
	
	// CONSTRUCTORS
	public SingletonImplTypeLazy(SingletonInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	// METHODS
	@Override
	/**
	 * This method conducts the Lazy initialization singleton insertion process by
	 * calling the appropriate methods on a {@link SingletonInsertMethod} subclass
	 * in the appropriate order.
	 */
	public void execute(PatternConfigData configData, InsertionHelper insertionHelper)
	{
		if (!(configData instanceof SingletonConfigData)) return;
		SingletonConfigData sConfigData = (SingletonConfigData) configData;
		insertionMethod.privatizeConstructorsInAST(insertionHelper);
		insertionMethod.addSingletonFieldToAST(insertionHelper, sConfigData);
		insertionMethod.addCreateInstanceMethodToAST(insertionHelper, sConfigData);
	}
	
	
	
}
