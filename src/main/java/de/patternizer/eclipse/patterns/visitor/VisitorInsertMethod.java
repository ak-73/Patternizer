package de.patternizer.eclipse.patterns.visitor;

import de.patternizer.eclipse.patterns.PatternInsertMethod;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;

/**
 * This interface defines all the methods that need to be implemented in order
 * to satisfy the demands of all Visitor pattern implementations (subclasses
 * of {@link VisitorImplType}. Implementing classes therefore may differ <i>by
 * which means</i> they make the required changes to source.
 * 
 * @author Alexander Kalinowski
 *
 */
public interface VisitorInsertMethod extends PatternInsertMethod
{
	public void insertVisitorAcceptHierarchical(InsertionDataDefault insertionHelper, VisitorConfigData configData);
}
