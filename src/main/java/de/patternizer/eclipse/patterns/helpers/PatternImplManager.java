package de.patternizer.eclipse.patterns.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeLazy;

/**
 * Responsible for locating (for a given pattern) the entry point class of the
 * form {@code Insert<Pattern>} as well as all classes for the pattern
 * implementation variants of the form {@code <Pattern>ImplType<Typename>} by
 * using {@code org.reflections}.
 * <p>
 * Additionally, it implements a {@link Comparator} interface for ordering these
 * pattern implementations by priority, so that they can be displayed to the
 * user as a choice in a pre-determined order.
 * 
 * @author Alexander Kalinowski
 *
 */
public class PatternImplManager implements Comparator<Class<? extends PatternImplType>>
{
	private static Logger logger = LoggerFactory.getLogger(PatternImplManager.class);
	
	public static final String BASEPACKAGE = "de.patternizer.eclipse.patterns";
	public static Map<String, List<Class<? extends PatternImplType>>> pattermImpListsByPatternName = new TreeMap<String, List<Class<? extends PatternImplType>>>();
	
	
	
	/**
	 * Returns all enumerated pattern implementations. For "Singleton", for example,
	 * the returned list might contain the {@code Class} objects for
	 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSimple
	 * SingletonImplTypeSimple}, {@link SingletonImplTypeLazy}, etc.
	 * 
	 * @param pattern Name name of the pattern ("Singleton", "Builder", etc.)
	 * @return A list of all pattern implementation types for the given pattern,
	 *         ordered by priority. This method will not return null nor an empty
	 *         list.
	 */
	public static List<Class<? extends PatternImplType>> enumPatternImplTypeListByPattern(String patternName)
	{
		if (pattermImpListsByPatternName.containsKey(patternName)) return pattermImpListsByPatternName.get(patternName);
		
		Reflections reflections = new Reflections(BASEPACKAGE + "." + patternName.toLowerCase());
		var implSet = reflections.getSubTypesOf(PatternImplType.class);
		
		//FIXME look into this
		//for SOME reason it fails sometimes to find the impls; let's try it a second time before giving up
		if ((implSet == null) || (implSet.isEmpty()))
		{
			logger.warn("Reflections instance " + reflections.toString() + " did not find the subtypes in package " + BASEPACKAGE + "." + patternName.toLowerCase() + ". Trying a second time before giving up...");
			implSet = reflections.getSubTypesOf(PatternImplType.class);
		}
		
		if (implSet == null) throw new IllegalStateException(
				"Reflections.getSubTypesOf() returned null instead of a list of pattern implementations for pattern " + patternName + ".");
		if (implSet.isEmpty())
			throw new IllegalStateException("Reflections.getSubTypesOf() returned empty list of pattern implementations for pattern " + patternName + ".");
		
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
	
	/**
	 * Returns a subclass of an {@code Insert<Pattern>} instance. This instance is
	 * the central class responsible for inserting a pattern of the given type,
	 * delegating to {@code <Pattern>ImplType} subclasses as necessary per pattern
	 * variant (eg, Lazy Singleton, Synchronized Singleton, etc).
	 * 
	 * @param patternName
	 * @param window
	 * @return
	 */
	public static InsertPattern getPatternInsertingInstance(ExecutionEvent event, IWorkbenchWindow window, String patternName)
	{
		String InsertPatternClassFullyQualifiedName = BASEPACKAGE + "." + patternName.toLowerCase() + ".Insert" + patternName;
		
		InsertPattern insertPattern = null;
		Class<?> cl = null;
		Class<?>[] type = { ExecutionEvent.class, IWorkbenchWindow.class, String.class };
		Constructor<?> cons = null;
		try
		{
			cl = Class.forName(InsertPatternClassFullyQualifiedName);
			cons = cl.getConstructor(type);
			insertPattern = (InsertPattern) cons.newInstance(event, window, patternName);
		}
		catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			logger.error("Constructor invocation failed: " + e.toString() + e.getMessage());
			e.printStackTrace();
		}
		
		return insertPattern;
	}
	
	/**
	 * Returns the priority associated with a given pattern implementation, to be
	 * used for ordering the pattern implementations in a config dialog for the
	 * user.
	 * 
	 * @param implClass {@code Class} object for a given pattern implementation
	 * @return the priority associated with a given pattern implementation
	 */
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
	
	/**
	 * Returns the description associated with a given pattern implementation, to be
	 * used for describing the pattern implementations in a config dialog to the
	 * user.
	 * 
	 * @param implClass {@code Class} object for a given pattern implementation
	 * @return the description associated with a given pattern implementation
	 */
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
	
	/**
	 * Compares to pattern implementation according to priority.
	 * @see #getImplPriority(Class)
	 */
	@Override
	// necessary for .sorted above
	public int compare(Class<? extends PatternImplType> o1, Class<? extends PatternImplType> o2)
	{
		
		return getImplPriority(o1) - getImplPriority(o2);
	}
}
