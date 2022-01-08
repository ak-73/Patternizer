package de.patternizer.eclipse.patterns.builder;

import de.patternizer.eclipse.patterns.PatternConfigData;

/**
 * Builder-specific config data that adds fields for
 * <ul>
 * <li>naming the Builder object,</li>
 * <li>naming the build method; and,</li>
 * <li>tracking the state of checkboxes used to optionally remove certain
 * members from the class we're applying the Builder pattern to.</li>
 * </ul>
 * 
 * @author Alexander Kalinowski
 *
 */
public class BuilderConfigData extends PatternConfigData
{
	
	// FIELDS
	private String builderClassIdentifier;
	private String buildMethodIdentifier;
	
	private boolean initializersRemoving;
	private boolean constructorsRemoving;
	private boolean gettersRemoving;
	private boolean settersRemoving;
	private boolean otherMethodsRemoving;
	
	
	
	// CONSTRUCTORS
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
	
	
	// GETTERS & SETTERS
	/**
	 * Plain getter.
	 * @return
	 */
	public String getBuilderClassIdentifier()
	{
		return builderClassIdentifier;
	}
	
	/**
	 * Plain setter.
	 * @param builderClassIdentifier
	 */
	public void setBuilderClassIdentifier(String builderClassIdentifier)
	{
		this.builderClassIdentifier = builderClassIdentifier;
	}
	

	/**
	 * Plain getter.
	 * @return
	 */
	public String getBuildMethodIdentifier()
	{
		return buildMethodIdentifier;
	}
	
	/**
	 * Plain setter.
	 * @param buildMethodIdentifier
	 */
	public void setBuildMethodIdentifier(String buildMethodIdentifier)
	{
		this.buildMethodIdentifier = buildMethodIdentifier;
	}
	

	/**
	 * Plain getter.
	 * @return
	 */
	public boolean isInitializersRemoving()
	{
		return initializersRemoving;
	}
	
	/**
	 * Plain setter.
	 * @param initializersRemoving
	 */
	public void setInitializersRemoving(boolean initializersRemoving)
	{
		this.initializersRemoving = initializersRemoving;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public boolean isConstructorsRemoving()
	{
		return constructorsRemoving;
	}
	
	/**
	 * Plain setter.
	 * @param constructorsRemoving
	 */
	public void setConstructorsRemoving(boolean constructorsRemoving)
	{
		this.constructorsRemoving = constructorsRemoving;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public boolean isGettersRemoving()
	{
		return gettersRemoving;
	}
	
	/**
	 * Plain setter.
	 * @param gettersRemoving
	 */
	public void setGettersRemoving(boolean gettersRemoving)
	{
		this.gettersRemoving = gettersRemoving;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public boolean isSettersRemoving()
	{
		return settersRemoving;
	}
	
	/**
	 * Plain setter.
	 * @param settersRemoving
	 */
	public void setSettersRemoving(boolean settersRemoving)
	{
		this.settersRemoving = settersRemoving;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public boolean isOtherMethodsRemoving()
	{
		return otherMethodsRemoving;
	}
	
	/**
	 * Plain setter.
	 * @param otherMethodsRemoving
	 */
	public void setOtherMethodsRemoving(boolean otherMethodsRemoving)
	{
		this.otherMethodsRemoving = otherMethodsRemoving;
	}
}
