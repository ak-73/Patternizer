package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternInsertMethod;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

/**
 * This interface defines all the methods that need to be implemented in order
 * to satisfy the demands of all singleton pattern implementations (subclasses
 * of {@link SingletonImplType}. Implementing classes therefore may differ <i>by
 * which means</i> they make the required changes to source.
 * 
 * @author Alexander Kalinowski
 *
 */
public interface SingletonInsertMethod extends PatternInsertMethod
{
	/**
	 * Turn all existing constructors in source private.
	 * @param insertionHelper
	 */
	void privatizeConstructorsInAST(InsertionHelper insertionHelper);
	
	/**
	 * Add a static private ("singleton") field to source.
	 * @param insertionHelper
	 * @param configData
	 * @return
	 */
	boolean addSingletonFieldToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
	
	/**
	 * Add a private static holder class that contains a private static field ("singleton") to source.
	 * @param insertionHelper
	 * @param configData
	 * @return
	 */
	boolean addHolderClassToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
	
	/**
	 * Add a public factory method to source that hands out the singleton object to client code. 
	 * @param insertionHelper
	 * @param configData
	 * @return
	 */
	boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
}
