package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternImplType;

public class SingletonConfigData extends PatternConfigData
{
	private String singletonInstanceIdentifier;
	private String holderClassIdentifier;


	public SingletonConfigData()
	{
		singletonInstanceIdentifier = "_______singletonInstance";
		holderClassIdentifier = "LazyHolder";
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



	public String getHolderClassIdentifier()
	{
		return holderClassIdentifier;
	}



	public void setHolderClassIdentifier(String holderClassIdentifier)
	{
		this.holderClassIdentifier = holderClassIdentifier;
	}
	

}
