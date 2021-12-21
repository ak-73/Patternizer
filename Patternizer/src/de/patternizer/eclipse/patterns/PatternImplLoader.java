package de.patternizer.eclipse.patterns;

//import org.reflections.Reflections;

public class PatternImplLoader
{
	public static void loadPattern(String patternName)
	{
		String lowerCasePatternName = patternName.toLowerCase();
		String fullyQualifiedPackage = "de.patternizer.eclipse.patterns." + lowerCasePatternName;
		
		System.out.println(fullyQualifiedPackage);
		
		//Reflections reflections = new Reflections("de.patternizer.eclipse.patterns.singleton.InsertHolderSingleton");
		
		/*Reflections reflections = new Reflections(fullyQualifiedPackage);
		var implSet = reflections.getSubTypesOf(IPatternImplType.class);
		for (Class<? extends IPatternImplType> implClass : implSet)
		{
			System.out.println(implClass.getName());
		}*/
	}
}
