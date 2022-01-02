package de.patternizer.eclipse.patterns;

import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

public abstract class PatternImplType
{
	//CONSTANTS
	public static final int PRIORITY = 1000;
	public static final String DESCRIPTION = "ERROR!";
	
	
	//FIELDS
	//protected PatternInsertMethod insertionMethod = null;
	protected PatternConfigPagePlugin patternConfigPageHandler = null;			
	
	
	
	
	
	//ABSTRACT METHODS
	public abstract void execute(PatternConfigData configData, InsertionHelper insertionHelper);
	
	
	
	
	//METHODS
	public String getDescription()
	{
		return DESCRIPTION;
	}
	
	public int getPriority()
	{
		return PRIORITY;
	}
		
	
	
	//GETTERS & SETTERS
	public PatternConfigPagePlugin getPatternConfigPageHandler()
	{
		return patternConfigPageHandler;
	}

	public void setPatternConfigPageHandler(PatternConfigPagePlugin patternConfigPageHandler)
	{
		this.patternConfigPageHandler = patternConfigPageHandler;
	}
		
}
