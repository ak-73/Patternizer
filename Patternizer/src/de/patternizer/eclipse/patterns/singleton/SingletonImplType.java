package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternImplType;

public abstract class SingletonImplType extends PatternImplType
{
	protected SingletonInsertMethod insertionMethod = null;
	
	//CONSTRUCTORS
	public SingletonImplType(SingletonInsertMethod insertionMethod)
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
