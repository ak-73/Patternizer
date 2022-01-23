package de.patternizer.eclipse.patterns.visitor;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;

/**
 * This class uses an instance of a {@link VisitorInsertMethod} subclass to
 * insert a simple implementation of the Visitor pattern into source.
 * 
 * @author Alexander Kalinowski
 *
 */
public class VisitorImplTypeSimple extends VisitorImplType
{
	
	// FIELDS
	public static int PRIORITY = 2;
	public static final String DESCRIPTION = "Simple";
	
	
	
	
	// CONSTRUCTORS
	public VisitorImplTypeSimple(VisitorInsertMethod insertionMethod)
	{
		super(insertionMethod);
	}
	
	
	
	
	// METHODS
	/**
	 * This method conducts the Simple Visitor pattern insertion process by calling
	 * the appropriate methods on a {@link VisitorInsertMethod} subclass in the
	 * appropriate order.
	 */
	@Override
	public void execute(PatternConfigData configData, InsertionDataDefault insertionHelper)
	{
		if (!(configData instanceof VisitorConfigData)) return;
		VisitorConfigData vConfigData = (VisitorConfigData) configData;		
		
		insertionMethod.insertVisitorAcceptHierarchical(insertionHelper, vConfigData);
	}
}
