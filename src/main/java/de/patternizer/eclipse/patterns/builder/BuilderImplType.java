package de.patternizer.eclipse.patterns.builder;

import de.patternizer.eclipse.patterns.PatternImplType;

/**
 * Abstract base class for all Builder implementation inserters that ensures
 * only {@link BuilderInsertMethod} subclasses will be used for inserting the
 * Builder pattern to source. 
 * 
 * <p>This class follows the naming conventions outlined under
 * {@link PatternImplType}.
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class BuilderImplType extends PatternImplType
{
	protected BuilderInsertMethod insertionMethod = null;
	
	//CONSTRUCTORS
	public BuilderImplType(BuilderInsertMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
	
}
