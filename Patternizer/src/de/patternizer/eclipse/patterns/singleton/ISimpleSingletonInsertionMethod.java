package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.InsertionHelper;

public interface ISimpleSingletonInsertionMethod
{
	boolean isSimpleSingletonImplemented();
	
	void privatizeConstructorsInAST(InsertionHelper insertionHelper);
	
	boolean addSingletonFieldToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
	
	boolean addHolderClassToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
	
	boolean addCreateInstanceMethodToAST(InsertionHelper insertionHelper, SingletonConfigData configData);
}
