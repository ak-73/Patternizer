package de.patternizer.eclipse.patterns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.ui.IWorkbenchWindow;
import org.reflections.Reflections;

public class PatternImplManager implements Comparator<Class<? extends PatternImplType>>
{
	public static final String BASEPACKAGE = "de.patternizer.eclipse.patterns";
	private static Map<String, List<Class<? extends PatternImplType>>> pattermImpListsByPatternName = new TreeMap<String, List<Class<? extends PatternImplType>>>();
	
	
	public static List<Class<? extends PatternImplType>> init(String patternName)
	{
		if (pattermImpListsByPatternName.containsKey(patternName)) return pattermImpListsByPatternName.get(patternName);
		
		Reflections reflections = new Reflections(BASEPACKAGE + "." + patternName.toLowerCase());
		var implSet = reflections.getSubTypesOf(PatternImplType.class);
		
		// TODO: research if this is the best way to handle the formatting of fluent
		// APIs in eclipse without affecting everything else
		//@formatter:off
		var implList = 	implSet.stream()
						.sorted(new PatternImplManager())
						.collect(Collectors.toList());
		//@formatter:on
		
		pattermImpListsByPatternName.put(patternName, implList);
		return implList;
		
	}
	
	public static List<Class<? extends PatternImplType>> enumPatternImpls(String patternName)
	{
		return init(patternName);
	}
	
	public static InsertPattern getPatternInsertingInstance(String patternName, IWorkbenchWindow window)
	{
		String InsertPatternClassFullyQualifiedName = BASEPACKAGE + "." + patternName.toLowerCase() + ".Insert" + patternName;
		
		InsertPattern insertPattern = null;
		Class<?> cl = null;
		Class<?>[] type = { IWorkbenchWindow.class };
		Constructor<?> cons = null;
		try
		{
			cl = Class.forName(InsertPatternClassFullyQualifiedName);
			cons = cl.getConstructor(type);
			insertPattern = (InsertPattern) cons.newInstance(window);
		}
		catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return insertPattern;
	}
	
	/**
	 * <p>Gets the {@code index}-th item in our list of enumerated extensions of PatternImplType.
	 * 
	 * <p>Necessary because our config page reports the currently selected implementation type by 0-based iinteger index.
	 */
	public static Class<? extends PatternImplType> getImplClassByIndex(String patternName, int index)
	{
		List<Class<? extends PatternImplType>> implList = init(patternName);		
		Class<? extends PatternImplType> patternImplClass = implList.get(index);
		
		return patternImplClass;
	}
	
	private static int getImplPriority(Class<? extends PatternImplType> implClass)
	{
		int priority = 10000;
		try
		{
			priority = implClass.getField("PRIORITY").getInt(null);
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return priority;
	}
	
	public static String getImplDescription(Class<? extends PatternImplType> implClass)
	{
		String desc = "ERROR";
		try
		{
			desc = (String) implClass.getField("DESCRIPTION").get(null);
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return desc;
	}
	
	@Override
	//necessary for .sorted above
	public int compare(Class<? extends PatternImplType> o1, Class<? extends PatternImplType> o2)
	{
		
		return getImplPriority(o1) - getImplPriority(o2);
	}
}
