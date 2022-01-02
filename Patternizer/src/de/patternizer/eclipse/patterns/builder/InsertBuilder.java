package de.patternizer.eclipse.patterns.builder;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.singleton.InsertSingleton;

public class InsertBuilder extends InsertPattern
{
	
	//FIELDS
	private static Logger logger = LoggerFactory.getLogger(InsertSingleton.class);
	
	
	
	//CONSTRUCTORS
	public InsertBuilder(IWorkbenchWindow window, String patternName)
	{
		super(window);
		this.patternName = patternName;
	}
	
	
	
	//METHODS (FACTORY METHODS)	
	@Override
	public PatternImplType createPatternImplType(Class<? extends PatternImplType> implTypeClass)
	{
		if (implTypeClass.equals(BuilderImplType.class)) throw new IllegalArgumentException("Erroneously attempting to make factory method createPatternImplType() create an instance of the abstract base class " + BuilderImplType.class.getSimpleName() + ".");
				
		if (implTypeClass.equals(BuilderImplTypeSimple.class))  return new BuilderImplTypeSimple( new BuilderInsertMethodProgrammatically());
		//else if (implTypeClass.equals(BuilderImplTypeLazy.class))  return new BuilderImplTypeSimple( new BuilderInsertMethodProgrammatically()); 
		//else if (implTypeClass.equals(SingletonImplTypeSync.class))  return new BuilderImplTypeSimple( new BuilderInsertMethodProgrammatically());
		//else if (implTypeClass.equals(SingletonImplTypeHolder.class))  return new BuilderImplTypeSimple( new BuilderInsertMethodProgrammatically());

		throw new IllegalArgumentException("Unknown pattern implementation type " + implTypeClass.getSimpleName() + ". Make sure that the appropriate createPatternImplType() method is aware of it.");
	}
	
	@Override
	public PatternConfigData createConfigData(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations)
	{
		logger.info("Insert " + patternName + " pattern has been evoked.");
		BuilderConfigData configData = new BuilderConfigData();
		configData.setBuilderClassIdentifier("Builder");
		configData.setBuildMethodIdentifier("build");
		configData.setImplTypeList(patternImplementations);
		return configData;
	}
	
	@Override
	public PatternConfigPagePlugin createPatternConfigPagePlugin()
	{
		return new BuilderConfigPagePlugin();
	}

}

