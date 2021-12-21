package de.patternizer.eclipse.patterns.singleton;

import org.eclipse.jface.wizard.Wizard;

public class SingletonConfigWizard extends Wizard
{
	
	private SingletonConfigData singletonConfigData;
	
	public SingletonConfigData getSingletonConfigData()
	{
		return singletonConfigData;
	}
	
	public void setSingletonConfigData(SingletonConfigData singletonConfigData)
	{
		this.singletonConfigData = singletonConfigData;
	}
	
	SingletonConfigPage singletonPage;
	
	@Override
	public void addPages()
	{
		singletonPage = new SingletonConfigPage();
		singletonPage.setSingletonConfigData(singletonConfigData);
		addPage(singletonPage);
	}
	
	@Override
	public boolean performFinish()
	{
		return true;
	}
	
}
