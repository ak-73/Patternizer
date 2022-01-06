package de.patternizer.eclipse.patterns;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import de.patternizer.eclipse.patterns.singleton.SingletonConfigData;
import de.patternizer.eclipse.patterns.singleton.SingletonConfigPagePlugin;

/**
 * Abstract base class for displaying optional widgets, depending on the current
 * user-selected pattern implementation, and binding them to an instance of a
 * {@link PatternConfigData} subclass appropriate for the user-selected pattern.
 * <p>
 * For example, if the user has currently selected the initialization-on-demand
 * holder implementation of the singleton pattern, the
 * {@link SingletonConfigPagePlugin} will display a {@code Text} widget for
 * adjusting the identifier of the holder class and bind it to a {@code String}
 * representation in a {@link SingletonConfigData} instance. This holder class
 * will then be accordingly named as it gets inserted into the class that is
 * meant to be made singleton.
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class PatternConfigPagePlugin
{
	
	// FIELDS
	private Composite parentComposite = null;
	
	
	
	
	// CONSTRUCTORS
	public PatternConfigPagePlugin()
	{
		super();
	}
	
	
	
	
	// ABSTRACT METHODS
	/**
	 * Abstract method responsible for initializing <i>all</i> optional widgets
	 * @param parentComposite
	 */
	protected abstract void initComponents(Composite parentComposite);
	
	/**
	 * Abstract method responsible for initializing data binding of <i>all</i> optional widgets
	 * @param dataBindingContext the data binding context
	 * @param configData an instance of an appropriate subclass of {@link PatternConfigData}
	 */
	protected abstract void initDatabinding(DataBindingContext dataBindingContext, PatternConfigData configData);
	
	/**
	 * Abstract method responsible for removing or hiding or disabling optional per-pattern implementation widgets 
	 * @param previouslySelectedTypeClassname the simple name of the {@code Class} whose associated widgets we need to clean up. For example, if we switch from lazy initialization singleton to synchronized singleton, we'll pass the name of the former's pattern implementation class, {@code "SingletonImplTypeLazy"} as a string 
	 */
	protected abstract void cleanUpConfigPage(String previouslySelectedTypeClassname);
	
	/**
	 * Abstract method responsible for inserting or displaying or enabling optional per-pattern implementation widgets 
	 * @param newlySelectedTypeClassname the simple name of the {@code Class} whose associated widgets we need to activate. For example, if we switch from lazy initialization singleton to synchronized singleton, we'll pass the name of the latter's pattern implementation class, {@code "SingletonImplTypeSync"} as a string
	 */
	protected abstract void setupConfigPage(String newlySelectedTypeClassname);
	
	
	
	
	
	// METHODS
	public Composite getParentComposite()
	{
		return parentComposite;
	}
	
	/**
	 * Sets the parentComposite and calls the abstract initialization methods {@link #initComponents(Composite)} and {@link #initDatabinding(DataBindingContext, PatternConfigData)}.
	 * @param parentComposite the parent composite, typically the {@link Composite} managed by a {@link PatternConfigPage} instance. 
	 * @param dataBindingContext the context for data binding, typically the {@link DataBindingContext} managed by a {@link PatternConfigPage} instance. 
	 * @param configData an instance of an appropriate subclass of {@link PatternConfigData}
	 */
	public void init(Composite parentComposite, DataBindingContext dataBindingContext, PatternConfigData configData)
	{
		this.parentComposite = parentComposite;
		initComponents(parentComposite);
		initDatabinding(dataBindingContext, configData);
	}
	
	/**
	 * Updates the pattern implementation-specific part of the config dialog, typically when the user has just selected a new pattern implementation.
	 * <p> Internally calls first {@link #cleanUpConfigPage(oldImpl)}, then {@link #setupConfigPage(newImpl)} to make the switch. 
	 * @param configData an instance of an appropriate subclass of {@link PatternConfigData}
	 * @param previouslySelectedTypeClassname  the simple name of the {@code Class} whose associated widgets we need to clean up. For example, if we switch from lazy initialization singleton to synchronized singleton, we'll pass the name of the former's pattern implementation class, {@code "SingletonImplTypeLazy"} as a string
	 */
	public void updateConfigPage(PatternConfigData configData, String previouslySelectedTypeClassname)
	{
		cleanUpConfigPage(previouslySelectedTypeClassname);
		setupConfigPage(configData.getSelectedImplTypeClassname());
	}
	
	
}
