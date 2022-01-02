package de.patternizer.eclipse.patterns;

import org.eclipse.jface.wizard.Wizard;

public class PatternConfigWizard extends Wizard
{
	//FIELDS
	private PatternConfigPage patternConfigPage = null;
	private PatternConfigPagePlugin patternConfigPagePlugin = null;
	private PatternConfigData patternConfigData = null;	
	private String patternName = "";

	
	
	
	//METHODS
	@Override
	public void addPages()
	{
		patternConfigPage = new PatternConfigPage();
		patternConfigPage.setPatternConfigData(patternConfigData);
		patternConfigPage.setPatternConfigPageHandler(patternConfigPagePlugin);
		patternConfigPage.setPatternName(patternName);
		addPage(patternConfigPage);
	}
	
	@Override
	public boolean performFinish()
	{
		return true;
	}
	
	
	
	

	//GETTERS & SETTERS
	public PatternConfigData getPatternConfigData()
	{
		return patternConfigData;
	}
	
	public void setPatternConfigData(PatternConfigData singletonConfigData)
	{
		this.patternConfigData = singletonConfigData;
	}
	
	public PatternConfigPagePlugin getPatternConfigPageHandler()
	{
		return patternConfigPagePlugin;
	}

	public void setPatternConfigPageHandler(PatternConfigPagePlugin patternConfigPagePlugin)
	{
		this.patternConfigPagePlugin = patternConfigPagePlugin;
	}

	public String getPatternName()
	{
		return patternName;
	}

	public void setPatternName(String patternName)
	{
		this.patternName = patternName;
	}
}
