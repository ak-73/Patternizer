package de.patternizer.eclipse.patterns;

import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * Abstract base class for all pattern implementation inserters.
 * <p>
 * Since we're using reflection for self-registering pattern implementations,
 * all subclasses need to follow strict naming conventions in order to get
 * located. Therefore, any subclasses MUST be named:
 * <p>
 * {@code <Pattern>ImplType<Typename>}
 * <p>
 * For example, implementations of the singleton pattern are named
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSimple
 * SingletonImplTypeSimple},
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeLazy
 * SingletonImplTypeLazy},
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSync
 * SingletonImplTypeSync} and so on. A builder pattern implementation, on the
 * other hand, might be named
 * {@link de.patternizer.eclipse.patterns.builder.BuilderImplTypeSimple
 * BuilderImplTypeSimple}.
 * <p>
 * These subclasses are the classes that are central for structuring the
 * insertion process. For example, an initialization-on-demand holder
 * implementation of the singleton pattern might specify to
 * <ul> 
 * <li>first privatize all constructors, 
 * <li>then add a nested holder class; and finally,
 * <li> add a factory method that returns the single instance inside the holder class.
 * </ul>
 * The actual implementation of each of these steps, on the other hand, is the responsibility of an appropriate subclass of {@link PatternInsertMethod}.
 * 
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class PatternImplType
{
	
	// CONSTANTS
	public static final int PRIORITY = 1000;
	public static final String DESCRIPTION = "ERROR!";
	
	// FIELDS
	protected PatternConfigPagePlugin patternConfigPageHandler = null;
	
	
	
	
	
	
	// ABSTRACT METHODS
	/**
	 * Abstract method for structuring the insertion method as outlined in the class description.
	 * @param configData an instance of an appropriate subclass of {@link PatternConfigData}
	 * @param insertionHelper helper class
	 */
	public abstract void execute(PatternConfigData configData, InsertionHelper insertionHelper);
	
	
	
	
	
	
	// METHODS
	/**
	 * Returns a description to be displayed in the config dialog to the user for pattern implementation selection.
	 * @return a description to be displayed in the config dialog to the user for pattern implementation selection.
	 */
	public String getDescription()
	{
		return DESCRIPTION;
	}
	
	/**
	 * Returns an int-based priority value, used for ordering the available pattern implementations when displaying them as options to the user
	 * @return an int-based priority value, used for ordering the available pattern implementations when displaying them as options to the user
	 */
	public int getPriority()
	{
		return PRIORITY;
	}
	
	
	
	
	
	
	// GETTERS & SETTERS
	/**
	 * Plain getter.
	 * @return
	 */
	public PatternConfigPagePlugin getPatternConfigPageHandler()
	{
		return patternConfigPageHandler;
	}
	
	/**
	 * Plain setter.
	 * @param patternConfigPageHandler
	 */
	public void setPatternConfigPageHandler(PatternConfigPagePlugin patternConfigPageHandler)
	{
		this.patternConfigPageHandler = patternConfigPageHandler;
	}
	
	
	
}
