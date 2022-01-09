package de.patternizer.eclipse.patterns.singleton;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;

/**
 * This class handles the widgets responsible for configuration of any
 * singleton-specific config data, such as naming the singleton instance or any
 * nested holder class.
 * 
 * @author Alexander Kalinowski
 *
 */
public class SingletonConfigPagePlugin extends PatternConfigPagePlugin
{
	
	// FIELDS (package private for unit testing)
	Label lblSingletonObjectIdentifier = null;	
	Text singletonIdentifierText = null;
	Label lblFactoryMethodIdentifier = null;
	Text factoryMethodIdentifierText = null;
	Label lblHolderIdentifier = null;
	Text textHolderIdentifier = null;
	
	
	
	// CONSTRUCTORS
	public SingletonConfigPagePlugin()
	{
		super();
	}
	
	
	
	
	// METHODS	
	@Override
	public void initComponents(Composite parentComposite)
	{
		lblSingletonObjectIdentifier = new Label(parentComposite, SWT.NONE);
		lblSingletonObjectIdentifier.setText("Singleton object identifier:");
		
		singletonIdentifierText = new Text(parentComposite, SWT.BORDER);
		singletonIdentifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		singletonIdentifierText.setText("_______singletonInstance");
		singletonIdentifierText.setSelection(0, 100);
		
		
		lblFactoryMethodIdentifier = new Label(parentComposite, SWT.NONE);
		lblFactoryMethodIdentifier.setText("Factory method identifier:");
		
		factoryMethodIdentifierText = new Text(parentComposite, SWT.BORDER);
		factoryMethodIdentifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		factoryMethodIdentifierText.setText("_______getInstance");
		factoryMethodIdentifierText.setSelection(0, 100);
		
		
		lblHolderIdentifier = new Label(parentComposite, SWT.NONE);
		lblHolderIdentifier.setEnabled(false);
		lblHolderIdentifier.setText("Holder class identifier:");
		
		textHolderIdentifier = new Text(parentComposite, SWT.BORDER);
		textHolderIdentifier.setEnabled(false);
		textHolderIdentifier.setText("LazyHolder");
		textHolderIdentifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	@Override
	protected void initDatabinding(DataBindingContext dataBindingContext, PatternConfigData configData)
	{
		var observeTextSingletonIdentifierTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(singletonIdentifierText);
		var singletonInstanceIdentifierSingletonConfigDataObserveValue = PojoProperties.value("singletonInstanceIdentifier").observe(configData);
		dataBindingContext.bindValue(observeTextSingletonIdentifierTextObserveWidget, singletonInstanceIdentifierSingletonConfigDataObserveValue, null, null);
		
		var observeTextFactoryMethodIdentifierObserveWidget = WidgetProperties.text(SWT.Modify).observe(factoryMethodIdentifierText);
		var factoryMethodIdentifierSingletonConfigDataObserveValue = PojoProperties.value("factoryMethodIdentifier").observe(configData);
		dataBindingContext.bindValue(observeTextFactoryMethodIdentifierObserveWidget, factoryMethodIdentifierSingletonConfigDataObserveValue, null, null);
		
		var observeTextHolderIdentifierObserveWidget = WidgetProperties.text(SWT.Modify).observe(textHolderIdentifier);
		var holderClassIdentifierSingletonConfigDataObserveValue = PojoProperties.value("holderClassIdentifier").observe(configData);
		dataBindingContext.bindValue(observeTextHolderIdentifierObserveWidget, holderClassIdentifierSingletonConfigDataObserveValue, null, null);
	}
	
	
	@Override
	protected void cleanUpConfigPage(String previouslySelectedTypeClassname)
	{
		switch (previouslySelectedTypeClassname)
		{
			case "SingletonImplTypeHolder":
				lblHolderIdentifier.setEnabled(false);
				textHolderIdentifier.setEnabled(false);
				break;
		}
	}
	
	@Override
	protected void setupConfigPage(String newlySelectedTypeClassname)
	{
		switch (newlySelectedTypeClassname)
		{
			case "SingletonImplTypeHolder":
				lblHolderIdentifier.setEnabled(true);
				textHolderIdentifier.setEnabled(true);
				break;
		}
	}
	
}
