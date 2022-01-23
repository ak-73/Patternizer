package de.patternizer.eclipse.patterns.singleton;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;
import de.patternizer.eclipse.patterns.PatternImplType;

/**
 * Responsible for:
 * <ul>
 * <li>supplying singleton-specific pattern insertion config data,</li>
 * <li>supplying the singleton-specific config page plugin for configuring the
 * above data; and,</li>
 * <li>supplying the appropriate singleton pattern implementations (eg, Simple,
 * Lazy Initialization, Synchronized, Initialization-on-demand holder) and a
 * {@link SingletonInsertMethod}-implementing class that is responsible for
 * making the actual changes to file.
 * </ul>
 *
 *
 * @author Alexander Kalinowski
 *
 */
public class InsertSingleton extends InsertPattern
{
	
	// FIELDS
	private static Logger logger = LoggerFactory.getLogger(InsertSingleton.class);
	
	
	
	// CONSTRUCTORS
	public InsertSingleton(ExecutionEvent event, IWorkbenchWindow window, String patternName)
	{
		super(event, window, patternName);
		this.patternName = patternName;
	}
	
	
	
	// METHODS (FACTORY METHODS)	
	/**
	 * Supplies the singleton-specific pattern insertion config data.
	 * 
	 * @return an instance of {@link SingletonConfigData}
	 */
	@Override
	public PatternConfigData createConfigData(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations)
	{
		logger.info("Insert " + patternName + " pattern has been evoked.");
		SingletonConfigData configData = new SingletonConfigData();
		configData.setSingletonInstanceIdentifier("_______singletonInstance");
		configData.setHolderClassIdentifier("LazyHolder");
		configData.setImplTypeList(patternImplementations);
		return configData;
	}
	
	

	/**
	 * Supplies the singleton-specific config page plugin used for configuring
	 * {@link SingletonConfigData}.
	 */
	@Override	
	public PatternConfigPagePlugin createPatternConfigPagePlugin()
	{
		return new SingletonConfigPagePlugin();
	}
	

	/**
	 * Supplies an appropriate singleton pattern implementation (eg, Simple, Lazy
	 * Initialization, Synchronized, Initialization-on-demand holder) which is
	 * configured with an {@link SingletonInsertMethod}-implementing class
	 * responsible for making the actual changes to file.
	 * 
	 * @param implTypeClass {@code Class} instance representing the {@link PatternImplType} subclass which the caller expects to determine the type of <b>implTypeClass</b>
	 */
	@Override	
	public PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass)
	{
		if (implTypeClass.equals(PatternImplType.class)) throw new IllegalArgumentException(
				"Erroneously attempting to make factory method createPatternImplType() create an instance of the abstract base class "
						+ PatternImplType.class.getSimpleName() + ".");
		if (implTypeClass.equals(SingletonImplType.class)) throw new IllegalArgumentException(
				"Erroneously attempting to make factory method createPatternImplType() create an instance of the abstract base class "
						+ SingletonImplType.class.getSimpleName() + ".");
		
		if (implTypeClass.equals(SingletonImplTypeSimple.class)) return new SingletonImplTypeSimple(new SingletonInsertMethodProgrammatically());
		else if (implTypeClass.equals(SingletonImplTypeLazy.class)) return new SingletonImplTypeLazy(new SingletonInsertMethodProgrammatically());
		else if (implTypeClass.equals(SingletonImplTypeSync.class)) return new SingletonImplTypeSync(new SingletonInsertMethodProgrammatically());
		else if (implTypeClass.equals(SingletonImplTypeHolder.class)) return new SingletonImplTypeHolder(new SingletonInsertMethodProgrammatically());
		
		throw new IllegalArgumentException("Unknown pattern implementation type " + implTypeClass.getSimpleName()
				+ ". Make sure that the appropriate createPatternImplType() method is aware of it.");
	}
	
	
}
