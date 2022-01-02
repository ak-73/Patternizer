package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternInsertMethod;
import de.patternizer.eclipse.patterns.helpers.InsertionHelper;

public interface SingletonInsertMethod extends PatternInsertMethod
{
	void privatizeConstructorsInAST(InsertionHelper insertionHelper);
	
	boolean addSingletonFieldToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
	
	boolean addHolderClassToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
	
	boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
}
