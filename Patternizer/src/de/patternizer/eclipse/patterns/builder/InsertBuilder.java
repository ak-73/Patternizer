package de.patternizer.eclipse.patterns.builder;

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
 * <li>supplying Builder-specific pattern insertion config data,</li>
 * <li>supplying the Builder-specific config page plugin for configuring the
 * above data; and,</li>
 * <li>supplying the appropriate Builder pattern implementations and a
 * {@link BuilderInsertMethod}-implementing class that is responsible for making
 * the actual changes to file.
 * </ul>
 *
 *
 * @author Alexander Kalinowski
 *
 */
public class InsertBuilder extends InsertPattern
{
	
	// FIELDS
	private static Logger logger = LoggerFactory.getLogger(InsertBuilder.class);
	
	
	
	// CONSTRUCTORS
	public InsertBuilder(IWorkbenchWindow window, String patternName)
	{
		super(window);
		this.patternName = patternName;
	}
	
	
	
	// METHODS (FACTORY METHODS)
	/**
	 * Supplies the Builder-specific pattern insertion config data.
	 * 
	 * @return an instance of {@link BuilderConfigData}
	 */
	@Override
	public PatternConfigData createConfigData(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations)
	{
		logger.info("Insert " + patternName + " pattern has been evoked.");
		BuilderConfigData configData = new BuilderConfigData();
		configData.setBuilderClassIdentifier("Builder");
		configData.setBuildMethodIdentifier("build");
		configData.setImplTypeList(patternImplementations);
		return configData;
	}
	
	
	/**
	 * Supplies the Builder-specific config page plugin used for configuring
	 * {@link BuilderConfigData}.
	 */
	@Override
	public PatternConfigPagePlugin createPatternConfigPagePlugin()
	{
		return new BuilderConfigPagePlugin();
	}
	
	
	/**
	 * Supplies an appropriate Builder pattern implementation which is configured
	 * with an {@link BuilderInsertMethod}-implementing class responsible for
	 * making the actual changes to file.
	 * 
	 * @param implTypeClass {@code Class} instance representing the
	 *                      {@link PatternImplType} subclass which the caller
	 *                      expects to determine the type of <b>implTypeClass</b>
	 */
	@Override
	public PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass)
	{
		if (implTypeClass.equals(BuilderImplType.class)) throw new IllegalArgumentException(
				"Erroneously attempting to make factory method createPatternImplType() create an instance of the abstract base class "
						+ BuilderImplType.class.getSimpleName() + ".");
		
		if (implTypeClass.equals(BuilderImplTypeSimple.class)) return new BuilderImplTypeSimple(new BuilderInsertMethodProgrammatically());
		
		throw new IllegalArgumentException("Unknown pattern implementation type " + implTypeClass.getSimpleName()
				+ ". Make sure that the appropriate createPatternImplType() method is aware of it.");
	}	
	
}

