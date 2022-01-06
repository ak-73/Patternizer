package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternImplType;

/**
 * Abstract base class for all singleton implementation inserters that ensures
 * only {@link SingletonInsertMethod} subclasses will be used for inserting the
 * singleton pattern to source. 
 * 
 * <p>This class follows the naming conventions outlined under
 * {@link PatternImplType}.
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class SingletonImplType extends PatternImplType
{
	protected SingletonInsertMethod insertionMethod = null;
	
	// CONSTRUCTORS
	public SingletonImplType(SingletonInsertMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
	
	
	
	
}
