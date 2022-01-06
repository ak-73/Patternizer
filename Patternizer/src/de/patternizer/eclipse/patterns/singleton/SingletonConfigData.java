package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;

/**
 * Singleton-specific config data that adds fields for
 * <ul>
 * <li>naming the singleton object; and,</li>
 * <li>optionally naming any holder class object.</li>
 * </ul>
 * 
 * @author Alexander Kalinowski
 *
 */
public class SingletonConfigData extends PatternConfigData
{
	
	//FIELDS
	private String singletonInstanceIdentifier;
	private String factoryMethodIdentifier;
	private String holderClassIdentifier;


	
	//CONSTRUCTORS
	public SingletonConfigData()
	{
		singletonInstanceIdentifier = "_______singletonInstance";
		factoryMethodIdentifier = "_______getInstance";
		holderClassIdentifier = "LazyHolder";
	}
	

	//GETTERS & SETTERS
	/**
	 * Plain getter.
	 * @return
	 */
	public String getSingletonInstanceIdentifier()
	{
		return singletonInstanceIdentifier;
	}
	
	/**
	 * Plain setter.
	 * @param singletonInstanceIdentifier
	 */
	public void setSingletonInstanceIdentifier(String singletonInstanceIdentifier)
	{
		this.singletonInstanceIdentifier = singletonInstanceIdentifier;
	}

	/**
	 * Plain getter.
	 * @return
	 */	
	public String getFactoryMethodIdentifier()
	{
		return factoryMethodIdentifier;
	}

	/**
	 * Plain setter.
	 * @param factoryMethodIdentifier
	 */
	public void setFactoryMethodIdentifier(String factoryMethodIdentifier)
	{
		this.factoryMethodIdentifier = factoryMethodIdentifier;
	}	

	/**
	 * Plain getter.
	 * @return
	 */
	public String getHolderClassIdentifier()
	{
		return holderClassIdentifier;
	}

	/**
	 * Plain setter.
	 * @param holderClassIdentifier
	 */
	public void setHolderClassIdentifier(String holderClassIdentifier)
	{
		this.holderClassIdentifier = holderClassIdentifier;
	}




}
