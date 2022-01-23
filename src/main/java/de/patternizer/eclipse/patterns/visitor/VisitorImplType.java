package de.patternizer.eclipse.patterns.visitor;

import de.patternizer.eclipse.patterns.PatternImplType;

/**
 * Abstract base class for all Visitor implementation inserters that ensures
 * only {@link VisitorInsertMethod} subclasses will be used for inserting the
 * Visitor pattern to source. 
 * 
 * <p>This class follows the naming conventions outlined under
 * {@link PatternImplType}.
 * 
 * @author Alexander Kalinowski
 *
 */
public abstract class VisitorImplType extends PatternImplType
{
	protected VisitorInsertMethod insertionMethod = null;
	
	//CONSTRUCTORS
	public VisitorImplType(VisitorInsertMethod insertionMethod)
	{
		this.insertionMethod = insertionMethod;
	}
	
}
