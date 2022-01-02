package de.patternizer.eclipse.patterns.builder;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;

public class BuilderConfigPagePlugin extends PatternConfigPagePlugin
{
	
	//FIELDS
	private Label lblBuilderClassIdentifier = null;
	private Text textBuilderClassIdentifier = null;
	private Label lblBuilderMethodIdentifier = null;
	private Text textBuilderMethodIdentifier = null;
	
	private Button btnInitializersRemoving = null;
	private Button btnConstructorsRemoving = null;
	private Button btnGettersRemoving = null;
	private Button btnSettersRemoving = null;
	private Button btnOtherMethodsRemoving = null;

	
	//CONSTRUCTORS
	public BuilderConfigPagePlugin()
	{
		super();		
	}
	
	
	
	
	//METHODS
	@Override
	public void initComponents(Composite parentComposite)
	{
		lblBuilderClassIdentifier = new Label(parentComposite, SWT.NONE);
		lblBuilderClassIdentifier.setText("Builder class identifier:");
		
		textBuilderClassIdentifier = new Text(parentComposite, SWT.BORDER);
		textBuilderClassIdentifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textBuilderClassIdentifier.setText("Builder");
		textBuilderClassIdentifier.setSelection(0, 100);
		
		lblBuilderMethodIdentifier = new Label(parentComposite, SWT.NONE);
		lblBuilderMethodIdentifier.setText("Builder method identifier:");
		
		textBuilderMethodIdentifier = new Text(parentComposite, SWT.BORDER);
		textBuilderMethodIdentifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textBuilderMethodIdentifier.setText("build");
		textBuilderMethodIdentifier.setSelection(0, 100);
		
		new Label(parentComposite, SWT.NONE);
		new Label(parentComposite, SWT.NONE);
		
		btnInitializersRemoving = new Button(parentComposite, SWT.CHECK);
		btnInitializersRemoving.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnInitializersRemoving.setText("Remove all initializers from all fields of the current class");
		
		btnConstructorsRemoving = new Button(parentComposite, SWT.CHECK);
		btnConstructorsRemoving.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnConstructorsRemoving.setText("Remove all constructors from the current class");
		
		btnGettersRemoving = new Button(parentComposite, SWT.CHECK);
		btnGettersRemoving.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGettersRemoving.setText("Remove all Getters from the current class");
		
		btnSettersRemoving = new Button(parentComposite, SWT.CHECK);
		btnSettersRemoving.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnSettersRemoving.setText("Remove all Setters from the current class");
		
		btnOtherMethodsRemoving = new Button(parentComposite, SWT.CHECK);
		btnOtherMethodsRemoving.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnOtherMethodsRemoving.setText("Caution! Remove all other Methods from the current class (not recommended)");
	}
	
	@Override
	protected void initDatabinding(DataBindingContext dataBindingContext, PatternConfigData configData)
	{
		var observeTextBuilderClassIdentifier = WidgetProperties.text(SWT.Modify).observe(textBuilderClassIdentifier);
		var builderClassIdentifierObserveValue = PojoProperties.value("builderClassIdentifier").observe(configData);
		dataBindingContext.bindValue(observeTextBuilderClassIdentifier, builderClassIdentifierObserveValue, null, null);	
		
		var observeTextBuilderMethodIdentifier = WidgetProperties.text(SWT.Modify).observe(textBuilderMethodIdentifier);
		var buildMethodIdentifierObserveValue = PojoProperties.value("buildMethodIdentifier").observe(configData);
		dataBindingContext.bindValue(observeTextBuilderMethodIdentifier, buildMethodIdentifierObserveValue, null, null);
		
		var observeBtnInitializersRemoving = WidgetProperties.buttonSelection().observe(btnInitializersRemoving);
		var initializersRemovingObserveValue = PojoProperties.value("initializersRemoving").observe(configData);
		dataBindingContext.bindValue(observeBtnInitializersRemoving, initializersRemovingObserveValue, null, null);
		
		var observeBtnConstructorsRemoving = WidgetProperties.buttonSelection().observe(btnConstructorsRemoving);
		var constructorsRemovingObserveValue = PojoProperties.value("constructorsRemoving").observe(configData);
		dataBindingContext.bindValue(observeBtnConstructorsRemoving, constructorsRemovingObserveValue, null, null);
		
		var observeBtnGettersRemoving = WidgetProperties.buttonSelection().observe(btnGettersRemoving);
		var gettersRemovingObserveValue = PojoProperties.value("gettersRemoving").observe(configData);
		dataBindingContext.bindValue(observeBtnGettersRemoving, gettersRemovingObserveValue, null, null);
		
		var observeBtnSettersRemoving = WidgetProperties.buttonSelection().observe(btnSettersRemoving);
		var settersRemovingObserveValue = PojoProperties.value("settersRemoving").observe(configData);
		dataBindingContext.bindValue(observeBtnSettersRemoving, settersRemovingObserveValue, null, null);
				
		var observeOtherMethodsRemoving = WidgetProperties.buttonSelection().observe(btnOtherMethodsRemoving);
		var otherMethodsRemovingbserveValue = PojoProperties.value("otherMethodsRemoving").observe(configData);
		dataBindingContext.bindValue(observeOtherMethodsRemoving, otherMethodsRemovingbserveValue, null, null);
	}
	

	@Override
	protected void cleanUpConfigPage(String previousCurrentTypeClassname)
	{
		switch(previousCurrentTypeClassname)
		{
			case "BuilderImplTypeSimple":
				lblBuilderClassIdentifier.setEnabled(false);
				textBuilderClassIdentifier.setEnabled(false);
				lblBuilderMethodIdentifier.setEnabled(false);
				textBuilderMethodIdentifier.setEnabled(false);
				btnInitializersRemoving.setEnabled(false);
				btnConstructorsRemoving.setEnabled(false);
				btnGettersRemoving.setEnabled(false);
				btnSettersRemoving.setEnabled(false);
				btnOtherMethodsRemoving.setEnabled(false);
				break;	
		}
	}
	
	@Override
	protected void setupConfigPage(String newCurrentTypeClassname)
	{
		switch(newCurrentTypeClassname)
		{
			case "BuilderImplTypeSimple":
				lblBuilderClassIdentifier.setEnabled(true);
				textBuilderClassIdentifier.setEnabled(true);
				lblBuilderMethodIdentifier.setEnabled(true);
				textBuilderMethodIdentifier.setEnabled(true);
				btnInitializersRemoving.setEnabled(true);
				btnConstructorsRemoving.setEnabled(true);
				btnGettersRemoving.setEnabled(true);
				btnSettersRemoving.setEnabled(true);
				btnOtherMethodsRemoving.setEnabled(true);				
				break;
		}
	}
		
}
