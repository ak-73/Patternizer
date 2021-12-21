package de.patternizer.eclipse.patterns.singleton;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.patternizer.eclipse.patterns.PatternImplLoader;

/**
 * Entry Point for the Insert Singleton command.
 * 
 * @author Alexander Kalinowski
 *
 */
public class _Handler_InsertSingleton_Command extends AbstractHandler
{
	private static Logger logger = LoggerFactory.getLogger(_Handler_InsertSingleton_Command.class);
	private IWorkbenchWindow window = null;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null) throw new AssertionError("execute() must never be passed NULL argument!");
		if (!initWindow(event)) return null;
		
		PatternImplLoader.loadPattern("Singleton");
		
		InsertSingleton singletonInserter = new InsertSingleton(window);
		
		// instantiate configuration data and let the user configure the insertion of
		// pattern in a dialog
		SingletonConfigData configData = singletonInserter.configurePatternInsertion(event);
		if (configData == null) return null;
		
		// main method
		singletonInserter.insertSingleton(event, configData);
		
		return null;
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
