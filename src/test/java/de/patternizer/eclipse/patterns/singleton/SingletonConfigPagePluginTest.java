package de.patternizer.eclipse.patterns.singleton;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SingletonConfigPagePluginTest
{
	Shell shell = null;
	Group g = null;
	SingletonConfigPagePlugin plugin = null;
	
	
	
	@BeforeEach
	public void arrange()
	{
		shell = new Shell();	
		shell.setVisible(false);
		g = new Group(shell, SWT.NONE);
		plugin = new SingletonConfigPagePlugin();
		plugin.initComponents(g);
	}
	
	
	
	@Test
	void test_initComponents_WidgetsInitialized()
	{		
		Assertions.assertNotNull(plugin.lblSingletonObjectIdentifier);
		Assertions.assertNotNull(plugin.singletonIdentifierText);
		Assertions.assertNotNull(plugin.lblFactoryMethodIdentifier);
		Assertions.assertNotNull(plugin.factoryMethodIdentifierText);
		Assertions.assertNotNull(plugin.lblHolderIdentifier);
		Assertions.assertNotNull(plugin.textHolderIdentifier);			
	}
	
	//a single widget's content only, as testing for constants isn't very maintainable anyway
	@Test
	void test_initComponents_FactoryMethodIdentifierInitCorrectly()
	{
		Assertions.assertTrue(plugin.lblFactoryMethodIdentifier.getText().equals("Factory method identifier:"));				
	}
	
	
	@Test
	void test_InitDatabinding_SingletonIdentifierTextSuccessfullyBound()
	{
		DataBindingContext dataBindingContext = new DataBindingContext();
		SingletonConfigData configData = new SingletonConfigData();
		String defaultSingletonIdentifierValue = configData.getSingletonInstanceIdentifier();
		
		plugin.initDatabinding(dataBindingContext, configData);

		Assertions.assertTrue(plugin.singletonIdentifierText.getText().equals(configData.getSingletonInstanceIdentifier())); //confirm in sync (pre)										
		plugin.singletonIdentifierText.setText(plugin.singletonIdentifierText.getText() + "_foobar"); //change value of one side
		Assertions.assertTrue(configData.getSingletonInstanceIdentifier() != defaultSingletonIdentifierValue); //confirm changed
		Assertions.assertTrue(plugin.singletonIdentifierText.getText().equals(configData.getSingletonInstanceIdentifier())); //confirm in sync (post)
	}
	
	
	@Test
	void test_CleanUpConfigPage_SetupConfigPage_Toggle()
	{
		if(plugin.textHolderIdentifier.isEnabled())
		{
			plugin.cleanUpConfigPage("SingletonImplTypeHolder");
			Assertions.assertTrue(plugin.lblHolderIdentifier.isEnabled() == false);
			Assertions.assertTrue(plugin.textHolderIdentifier.isEnabled() == false);	
			
			plugin.setupConfigPage("SingletonImplTypeHolder");
			Assertions.assertTrue(plugin.lblHolderIdentifier.isEnabled() == true);
			Assertions.assertTrue(plugin.textHolderIdentifier.isEnabled() == true);	
		}
		else
		{
			plugin.setupConfigPage("SingletonImplTypeHolder");
			Assertions.assertTrue(plugin.lblHolderIdentifier.isEnabled() == true);
			Assertions.assertTrue(plugin.textHolderIdentifier.isEnabled() == true);
			
			plugin.cleanUpConfigPage("SingletonImplTypeHolder");
			Assertions.assertTrue(plugin.lblHolderIdentifier.isEnabled() == false);
			Assertions.assertTrue(plugin.textHolderIdentifier.isEnabled() == false);	
		}
	}	
}
