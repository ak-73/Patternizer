package de.patternizer.eclipse.patterns;

import java.util.ArrayList;
import java.util.List;

public abstract class PatternConfigData
{
	private int selectedImplTypeIndex = 0;
	private List<Class<? extends PatternImplType>> implTypeList = new ArrayList<Class<? extends PatternImplType>>();
	private PatternConfigPagePlugin patternConfigPagePlugin = null;
	
	public int getSelectedImplTypeIndex()
	{
		return selectedImplTypeIndex;
	}

	public void setSelectedImplTypeIndex(int selectedImplTypeIndex)
	{
		String oldTypeClassname = getCurrentlySelectedImplTypeClassname();
		this.selectedImplTypeIndex = selectedImplTypeIndex;
		if (patternConfigPagePlugin != null) patternConfigPagePlugin.updateConfigPage(this, oldTypeClassname);
	}

	public List<Class<? extends PatternImplType>> getImplTypeList()
	{
		return implTypeList;
	}

	public void setImplTypeList(List<Class<? extends PatternImplType>> implTypeList)
	{
		this.implTypeList = implTypeList;
		
		setSelectedImplTypeIndex(getSelectedImplTypeIndex());
	}

	
	public Class<? extends PatternImplType> getCurrentlySelectedImplTypeClass()
	{
		return implTypeList.get(selectedImplTypeIndex);
	}
	
	public String getCurrentlySelectedImplTypeClassname()
	{
		return (implTypeList.size() > 0) ? implTypeList.get(selectedImplTypeIndex).getSimpleName() : "";
	}

	public PatternConfigPagePlugin getPatternConfigPagePlugin()
	{
		return patternConfigPagePlugin;
	}

	public void setPatternConfigPagePlugin(PatternConfigPagePlugin patternConfigPagePlugin)
	{
		this.patternConfigPagePlugin = patternConfigPagePlugin;
	}
}
