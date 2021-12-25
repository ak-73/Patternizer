package de.patternizer.eclipse.patterns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
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

public class PatternConfigPage extends WizardPage
{	
	private DataBindingContext dataBindingContext = new DataBindingContext();		
	
	private List<Button> radioButtonsPatternImpl = new ArrayList<Button>();
	
	private PatternConfigData patternConfigData;
	private PatternConfigPagePlugin patternConfigPageHandler = null;
	private String patternName = "";
	
	protected PatternConfigPage()
	{
		super("Pattern Code Configuration");
	}
	

	
	
	public PatternConfigData getPatternConfigData()
	{
		return patternConfigData;
	}
	
	public void setPatternConfigData(PatternConfigData configData)
	{
		this.patternConfigData = configData;
	}
	
	private void initRadioButtons(Group group)
	{
		radioButtonsPatternImpl = new ArrayList<Button>();
		var implTypeList = PatternImplManager.enumPatternImpls(patternName);
				
		for (var implType : implTypeList)
		{
			String desc = PatternImplManager.getImplDescription(implType);
			
			Button radioButton = new Button(group, SWT.RADIO);
			radioButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{}
			});
			radioButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			radioButton.setText(desc);
			radioButtonsPatternImpl.add(radioButton);
			
			new Label(group, SWT.NONE);
		}
		Button selButton = radioButtonsPatternImpl.get(0);
		if (selButton != null) selButton.setSelection(true);
		
	}
	
	private void initRadioButtonsDataBinding(DataBindingContext bindingContext)
	{		
		SelectObservableValue<Integer> v = new SelectObservableValue<Integer>(Integer.class);
		 
		int index = 0;
		for (Button b : radioButtonsPatternImpl)
		{
			var observeSelectionRadioButtonWidget = WidgetProperties.buttonSelection().observe(b);			
			v.addOption(index, observeSelectionRadioButtonWidget);	
			index++;
		}
		
		var temp = PojoProperties.value("selectedImplTypeIndex").observe(patternConfigData);
		bindingContext.bindValue(v, temp);
			
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
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);		
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		initRadioButtons(group);
		initRadioButtonsDataBinding(dataBindingContext);	
				
		patternConfigPageHandler.init(composite, dataBindingContext, patternConfigData);
	}
	

	public PatternConfigPagePlugin getPatternConfigPageHandler()
	{
		return patternConfigPageHandler;
	}


	public void setPatternConfigPageHandler(PatternConfigPagePlugin patternConfigPageHandler)
	{
		this.patternConfigPageHandler = patternConfigPageHandler;
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
