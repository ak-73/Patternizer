package de.patternizer.eclipse.patterns.singleton;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SingletonConfigPage extends WizardPage
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
	
	private Text singletonIdentifierText;
	private Button radioButtonSimple;
	private Button radioButtonLazyInit;
	private Button radioButtonSynchronized;
	private Button radioButtonInitOnDemandHolder;
	private Text textHolderIdentifier;
	private Label lblHolderIdentifier;
	
	public String getSingletonIdentifier()
	{
		return singletonIdentifierText.getText();
	}
	
	protected SingletonConfigPage()
	{
		super("Singleton pattern Code Configuration");
		setTitle("Personal Information");
		setDescription("Please enter your personal information");
	}
	
	@Override
	public void createControl(Composite parent)
	{
		
		setTitle("Singleton Pattern Code Configuration");
		setDescription("Configure the generated code here.");
		
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 6));
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setText("Implementation Type:");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Label lblSingletonObjectIdentifier = new Label(composite, SWT.NONE);
		lblSingletonObjectIdentifier.setText("Singleton object identifier:");
		
		singletonIdentifierText = new Text(composite, SWT.BORDER);
		singletonIdentifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		singletonIdentifierText.setText("_______singletonObject");
		singletonIdentifierText.setSelection(0, 100);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		
		radioButtonSimple = new Button(group, SWT.RADIO);
		radioButtonSimple.setSelection(true);
		radioButtonSimple.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{}
		});
		radioButtonSimple.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		radioButtonSimple.setText("Simple (Readable but for non-thread safe, non-resource intensive objects only)");
		new Label(group, SWT.NONE);
		
		radioButtonLazyInit = new Button(group, SWT.RADIO);
		radioButtonLazyInit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		radioButtonLazyInit.setText("Lazy Initialization (Use for more resource intensive but non-thread safe objects)");
		new Label(group, SWT.NONE);
		
		radioButtonSynchronized = new Button(group, SWT.RADIO);
		radioButtonSynchronized.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		radioButtonSynchronized.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{}
		});
		radioButtonSynchronized.setText("Synchronized (Readable and thread safe but big overhead. Do not use if time-critical!)");
		new Label(group, SWT.NONE);
		
		radioButtonInitOnDemandHolder = new Button(group, SWT.RADIO);
		radioButtonInitOnDemandHolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		radioButtonInitOnDemandHolder.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{}
		});
		radioButtonInitOnDemandHolder.setText("Initalization-On-Demand Holder (Thread safe and lean.)");
		new Label(group, SWT.NONE);
		
		lblHolderIdentifier = new Label(composite, SWT.NONE);
		lblHolderIdentifier.setEnabled(false);
		lblHolderIdentifier.setText("Holder class identifier:");
		
		textHolderIdentifier = new Text(composite, SWT.BORDER);
		textHolderIdentifier.setEnabled(false);
		textHolderIdentifier.setText("LazyHolder");
		textHolderIdentifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		initMyDataBindings();
		
	}
	
	// method renamed to protect it from Eclipse Bindings tab manipulations?
	protected DataBindingContext initMyDataBindings()
	{
		DataBindingContext m_bindingContext = new DataBindingContext();
		//
		var observeSelectionRadioButtonSimpleObserveWidget = WidgetProperties.buttonSelection().observe(radioButtonSimple);
		var simpleInsertionSingletonConfigDataObserveValue = PojoProperties.value("simpleInsertion").observe(singletonConfigData);
		m_bindingContext.bindValue(observeSelectionRadioButtonSimpleObserveWidget, simpleInsertionSingletonConfigDataObserveValue, null, null);
		//
		var observeSelectionRadioButtonLazyInitObserveWidget = WidgetProperties.buttonSelection().observe(radioButtonLazyInit);
		var lazyInitInsertionSingletonConfigDataObserveValue = PojoProperties.value("lazyInitInsertion").observe(singletonConfigData);
		m_bindingContext.bindValue(observeSelectionRadioButtonLazyInitObserveWidget, lazyInitInsertionSingletonConfigDataObserveValue, null, null);
		//
		var observeSelectionRadioButtonSynchronizedObserveWidget = WidgetProperties.buttonSelection().observe(radioButtonSynchronized);
		var syncInsertionSingletonConfigDataObserveValue = PojoProperties.value("syncInsertion").observe(singletonConfigData);
		m_bindingContext.bindValue(observeSelectionRadioButtonSynchronizedObserveWidget, syncInsertionSingletonConfigDataObserveValue, null, null);
		//
		var observeSelectionRadioButtonInitOnDemandHolderObserveWidget = WidgetProperties.buttonSelection().observe(radioButtonInitOnDemandHolder);
		var holderInsertionSingletonConfigDataObserveValue = PojoProperties.value("holderInsertion").observe(singletonConfigData);
		m_bindingContext.bindValue(observeSelectionRadioButtonInitOnDemandHolderObserveWidget, holderInsertionSingletonConfigDataObserveValue, null, null);
		//
		var observeEnabledRadioButtonInitOnDemandHolderObserveWidget = WidgetProperties.enabled().observe(radioButtonInitOnDemandHolder);
		var holderInsertionSupportedSingletonConfigDataObserveValue = PojoProperties.value("holderInsertionSupported").observe(singletonConfigData);
		m_bindingContext.bindValue(observeEnabledRadioButtonInitOnDemandHolderObserveWidget, holderInsertionSupportedSingletonConfigDataObserveValue, null, null);
		//
		var observeEnabledRadioButtonSynchronizedObserveWidget = WidgetProperties.enabled().observe(radioButtonSynchronized);
		var syncInsertionSupportedSingletonConfigDataObserveValue = PojoProperties.value("syncInsertionSupported").observe(singletonConfigData);
		m_bindingContext.bindValue(observeEnabledRadioButtonSynchronizedObserveWidget, syncInsertionSupportedSingletonConfigDataObserveValue, null, null);
		//
		var observeEnabledRadioButtonLazyInitObserveWidget = WidgetProperties.enabled().observe(radioButtonLazyInit);
		var lazyInsertionSupportedSingletonConfigDataObserveValue = PojoProperties.value("lazyInsertionSupported").observe(singletonConfigData);
		m_bindingContext.bindValue(observeEnabledRadioButtonLazyInitObserveWidget, lazyInsertionSupportedSingletonConfigDataObserveValue, null, null);
		//
		var observeEnabledRadioButtonSimpleObserveWidget = WidgetProperties.enabled().observe(radioButtonSimple);
		var simpleInsertionSupportedSingletonConfigDataObserveValue = PojoProperties.value("simpleInsertionSupported").observe(singletonConfigData);
		m_bindingContext.bindValue(observeEnabledRadioButtonSimpleObserveWidget, simpleInsertionSupportedSingletonConfigDataObserveValue, null, null);
		//
		var observeTextSingletonIdentifierTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(singletonIdentifierText);
		var singletonInstanceIdentifierSingletonConfigDataObserveValue = PojoProperties.value("singletonInstanceIdentifier").observe(singletonConfigData);
		m_bindingContext.bindValue(observeTextSingletonIdentifierTextObserveWidget, singletonInstanceIdentifierSingletonConfigDataObserveValue, null, null);
		//
		var observeSelectionRadioButtonInitOnDemandHolderObserveWidget2 = WidgetProperties.buttonSelection().observe(radioButtonInitOnDemandHolder);
		var enabledTextHolderIdentifierObserveValue = PojoProperties.value("enabled").observe(textHolderIdentifier);
		m_bindingContext.bindValue(observeSelectionRadioButtonInitOnDemandHolderObserveWidget2, enabledTextHolderIdentifierObserveValue, null, null);
		//
		var observeSelectionRadioButtonInitOnDemandHolderObserveWidget_1 = WidgetProperties.buttonSelection().observe(radioButtonInitOnDemandHolder);
		var enabledLblHolderIdentifierObserveValue = PojoProperties.value("enabled").observe(lblHolderIdentifier);
		m_bindingContext.bindValue(observeSelectionRadioButtonInitOnDemandHolderObserveWidget_1, enabledLblHolderIdentifierObserveValue, null, null);
		//
		return m_bindingContext;
	}
}
