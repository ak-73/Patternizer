package de.patternizer.eclipse.patterns;

import java.util.ArrayList;
import java.util.List;

import de.patternizer.eclipse.patterns.helpers.SrcCodeModMethod;

/**
 * An abstract base class for pattern-specific config data encapsulated in
 * subclasses. Significantly, it contains a 0-based index into the list of
 * available pattern implementation types. This index is bound to the current
 * selection of the corresponding radio button group in the
 * {@link PatternConfigPage} dialog, which is used for configuring pattern
 * insertion.
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class PatternConfigData
{
	// ENUMS
	/*
	 * public enum InsertResponsibility { INSERTPATTERNCLASS, CONCRETEPATTERN }
	 * private InsertResponsibility insertResponsibility =
	 * InsertResponsibility.INSERTPATTERNCLASS;
	 */
	
	// FIELDS
	private SrcCodeModMethod patternSpecificModMethod = null;
	private int selectedImplTypeIndex = 0;
	private List<Class<? extends PatternImplType>> implTypeList = new ArrayList<Class<? extends PatternImplType>>();
	private PatternConfigPagePlugin patternConfigPagePlugin = null;
	private PatternImplType selectedImplTypeInstance = null;
	
	
	
	
	// METHODS
	/**
	 * Get {@link Class} of the currently selected pattern implementation.
	 * 
	 * @return {@code Class} of the user-selected pattern implementation. This
	 *         {@code Class} must be a subclass of {@link PatternImplType}
	 */
	public Class<? extends PatternImplType> getSelectedImplTypeClass()
	{
		return implTypeList.get(selectedImplTypeIndex);
	}
	
	/**
	 * Get the simple name of the user-selected pattern implementation {@link Class}
	 * 
	 * @return {@code String} containing the simple name of the currently selected
	 *         pattern implementation {@link Class}. This {@code Class} must be a
	 *         subclass of {@link PatternImplType}
	 */
	public String getSelectedImplTypeClassname()
	{
		return (implTypeList.size() > 0) ? implTypeList.get(selectedImplTypeIndex).getSimpleName() : "";
	}
	
	
	
	
	// GETTERS & SETTERS
	/**
	 * Plain getter for the 0-based index into the list of available pattern
	 * implementation types, signifying the current user selection.
	 * 
	 * @return 0-based index into the list of available pattern implementation
	 *         types, signifying the current user selection
	 */
	public int getSelectedImplTypeIndex()
	{
		return selectedImplTypeIndex;
	}
	
	/**
	 * Setter for the 0-based index into the list of available pattern
	 * implementation types, signifying the current user selection.
	 * <p>
	 * IMPORTANT! This method is not a plain setter because whenever the current
	 * implementation type is changed by the user, the config dialog must be
	 * adjusted to take account for implementation type-specific config data.
	 * Therefore, this method calls
	 * {@link PatternConfigPagePlugin#updateConfigPage(PatternConfigData, String)
	 * PatternConfigPagePlugin#updateConfigPage()}} to trigger the necessary changes
	 * in the GUI.
	 * 
	 * @param selectedImplTypeIndex new value of 0-based index into the list of
	 *                              available pattern implementation types
	 */
	public void setSelectedImplTypeIndex(int selectedImplTypeIndex)
	{
		String oldTypeClassname = getSelectedImplTypeClassname();
		this.selectedImplTypeIndex = selectedImplTypeIndex;
		if (patternConfigPagePlugin != null) patternConfigPagePlugin.updateConfigPage(this, oldTypeClassname);
	}
	
	/**
	 * Setter for the list of enumerated implementation types for the current
	 * pattern.
	 * <p>
	 * Outside of data binding, this method should only be called on freshly
	 * constructed PatternConfigData objects, as it triggers an update of the config
	 * dialog, displaying pattern implementation-specific options.
	 * <p>
	 * Also, the method will manipulate the {@link #getSelectedImplTypeIndex()
	 * current selection index} if necessary to keep it within legal bounds.
	 * 
	 * @param implTypeList new list of enumerated implementation types for the
	 *                     current pattern. Must not be null nor empty.
	 */
	public void setImplTypeList(List<Class<? extends PatternImplType>> implTypeList)
	{
		if (implTypeList == null) throw new IllegalArgumentException("setImplTypeList() may not be passed null.");
		this.implTypeList = implTypeList;
		
		// we also need to ensure that any widgets associated with the currently
		// selected implementation type (see PatternConfigPagePlugin and subclasses) are
		// unhidden/enabled when the dialog is opened - a call to
		// setSelectedImplTypeIndex()
		// ensures that, as it in turn calls setupConfigPage()
		int currentIndex = getSelectedImplTypeIndex(); // for sanity checking
		if (currentIndex < 0) currentIndex = 0;
		if (currentIndex >= implTypeList.size()) currentIndex = implTypeList.size() - 1;
		setSelectedImplTypeIndex(getSelectedImplTypeIndex());
	}
	
	/**
	 * Plain setter for the implementation type-specific config page plugin.
	 * 
	 * @param patternConfigPagePlugin new implementation type-specific config page
	 *                                plugin
	 */
	public void setPatternConfigPagePlugin(PatternConfigPagePlugin patternConfigPagePlugin)
	{
		this.patternConfigPagePlugin = patternConfigPagePlugin;
	}
	
	/**
	 * Plain getter for the encapsulated instance of the currently selected
	 * implementation type
	 * 
	 * @return encapsulated instance of the currently selected implementation type
	 */
	public PatternImplType getSelectedImplTypeInstance()
	{
		return selectedImplTypeInstance;
	}
	
	/**
	 * Plain setter for the encapsulated instance of the currently selected
	 * implementation type
	 * 
	 * @param selectedImplTypeInstance new instance of the currently selected
	 *                                 implementation type. Must not be
	 *                                 {@code null}.
	 */
	public void setSelectedImplTypeInstance(PatternImplType selectedImplTypeInstance)
	{
		if (!(selectedImplTypeInstance.getClass().equals(getSelectedImplTypeClass())))
			throw new IllegalStateException("The class of selectedImplTypeInstance does not match the class of getSelectedImplTypeClass()!");
		this.selectedImplTypeInstance = selectedImplTypeInstance;
	}
	
	
	/**
	 * Plain getter for the way modifications should be recorded and inserted.
	 * {@code null} means that the {@link InsertPattern} class is responsible (this
	 * should be the default if you're only modifying the currently active file).
	 * Any other value encapsulates a method for recording and a method for writing
	 * via an implementing class of the
	 * {@link de.patternizer.eclipse.patterns.helpers.SrcCodeModMethod SrcCodeModMethod}
	 * interface.
	 * 
	 * @return a {@link de.patternizer.eclipse.patterns.helpers.SrcCodeModMethod SrcCodeModMethod} implementing class
	 */
	public SrcCodeModMethod getPatternSpecificModMethod()
	{
		return patternSpecificModMethod;
	}
	
	/**
	 * Plain setter for the way modifications should be recorded and inserted.
	 * {@code null} means that the {@link InsertPattern} class is responsible (this
	 * should be the default if you're only modifying the currently active file).
	 * Any other value encapsulates a method for recording and a method for writing
	 * via an implementing class of the
	 * {@link de.patternizer.eclipse.patterns.helpers.SrcCodeModMethod SrcCodeModMethod}
	 * interface.
	 * 
	 * @param patternSpecificModMethod  a {@link de.patternizer.eclipse.patterns.helpers.SrcCodeModMethod SrcCodeModMethod} implementing class
	 */
	public void setPatternSpecificModMethod(SrcCodeModMethod patternSpecificModMethod)
	{
		this.patternSpecificModMethod = patternSpecificModMethod;
	}
	
}
