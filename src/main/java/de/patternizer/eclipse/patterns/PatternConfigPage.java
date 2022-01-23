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

/**
 * Primary page of the pattern insertion config dialog.
 * 
 * @author Alexander Kalinowski
 * @see PatternConfigWizard
 */
public class PatternConfigPage extends WizardPage
{
	
	// FIELDS
	private DataBindingContext dataBindingContext = new DataBindingContext();
	private List<Button> radioButtonsPatternImplList = new ArrayList<Button>();
	private PatternConfigData patternConfigData;
	private PatternConfigPagePlugin patternConfigPagePlugin = null;
	private String patternName = "";	
	
	
	
	// CONSTRUCTORS
	protected PatternConfigPage(PatternConfigData patternConfigData, PatternConfigPagePlugin patternConfigPagePlugin)
	{
		super("Pattern Code Configuration");
		
		setPatternConfigData(patternConfigData);
		patternConfigPagePlugin.setParentConfigPage(this);
		patternConfigPagePlugin.setPatternConfigData(patternConfigData);
		setPatternConfigPagePlugin(patternConfigPagePlugin);
	}
	
	
	
	
	
	// MAIN METHODS
	/**
	 * Sets up the dialog page (including any {@link PatternConfigPagePlugin
	 * optional widgets} for the initially selected pattern implementation) and
	 * initializes data binding to a previously passed in {@link PatternConfigData}
	 * subclass instance
	 */
	@Override
	public void createControl(Composite parent)
	{
		setTitle(patternName + " Pattern Code Configuration");
		setDescription("Configure the generated code here.");
		
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		
		var implTypeList = PatternImplManager.enumPatternImplTypeListByPattern(patternName);
		if (implTypeList.size() >= 2)
		{
			Group group = new Group(composite, SWT.NONE);
			group.setLayout(new GridLayout(3, false));
			group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 6));
			
			Label lblNewLabel = new Label(group, SWT.NONE);
			lblNewLabel.setText("Implementation Type:");
			new Label(group, SWT.NONE);
			new Label(group, SWT.NONE);
			new Label(composite, SWT.NONE);
			new Label(composite, SWT.NONE);
			
			initRadioButtons(group, implTypeList);
		}
		
		
		
		
		patternConfigPagePlugin.init(composite, dataBindingContext, patternConfigData);
	}
	
	/**
	 * Create a radio button per enumerated pattern implementation and add it to the
	 * group. Make the first radio button in the list selected. Finally, bind the
	 * widgets to data in a previously passed in {@link PatternConfigData} subclass
	 * instance
	 * <p>
	 * Only displayed if there's at least 2 pattern implementations available as
	 * otherwise there is no choice for the user.
	 * 
	 * @param group the SWT {@code Group} object that will manage the group of radio
	 *              buttons
	 */
	private void initRadioButtons(Group group, List<Class<? extends PatternImplType>> implTypeList)
	{				
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
	 * Bind the selected radio button to a 0-based index into the list of pattern
	 * implementations.
	 * 
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
	/**
	 * Plain getter.
	 * @return
	 */
	public PatternConfigPagePlugin getPatternConfigPagePlugin()
	{
		return patternConfigPagePlugin;
	}
	
	/**
	 * Plain setter.
	 * @param patternConfigPagePlugin
	 */	
	void setPatternConfigPagePlugin(PatternConfigPagePlugin patternConfigPagePlugin)
	{
		this.patternConfigPagePlugin = patternConfigPagePlugin;
	}
	
	
	/**
	 * Plain getter.
	 * @return
	 */
	public String getPatternName()
	{
		return patternName;
	}
	
	/**
	 * Plain setter.
	 * @param patternName
	 */
	public void setPatternName(String patternName)
	{
		this.patternName = patternName;
	}
	
	/**
	 * Plain getter.
	 * @return
	 */
	public PatternConfigData getPatternConfigData()
	{
		return patternConfigData;
	}
	
	/**
	 * Plain setter.
	 * @param configData
	 */
	void setPatternConfigData(PatternConfigData configData)
	{
		this.patternConfigData = configData;
	}
	
	
	
}
