package de.patternizer.eclipse.patterns.builder;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.singleton.SingletonInsertMethod;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeHolder;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeLazy;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSimple;

public class BuilderConfigData extends PatternConfigData
{
	private String singletonInstanceIdentifier;


	public BuilderConfigData()
	{
	}
	

	
	public boolean isSimpleInsertion()
	{
		Class<? extends PatternImplType> currentlySelectedImplTypeClass = getCurrentlySelectedImplTypeClass();		
		return currentlySelectedImplTypeClass.getSimpleName().equals("SingletonImplTypeSimple");
	}
	

	
	public boolean isLazyInitInsertion()
	{
		Class<? extends PatternImplType> currentlySelectedImplTypeClass = getCurrentlySelectedImplTypeClass();		
		return currentlySelectedImplTypeClass.getSimpleName().equals("SingletonImplTypeLazy");
	}
	

	
	public boolean isSyncInsertion()
	{
		Class<? extends PatternImplType> currentlySelectedImplTypeClass = getCurrentlySelectedImplTypeClass();		
		return currentlySelectedImplTypeClass.getSimpleName().equals("SingletonImplTypeSync");
	}
	

	
	public boolean isHolderInsertion()
	{
		Class<? extends PatternImplType> currentlySelectedImplTypeClass = getCurrentlySelectedImplTypeClass();		
		return currentlySelectedImplTypeClass.getSimpleName().equals("SingletonImplTypeHolder");
	}
	
	
	public String getSingletonInstanceIdentifier()
	{
		return singletonInstanceIdentifier;
	}
	
	public void setSingletonInstanceIdentifier(String singletonInstanceIdentifier)
	{
		this.singletonInstanceIdentifier = singletonInstanceIdentifier;
	}
	

}