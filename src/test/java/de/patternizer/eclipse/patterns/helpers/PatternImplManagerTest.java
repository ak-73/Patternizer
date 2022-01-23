package de.patternizer.eclipse.patterns.helpers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import de.patternizer.eclipse.patterns.InsertPattern;
import de.patternizer.eclipse.patterns.PatternImplType;
import de.patternizer.eclipse.patterns.singleton.SingletonImplTypeHolder;

class PatternImplManagerTest
{
	
	
	@Test
	public void test_enumPatternImplTypeListByPattern_SingletonImplTypeCount()
	{
		List<Class<? extends PatternImplType>> patternImplementations = PatternImplManager.enumPatternImplTypeListByPattern("Singleton");
		Assertions.assertEquals(patternImplementations.size(), 4);
	}
	
	@Test
	public void test_enumPatternImplTypeListByPattern_BuilderImplTypeCount()
	{
		List<Class<? extends PatternImplType>> patternImplementations = PatternImplManager.enumPatternImplTypeListByPattern("Builder");
		Assertions.assertEquals(patternImplementations.size(), 1);
	}
	
	@Test
	public void test_GetPatternInsertingInstance_Singleton_NotNull()
	{
		Map<String, String> parameters = new HashMap<String, String>();
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command c = commandService.getCommand("de.patternizer.patterns.Singleton");
		ExecutionEvent event = new ExecutionEvent(c, parameters, null, null);
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		InsertPattern patternInserter = PatternImplManager.getPatternInsertingInstance(event, window, "Singleton");		
		assertNotNull(patternInserter);
	}
	
	@Test
	public void test_enumPatternImplTypeListByPattern_UnknownPatternName()
	{
		Assertions.assertThrows(IllegalStateException.class, () -> PatternImplManager.enumPatternImplTypeListByPattern("gkjghfjkgvjdhgbnvbjkui89rjfa"), "Message");		
	}
	
	@Test
	public void test_enumPatternImplTypeListByPattern_NullPatternName()
	{
		Assertions.assertThrows(NullPointerException.class, () -> PatternImplManager.enumPatternImplTypeListByPattern(null), "Message");		
	}
	
	/*
	@Test
	public void test_getPatternInsertingInstance_NullPatternName()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Assertions.assertThrows(Exception.class, () -> PatternImplManager.getPatternInsertingInstance("gkjghfjkgvjdhgbnvbjkui89rjfa", window), "Expected getPatternInsertingInstance() to throw but it didn't.");		
	}
	*/
	
	@Test
	public void test_getImplDescription_NotERRORString()
	{
		Assertions.assertNotEquals(PatternImplManager.getImplDescription(SingletonImplTypeHolder.class), "ERROR");	
	}
		
}
