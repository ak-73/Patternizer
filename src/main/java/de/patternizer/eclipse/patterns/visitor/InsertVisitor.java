package de.patternizer.eclipse.patterns.visitor;


import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.SrcCodeModMethodModifying;;


/**
 * Responsible for:
 * <ul>
 * <li>supplying Visitor-specific pattern insertion config data,</li>
 * <li>supplying the Visitor-specific config page plugin for configuring the
 * above data; and,</li>
 * <li>supplying the appropriate Visitor pattern implementations and a
 * {@link VisitorInsertMethod}-implementing class that is responsible for
 * making the actual changes to file.
 * </ul>
 *
 *
 * @author Alexander Kalinowski
 *
 */
public class InsertVisitor extends InsertPattern
{
	
	// FIELDS
	private static Logger logger = LoggerFactory.getLogger(InsertVisitor.class);
	
	
	
	// CONSTRUCTORS
	public InsertVisitor(ExecutionEvent event, IWorkbenchWindow window, String patternName)
	{
		super(event, window, patternName);
		this.patternName = patternName;
	}
	
	
	
	// METHODS (FACTORY METHODS)
	/**
	 * Supplies the Visitor-specific pattern insertion config data.
	 * 
	 * @return an instance of {@link VisitorConfigData}
	 */
	@Override
	public PatternConfigData createConfigData(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations)
	{
		logger.info("Insert " + patternName + " pattern has been evoked.");
		VisitorConfigData configData = new VisitorConfigData();
		configData.setImplTypeList(patternImplementations);
		configData.setInsertionHelper(insertionHelper);
		List<ICompilationUnit> units = ASTManipulationHelper.getAllOpenICUsInProject(configData.getInsertionHelper());
		configData.setUnits(units);
		configData.setPatternSpecificModMethod(new SrcCodeModMethodModifying());
		return configData;		
	}
	
	
	/**
	 * Supplies the Visitor-specific config page plugin used for configuring
	 * {@link VisitorConfigData}.
	 */
	@Override
	public PatternConfigPagePlugin createPatternConfigPagePlugin()
	{			
		return new VisitorConfigPagePlugin();
	}
	
	
	/**
	 * Supplies an appropriate Visitor pattern implementation which is configured
	 * with an {@link VisitorInsertMethod}-implementing class responsible for
	 * making the actual changes to file.
	 * 
	 * @param implTypeClass {@code Class} instance representing the
	 *                      {@link PatternImplType} subclass which the caller
	 *                      expects to determine the type of <b>implTypeClass</b>
	 */
	@Override
	public PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass)
	{
		if (implTypeClass.equals(VisitorImplType.class)) throw new IllegalArgumentException(
				"Erroneously attempting to make factory method createPatternImplType() create an instance of the abstract base class "
						+ VisitorImplType.class.getSimpleName() + ".");
		
		if (implTypeClass.equals(VisitorImplTypeSimple.class)) return new VisitorImplTypeSimple(new VisitorInsertMethodProgrammatically());
		
		throw new IllegalArgumentException("Unknown pattern implementation type " + implTypeClass.getSimpleName()
				+ ". Make sure that the appropriate createPatternImplType() method is aware of it.");
	}
	
}
