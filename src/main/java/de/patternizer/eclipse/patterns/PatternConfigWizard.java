package de.patternizer.eclipse.patterns;

import org.eclipse.jface.wizard.Wizard;

/**
 * Top class responsible for handling the pattern insertion config dialog. Has
 * by default only one wizard page, an instance of {@link PatternConfigPage}.
 * Does nothing special except passing on config data to the
 * {@code PatternConfigPage}.
 * 
 * @author Alexander Kalinowski
 *
 */
public class PatternConfigWizard extends Wizard
{
	
	// FIELDS
	private PatternConfigPage patternConfigPage = null;
	private PatternConfigPagePlugin patternConfigPagePlugin = null;
	private PatternConfigData patternConfigData = null;
	private String patternName = "";
	
	
	
	
	
	// METHODS
	@Override
	public void addPages()
	{
		patternConfigPage = new PatternConfigPage(patternConfigData, patternConfigPagePlugin);
		patternConfigPage.setPatternName(patternName);
		addPage(patternConfigPage);
	}
	
	@Override
	public boolean performFinish()
	{
		return true;
	}
	
	
	
	
	
	// GETTERS & SETTERS
	/**
	 * Plain getter.
	 * @return
	 */
	public PatternConfigData getPatternConfigData()
	{
		return patternConfigData;
	}
	
	/**
	 * Plain setter.
	 * @param patternConfigData
	 */
	public void setPatternConfigData(PatternConfigData patternConfigData)
	{
		this.patternConfigData = patternConfigData;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public PatternConfigPagePlugin getPatternConfigPagePlugin()
	{
		return patternConfigPagePlugin;
	}
	
	/**
	 * Plain setter.
	 * @param patternConfigPagePlugin
	 */
	public void setPatternConfigPagePlugin(PatternConfigPagePlugin patternConfigPagePlugin)
	{
		this.patternConfigPagePlugin = patternConfigPagePlugin;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public String getPatternName()
	{
		return patternName;
	}
	
	/**
	 * Plain setter.
	 * @param patternName
	 */
	public void setPatternName(String patternName)
	{
		this.patternName = patternName;
	}
}
