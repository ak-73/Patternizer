package de.patternizer.eclipse.patterns.singleton;

import de.patternizer.eclipse.patterns.IPatternConfigData;
import de.patternizer.eclipse.patterns.IPatternImplType;

public class SingletonConfigData implements IPatternConfigData
{
	private String singletonInstanceIdentifier;
	private boolean simpleInsertion = true;
	private boolean lazyInitInsertion = false;
	private boolean syncInsertion = false;
	private boolean holderInsertion = false;
	
	private boolean isSimpleInsertionSupported = true;
	private boolean isLazyInsertionSupported = true;
	private boolean isSyncInsertionSupported = true;
	private boolean isHolderInsertionSupported = true;
	
	public boolean isSimpleInsertionSupported()
	{
		return isSimpleInsertionSupported;
	}
	
	public void setSimpleInsertionSupported(boolean isSimpleInsertionSupported)
	{
		this.isSimpleInsertionSupported = isSimpleInsertionSupported;
	}
	
	public boolean isLazyInsertionSupported()
	{
		return isLazyInsertionSupported;
	}
	
	public void setLazyInsertionSupported(boolean isLazyInsertionSupported)
	{
		this.isLazyInsertionSupported = isLazyInsertionSupported;
	}
	
	public boolean isSyncInsertionSupported()
	{
		return isSyncInsertionSupported;
	}
	
	public void setSyncInsertionSupported(boolean isSyncInsertionSupported)
	{
		this.isSyncInsertionSupported = isSyncInsertionSupported;
	}
	
	public boolean isHolderInsertionSupported()
	{
		return isHolderInsertionSupported;
	}
	
	public void setHolderInsertionSupported(boolean isOnDemandInsertionSupported)
	{
		this.isHolderInsertionSupported = isOnDemandInsertionSupported;
	}
	
	public boolean isSimpleInsertion()
	{
		return simpleInsertion;
	}
	
	public void setSimpleInsertion(boolean simpleInsertion)
	{
		this.simpleInsertion = simpleInsertion;
	}
	
	public boolean isLazyInitInsertion()
	{
		return lazyInitInsertion;
	}
	
	public void setLazyInitInsertion(boolean lazyInitInsertion)
	{
		this.lazyInitInsertion = lazyInitInsertion;
	}
	
	public boolean isSyncInsertion()
	{
		return syncInsertion;
	}
	
	public void setSyncInsertion(boolean syncInsertion)
	{
		this.syncInsertion = syncInsertion;
	}
	
	public boolean isHolderInsertion()
	{
		return holderInsertion;
	}
	
	public void setHolderInsertion(boolean onDemandInsertion)
	{
		this.holderInsertion = onDemandInsertion;
	}
	
	public String getSingletonInstanceIdentifier()
	{
		return singletonInstanceIdentifier;
	}
	
	public void setSingletonInstanceIdentifier(String singletonInstanceIdentifier)
	{
		this.singletonInstanceIdentifier = singletonInstanceIdentifier;
	}
	
	public IPatternImplType getSingletonImplType()
	{
		if (simpleInsertion)
		{
			InsertSingletonProgrammatically progInsertion = new InsertSingletonProgrammatically();
			return new InsertSimpleSingleton(progInsertion);
			
		}
		else if (lazyInitInsertion) //it's 95% the same as Simple Impl
		{
			InsertSingletonProgrammatically progInsertion = new InsertSingletonProgrammatically();
			return new InsertLazySingleton(progInsertion);
			
		}
		else if (syncInsertion) //it's 95% the same as Simple Impl
		{
			InsertSingletonProgrammatically progInsertion = new InsertSingletonProgrammatically();
			return new InsertSimpleSingleton(progInsertion);
			
		}
		else if (holderInsertion) 
		{
			InsertSingletonProgrammatically progInsertion = new InsertSingletonProgrammatically();
			return new InsertHolderSingleton(progInsertion);
			
		}
		return null;
	}
	
}
