package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;

public class SingletonConfigData extends PatternConfigData
{
	
	//FIELDS
	private String singletonInstanceIdentifier;
	private String holderClassIdentifier;


	
	//CONSTRUCTORS
	public SingletonConfigData()
	{
		singletonInstanceIdentifier = "_______singletonInstance";
		holderClassIdentifier = "LazyHolder";
	}
	

	//GETTERS & SETTERS
	public String getSingletonInstanceIdentifier()
	{
		return singletonInstanceIdentifier;
	}
	
	public void setSingletonInstanceIdentifier(String singletonInstanceIdentifier)
	{
		this.singletonInstanceIdentifier = singletonInstanceIdentifier;
	}

	
	public String getHolderClassIdentifier()
	{
		return holderClassIdentifier;
	}

	public void setHolderClassIdentifier(String holderClassIdentifier)
	{
		this.holderClassIdentifier = holderClassIdentifier;
	}	

}
