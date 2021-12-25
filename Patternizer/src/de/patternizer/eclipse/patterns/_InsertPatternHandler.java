package de.patternizer.eclipse.patterns;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry Point Handler for the Insert <Pattern> command.
 * 
 * @author Alexander Kalinowski
 *
 */
public class _InsertPatternHandler extends AbstractHandler
{
	private static Logger logger = LoggerFactory.getLogger(_InsertPatternHandler.class);
	private IWorkbenchWindow window = null;
	public static final String COMMANDPREFIX = "de.kalinowski.patternizer.patterns.";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null) throw new AssertionError("execute() must never be passed NULL argument!");
		if (!initWindow(event)) return null;
		
		//preparations
		String patternName = getPatternName(event);						
		List<Class<? extends PatternImplType>> patternImplementations = getPatternImplementations(patternName);								
		InsertPattern patternInserter = getPatternInserter(patternName);		
		
		// instantiate configuration data and let the user configure the insertion of pattern in a dialog
		PatternConfigData configData = patternInserter.configurePatternInsertion(event, patternImplementations);
		if (configData == null) return null;
						
		// main method
		patternInserter.insertPattern(event, configData);
		
		return null;
	}


	private InsertPattern getPatternInserter(String patternName)
	{
		InsertPattern patternInserter = PatternImplManager.getPatternInsertingInstance(patternName, window);
		if (patternInserter == null) throw new IllegalArgumentException("No pattern inserting class found!");
		return patternInserter;
	}



	private List<Class<? extends PatternImplType>> getPatternImplementations(String patternName)
	{
		List<Class<? extends PatternImplType>> patternImplementations = PatternImplManager.enumPatternImpls(patternName);
		if (patternImplementations.isEmpty()) throw new IllegalArgumentException("No implementation of the pattern found!");
		return patternImplementations;
	}



	private String getPatternName(ExecutionEvent event)
	{
		Command command = event.getCommand();
		String commandID = command.getId();
		if (!(commandID.startsWith(COMMANDPREFIX))) throw new IllegalArgumentException("Pattern has not been assigned a properly formed commandID.");
		String patternName = commandID.substring(COMMANDPREFIX.length(), commandID.length());
		if (patternName.length() == 0) throw new IllegalArgumentException("Pattern has not been assigned a properly formed commandID.");
		return patternName;
	}
	
	
	
	private boolean initWindow(ExecutionEvent event)
	{
		try
		{
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		}
		catch (ExecutionException e)
		{
			logger.error("getActiveWorkbenchWindowChecked() failed.");
			return false;
		}
		
		return true;
	}
	
}
