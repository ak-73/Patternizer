package de.patternizer.eclipse.patterns;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

public abstract class PatternConfigPagePlugin
{
	
	//FIELDS
	private Composite parentComposite = null;

	
	
	
	//CONSTRUCTORS
	public PatternConfigPagePlugin()
	{
		super();
	}
	
	
	
	
	//ABSTRACT METHODS
	protected abstract void initComponents(Composite parentComposite);
	
	protected abstract void initDatabinding(DataBindingContext dataBindingContext, PatternConfigData configData);
		
	protected abstract void cleanUpConfigPage(String previousCurrentTypeClassname);
		
	protected abstract void setupConfigPage(String newCurrentTypeClassname);
	
	
	
	
	
	//METHODS
	public Composite getParentComposite()
	{
		return parentComposite;
	}

	public void init(Composite parentComposite, DataBindingContext dataBindingContext, PatternConfigData configData)
	{
		this.parentComposite = parentComposite;
		initComponents(parentComposite);
		initDatabinding(dataBindingContext, configData);
	}
	
	public void updateConfigPage(PatternConfigData patternConfigData, String previousCurrentTypeClassname)
	{
		cleanUpConfigPage(previousCurrentTypeClassname);
		setupConfigPage(patternConfigData.getSelectedImplTypeClassname());
	}
	

}
