package de.patternizer.eclipse.patterns.builder;

import de.patternizer.eclipse.patterns.PatternConfigData;

public class BuilderConfigData extends PatternConfigData
{
	
	//FIELDS
	private String builderClassIdentifier;
	private String buildMethodIdentifier;
	
	private boolean initializersRemoving;
	private boolean constructorsRemoving;
	private boolean gettersRemoving;
	private boolean settersRemoving;
	private boolean otherMethodsRemoving;


	
	//CONSTRUCTORS
	public BuilderConfigData()
	{
		builderClassIdentifier = "Builder";
		buildMethodIdentifier = "build";
		setInitializersRemoving(true);
		setConstructorsRemoving(false);
		setGettersRemoving(false);
		setSettersRemoving(true);
		setOtherMethodsRemoving(true);
	}
	

	//GETTERS & SETTERS
	public String getBuilderClassIdentifier()
	{
		return builderClassIdentifier;
	}


	public void setBuilderClassIdentifier(String builderClassIdentifier)
	{
		this.builderClassIdentifier = builderClassIdentifier;
	}


	public String getBuildMethodIdentifier()
	{
		return buildMethodIdentifier;
	}


	public void setBuildMethodIdentifier(String buildMethodIdentifier)
	{
		this.buildMethodIdentifier = buildMethodIdentifier;
	}


	public boolean isInitializersRemoving()
	{
		return initializersRemoving;
	}


	public void setInitializersRemoving(boolean initializersRemoving)
	{
		this.initializersRemoving = initializersRemoving;
	}


	public boolean isConstructorsRemoving()
	{
		return constructorsRemoving;
	}


	public void setConstructorsRemoving(boolean constructorsRemoving)
	{
		this.constructorsRemoving = constructorsRemoving;
	}


	public boolean isGettersRemoving()
	{
		return gettersRemoving;
	}


	public void setGettersRemoving(boolean gettersRemoving)
	{
		this.gettersRemoving = gettersRemoving;
	}


	public boolean isSettersRemoving()
	{
		return settersRemoving;
	}


	public void setSettersRemoving(boolean settersRemoving)
	{
		this.settersRemoving = settersRemoving;
	}


	public boolean isOtherMethodsRemoving()
	{
		return otherMethodsRemoving;
	}


	public void setOtherMethodsRemoving(boolean otherMethodsRemoving)
	{
		this.otherMethodsRemoving = otherMethodsRemoving;
	}	
}