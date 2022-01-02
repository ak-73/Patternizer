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


public class SingletonConfigPagePlugin extends PatternConfigPagePlugin
{
	
	//FIELDS
	private Label lblSingletonObjectIdentifier = null;
	private Text singletonIdentifierText = null;
	private Label lblHolderIdentifier = null;
	private Text textHolderIdentifier = null;
	

	
	//CONSTRUCTORS
	public SingletonConfigPagePlugin()
	{
		super();		
	}
	
	
	
	
	//METHODS
	@Override
	public void initComponents(Composite parentComposite)
	{
		lblSingletonObjectIdentifier = new Label(parentComposite, SWT.NONE);
		lblSingletonObjectIdentifier.setText("Singleton object identifier:");
		
		singletonIdentifierText = new Text(parentComposite, SWT.BORDER);
		singletonIdentifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		singletonIdentifierText.setText("_______singletonInstance");
		singletonIdentifierText.setSelection(0, 100);
		
		
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
		
		var observeTextHolderIdentifierObserveWidget = WidgetProperties.text(SWT.Modify).observe(textHolderIdentifier);
		var holderClassIdentifierSingletonConfigDataObserveValue = PojoProperties.value("holderClassIdentifier").observe(configData);
		dataBindingContext.bindValue(observeTextHolderIdentifierObserveWidget, holderClassIdentifierSingletonConfigDataObserveValue, null, null);	
	}
	

	@Override
	protected void cleanUpConfigPage(String previousCurrentTypeClassname)
	{
		switch(previousCurrentTypeClassname)
		{
			case "SingletonImplTypeHolder":
				lblHolderIdentifier.setEnabled(false);
				textHolderIdentifier.setEnabled(false);
				break;	
		}
	}
	
	@Override
	protected void setupConfigPage(String newCurrentTypeClassname)
	{
		switch(newCurrentTypeClassname)
		{
			case "SingletonImplTypeHolder":
				lblHolderIdentifier.setEnabled(true);
				textHolderIdentifier.setEnabled(true);
				break;
		}
	}
	
}
