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

import de.patternizer.eclipse.patterns.helpers.PatternImplManager;

public class PatternConfigPage extends WizardPage
{
	
	// FIELDS
	private DataBindingContext dataBindingContext = new DataBindingContext();
	private List<Button> radioButtonsPatternImplList = new ArrayList<Button>();
	private PatternConfigData patternConfigData;
	private PatternConfigPagePlugin patternConfigPagePlugin = null;
	private String patternName = "";
	
	// CONSTRUCTORS
	protected PatternConfigPage()
	{
		super("Pattern Code Configuration");
	}
	
	// MAIN METHODS
	@Override
	public void createControl(Composite parent)
	{
		setTitle(patternName + " Pattern Code Configuration");
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
		
		
		patternConfigPagePlugin.init(composite, dataBindingContext, patternConfigData);
	}
	
	/**
	 * Create a radio button per enumerated pattern implementation and add it to the
	 * group. Make the first radio button in the list selected.
	 * <p> Only displayed if there's at least 2 pattern implementations available.
	 * 
	 * @param group the SWT {@code Group} object that will manage the group of radio buttons
	 */
	private void initRadioButtons(Group group)
	{					
		var implTypeList = PatternImplManager.getPatternImplTypeListByPattern(patternName);
		if (implTypeList.size() < 2)
		{ 
			group.dispose();
			return;
		}
		
		radioButtonsPatternImplList = new ArrayList<Button>();
		
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
			radioButtonsPatternImplList.add(radioButton);
			
			new Label(group, SWT.NONE);
		}
		Button selButton = radioButtonsPatternImplList.get(0);
		if (selButton != null) selButton.setSelection(true);
				
		initRadioButtonsDataBinding();
	}
	
	/**
	 * Bind the selected radio button to a 0-based index into the list of pattern implementations. 
	 * @param bindingContext
	 */
	private void initRadioButtonsDataBinding()
	{
		SelectObservableValue<Integer> v = new SelectObservableValue<Integer>(Integer.class);
		
		int index = 0;
		for (Button b : radioButtonsPatternImplList)
		{
			var observeSelectionRadioButtonWidget = WidgetProperties.buttonSelection().observe(b);
			v.addOption(index, observeSelectionRadioButtonWidget);
			index++;
		}
		
		var temp = PojoProperties.value("selectedImplTypeIndex").observe(patternConfigData);
		dataBindingContext.bindValue(v, temp);
		
	}
	
	// GETTERS & SETTERS
	public PatternConfigPagePlugin getPatternConfigPageHandler()
	{
		return patternConfigPagePlugin;
	}
	
	public void setPatternConfigPageHandler(PatternConfigPagePlugin patternConfigPageHandler)
	{
		this.patternConfigPagePlugin = patternConfigPageHandler;
	}
	
	public String getPatternName()
	{
		return patternName;
	}
	
	public void setPatternName(String patternName)
	{
		this.patternName = patternName;
	}
	
	public PatternConfigData getPatternConfigData()
	{
		return patternConfigData;
	}
	
	public void setPatternConfigData(PatternConfigData configData)
	{
		this.patternConfigData = configData;
	}
}
