package de.patternizer.eclipse.patterns.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.ui.IWorkbenchWindow;
import org.reflections.Reflections;

import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.PatternImplType;

public class PatternImplManager implements Comparator<Class<? extends PatternImplType>>
{
	public static final String BASEPACKAGE = "de.patternizer.eclipse.patterns";
	public static Map<String, List<Class<? extends PatternImplType>>> pattermImpListsByPatternName = new TreeMap<String, List<Class<? extends PatternImplType>>>();
	
	/**
	 * 
	 * @param patternName
	 * @return A list of all pattern implementation types for the given pattern, ordered by priority. This method will not return null nor an empty list. 
	 */
	public static List<Class<? extends PatternImplType>> getPatternImplTypeListByPattern(String patternName)
	{
		if (pattermImpListsByPatternName.containsKey(patternName)) return pattermImpListsByPatternName.get(patternName);
		
		Reflections reflections = new Reflections(BASEPACKAGE + "." + patternName.toLowerCase());
		var implSet = reflections.getSubTypesOf(PatternImplType.class);
		
		if (implSet == null) throw new IllegalStateException("Reflections.getSubTypesOf() returned null instead of a list of pattern implementations for pattern " + patternName + ".");
		if (implSet.isEmpty()) throw new IllegalStateException("Reflections.getSubTypesOf() returned empty list of pattern implementations for pattern " + patternName + ".");
		
		// TODO: research if this is the best way to handle the formatting of fluent
		// APIs in eclipse without affecting everything else
		//@formatter:off
		var implList = 	implSet.stream()
						.filter(impl -> !Modifier.isAbstract(impl.getModifiers()))			//gotta filter out non-instantiable classes
						.sorted(new PatternImplManager())
						.collect(Collectors.toList());
		//@formatter:on
		
		pattermImpListsByPatternName.put(patternName, implList);
		return implList;		
	}
	
	
	public static InsertPattern getPatternInsertingInstance(String patternName, IWorkbenchWindow window)
	{
		String InsertPatternClassFullyQualifiedName = BASEPACKAGE + "." + patternName.toLowerCase() + ".Insert" + patternName;
		
		InsertPattern insertPattern = null;
		Class<?> cl = null;
		Class<?>[] type = { IWorkbenchWindow.class, String.class };
		Constructor<?> cons = null;
		try
		{
			cl = Class.forName(InsertPatternClassFullyQualifiedName);
			cons = cl.getConstructor(type);
			insertPattern = (InsertPattern) cons.newInstance(window, patternName);
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
	 * <p>
	 * Gets the {@code index}-th item in our list of enumerated extensions of
	 * PatternImplType.
	 * 
	 * <p>
	 * Necessary because our config page reports the currently selected
	 * implementation type by 0-based iinteger index.
	 */
	public static Class<? extends PatternImplType> getImplClassByIndex(String patternName, int index)
	{
		List<Class<? extends PatternImplType>> implList = getPatternImplTypeListByPattern(patternName);
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
	// necessary for .sorted above
	public int compare(Class<? extends PatternImplType> o1, Class<? extends PatternImplType> o2)
	{
		
		return getImplPriority(o1) - getImplPriority(o2);
	}
}
