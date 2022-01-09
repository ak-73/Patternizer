package de.patternizer.eclipse.patterns;





import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.patternizer.eclipse.patterns.helpers.PatternImplManager;
import de.patternizer.eclipse.patterns.singleton.SingletonConfigData;

public class _InsertPatternHandlerTest
{
	
	
	@Test
	public void test_execute_ExecutionEventNull()
	{
		_InsertPatternHandler handler = new _InsertPatternHandler();
		
		Assertions.assertThrows(AssertionError.class, () -> handler.execute(null), "Expected _InsertPatternHandler.execute() to throw AssertionError");
	}
	
	
	
	@Test
	public void test_initWindow_Succeeds()
	{
		Map<String, String> parameters = new HashMap<String, String>();
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command c = commandService.getCommand("de.patternizer.patterns.Singleton");
		ExecutionEvent event = new ExecutionEvent(c, parameters, null, null);
		
		_InsertPatternHandler handler = new _InsertPatternHandler();
		boolean result = handler.initWindow(event);
		assertEquals(result, false);
	}
		
	
	
	@Test
	public void test_CreateConfigData_CreatesSingletonConfigData()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		InsertPattern patternInserter = PatternImplManager.getPatternInsertingInstance("Singleton", window);
		
		Map<String, String> parameters = new HashMap<String, String>();
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command c = commandService.getCommand("de.patternizer.patterns.Singleton");
		ExecutionEvent event = new ExecutionEvent(c, parameters, null, null);
		
		List<Class<? extends PatternImplType>> patternImplementations = PatternImplManager.enumPatternImplTypeListByPattern("Singleton");
		
		PatternConfigData configData = patternInserter.createConfigData(event, patternImplementations);
		Assertions.assertTrue(configData instanceof SingletonConfigData);
	}
	
}
