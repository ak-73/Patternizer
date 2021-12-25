package de.patternizer.eclipse.patterns;

import de.patternizer.eclipse.patterns.singleton.SingletonInsertMethod;

public abstract class PatternImplType
{
	public static final int PRIORITY = 1000;
	public static final String DESCRIPTION = "ERROR!";
	
	protected SingletonInsertMethod insertionMethod = null;
	protected PatternConfigPagePlugin patternConfigPageHandler = null;			
	
	
	public PatternImplType(SingletonInsertMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
		
	public String getDescription()
	{
		return DESCRIPTION;
	}
	
	public int getPriority()
	{
		return PRIORITY;
	}
		
	
	public PatternConfigPagePlugin getPatternConfigPageHandler()
	{
		return patternConfigPageHandler;
	}


	public void setPatternConfigPageHandler(PatternConfigPagePlugin patternConfigPageHandler)
	{
		this.patternConfigPageHandler = patternConfigPageHandler;
	}
	
	public abstract void execute(PatternConfigData configData, InsertionHelper insertionHelper);
}
