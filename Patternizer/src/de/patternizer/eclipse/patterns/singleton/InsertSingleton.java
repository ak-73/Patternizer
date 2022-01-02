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
 * <li>managing the Pattern Insertion dialog,</li>
 * <li>applying the appropriate pattern implementation (aka variant of the
 * pattern) to AST; and,</li>
 * <li>writing the AST changes made to file.</li>
 * </ul>
 *
 *
 * @author Alexander Kalinowski
 *
 */
public class InsertSingleton extends InsertPattern
{
	
	//FIELDS
	private static Logger logger = LoggerFactory.getLogger(InsertSingleton.class);
	
	
	
	//CONSTRUCTORS
	public InsertSingleton(IWorkbenchWindow window, String patternName)
	{
		super(window);
		this.patternName = patternName;
	}
	
	
	
	//METHODS (FACTORY METHODS)	
	@Override
	public PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass)
	{
		if (implTypeClass.equals(SingletonImplType.class)) throw new IllegalArgumentException("Erroneously attempting to make factory method createPatternImplType() create an instance of the abstract base class " + SingletonImplType.class.getSimpleName() + ".");
				
		if (implTypeClass.equals(SingletonImplTypeSimple.class))  return new SingletonImplTypeSimple( new SingletonInsertMethodProgrammatically());
		else if (implTypeClass.equals(SingletonImplTypeLazy.class))  return new SingletonImplTypeLazy( new SingletonInsertMethodProgrammatically()); 
		else if (implTypeClass.equals(SingletonImplTypeSync.class))  return new SingletonImplTypeSync( new SingletonInsertMethodProgrammatically());
		else if (implTypeClass.equals(SingletonImplTypeHolder.class))  return new SingletonImplTypeHolder( new SingletonInsertMethodProgrammatically());

		throw new IllegalArgumentException("Unknown pattern implementation type " + implTypeClass.getSimpleName() + ". Make sure that the appropriate createPatternImplType() method is aware of it.");
	}
	
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
	
	@Override
	public PatternConfigPagePlugin createPatternConfigPagePlugin()
	{
		return new SingletonConfigPagePlugin();
	}

}
