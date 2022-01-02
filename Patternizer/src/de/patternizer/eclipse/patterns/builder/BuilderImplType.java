package de.patternizer.eclipse.patterns.builder;

import de.patternizer.eclipse.patterns.PatternImplType;

public abstract class BuilderImplType extends PatternImplType
{
	protected BuilderInsertMethod insertionMethod = null;
	
	//CONSTRUCTORS
	public BuilderImplType(BuilderInsertMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
	
	
	
	//METHODS
	public boolean isSimpleInsertion()
	{
		return false;
	}
	
	public boolean isLazyInitInsertion()
	{
		return false;
	}
	
	public boolean isSyncInsertion()
	{
		return false;
	}
	
	public boolean isHolderInsertion()
	{
		return false;
	}
}
