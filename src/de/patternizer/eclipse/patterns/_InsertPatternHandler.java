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

import de.patternizer.eclipse.patterns.helpers.PatternImplManager;

/**
 * Entry Point Handler for the Insert <Pattern> command. Calls configuration
 * handling and pattern insertion methods on the central class responsible for
 * handling the insertion of a given pattern, {@code Insert<Pattern>} (eg,
 * {@link de.patternizer.eclipse.patterns.singleton.InsertSingleton
 * InsertSingleton},
 * {@link de.patternizer.eclipse.patterns.builder.InsertBuilder InsertBuilder},
 * etc).
 * 
 * @author Alexander Kalinowski
 *
 */
//unconventional underscore but it makes this central class easy to find in the source folder
public class _InsertPatternHandler extends AbstractHandler
{
	
	// FIELDS
	private static Logger logger = LoggerFactory.getLogger(_InsertPatternHandler.class);
	private IWorkbenchWindow window = null;
	public static final String COMMANDPREFIX = "de.patternizer.patterns.";
	
	// MAIN METHODS
	/**
	 * Entry point for this plug-in. Implements {@code AbstractHandler} to deal with
	 * commands issues by user selections in the context menu.
	 * 
	 * @param event the Eclipse event that triggered this command execution
	 * @return {@code null}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null) throw new AssertionError("execute() must never be passed NULL argument!");
		if (!initWindow(event)) return null;
		
		// preparations
		String patternName = getPatternName(event);
		List<Class<? extends PatternImplType>> patternImplementations = getPatternImplementations(patternName);
		InsertPattern patternInserter = getPatternInserter(patternName);
		
		// open config dialog
		PatternConfigData configData = patternInserter.configurePatternInsertion(event, patternImplementations);
		if (configData == null) return null;
		
		// main method
		patternInserter.insertPattern(event, configData);
		
		return null;
	}
	
	// HELPER METHODS
	/**
	 * Returns a instance of an {@code Insert<Pattern>} subclass (eg,
	 * {@link de.patternizer.eclipse.patterns.singleton.InsertSingleton
	 * InsertSingleton}) that is the central class for handling the insertion of the
	 * given pattern.
	 * 
	 * @param patternName the name of the user-selected pattern to be inserted
	 * @return instance of an {@code Insert<Pattern>} subclass responsible for
	 *         handling the insertion
	 * @see de.patternizer.eclipse.patterns.singleton.InsertSingleton
	 *      InsertSingleton as an example subclass
	 */
	private InsertPattern getPatternInserter(String patternName)
	{
		InsertPattern patternInserter = PatternImplManager.getPatternInsertingInstance(patternName, window);
		if (patternInserter == null) throw new IllegalArgumentException("No pattern inserting class found!");
		return patternInserter;
	}
	
	/**
	 * Returns a list of all inserters for a given pattern variant (Lazy Singleton,
	 * Synchronized Singleton, etc).
	 * 
	 * @param the name of the user-selected pattern to be inserted
	 * @return a list of all inserters for a given pattern variant
	 * @see de.patternizer.eclipse.patterns.singleton.SingletonImplTypeLazy
	 *      SingletonImplTypeLazy as an sample pattern variant
	 * @see de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSync
	 *      SingletonImplTypeSync as another sample pattern variant
	 */
	private List<Class<? extends PatternImplType>> getPatternImplementations(String patternName)
	{
		List<Class<? extends PatternImplType>> patternImplementations = PatternImplManager.enumPatternImplTypeListByPattern(patternName);
		return patternImplementations;
	}
	
	/**
	 * The name of the user-selected pattern to be inserted, as derived from the
	 * command identifier encapsulated by the Eclipse event that triggered this
	 * command execution.
	 * 
	 * @param event the Eclipse event that triggered this command execution
	 * @return the name of the user-selected pattern to be inserted
	 */
	private String getPatternName(ExecutionEvent event)
	{
		Command command = event.getCommand();
		String commandID = command.getId();
		if (!(commandID.startsWith(COMMANDPREFIX))) throw new IllegalArgumentException("Pattern has not been assigned a properly formed commandID.");
		String patternName = commandID.substring(COMMANDPREFIX.length(), commandID.length());
		if (patternName.length() == 0) throw new IllegalArgumentException("Pattern has not been assigned a properly formed commandID.");
		return patternName;
	}
	
	/**
	 * Retrieve and store the active workbench window.
	 * <p>Package-private for unit testing only
	 * 
	 * @param event the Eclipse event that triggered this command execution
	 * @return true if successful, false otherwise
	 */
	boolean initWindow(ExecutionEvent event)
	{
		try
		{
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		}
		catch (ExecutionException e)
		{
			logger.error("getActiveWorkbenchWindowChecked() in _InsertPatternHandler.initWindow() failed.");
			return false;
		}
		
		return true;
	}
	
}
