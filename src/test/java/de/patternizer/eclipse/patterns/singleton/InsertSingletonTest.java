package de.patternizer.eclipse.patterns.singleton;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;
import de.patternizer.eclipse.patterns.PatternImplType;

class InsertSingletonTest
{
	IWorkbenchWindow window = null;
	InsertSingleton sut = null;
	
	
	@BeforeEach
	public void arrange()
	{
		Map<String, String> parameters = new HashMap<String, String>();
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command c = commandService.getCommand("de.patternizer.patterns.Singleton");
		ExecutionEvent event = new ExecutionEvent(c, parameters, null, null);
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		sut = new InsertSingleton(event, window, "Singleton");
	}
	
	
	
	
	@Test
	void test_createPatternConfigPagePlugin_returnsSingletonConfigPagePlugin()
	{
		PatternConfigPagePlugin plugin = sut.createPatternConfigPagePlugin();
		
		Assertions.assertTrue(plugin instanceof SingletonConfigPagePlugin, "InsertSingleton.createPatternConfigPagePlugin)() did not return an instance of SingletonConfigPagePlugin"); 				
	}
	
	
	@Test
	void test_createPatternImplType_SingletonImplTypeSimpleReturnValid()
	{
		PatternImplType patternImplType = null;
		
		patternImplType = sut.createPatternImplType(SingletonImplTypeSimple.class);
		Assertions.assertTrue(patternImplType instanceof SingletonImplTypeSimple, "InsertSingleton.createPatternImplType(SingletonImplTypeSimple) did not return an instance of SingletonImplTypeSimple"); 				
	}
	
	@Test
	void test_createPatternImplType_SingletonImplTypeLazyReturnValid()
	{
		PatternImplType patternImplType = null;
		
		patternImplType = sut.createPatternImplType(SingletonImplTypeLazy.class);
		Assertions.assertTrue(patternImplType instanceof SingletonImplTypeLazy, "InsertSingleton.createPatternImplType(SingletonImplTypeLazy) did not return an instance of SingletonImplTypeLazy");		
	}
	
	@Test
	void test_createPatternImplType_SingletonImplTypeSyncReturnValid()
	{
		PatternImplType patternImplType = null;
		
		patternImplType = sut.createPatternImplType(SingletonImplTypeSync.class);
		Assertions.assertTrue(patternImplType instanceof SingletonImplTypeSync, "InsertSingleton.createPatternImplType(SingletonImplTypeSync) did not return an instance of SingletonImplTypeSync");
	}
	
	@Test
	void test_createPatternImplType_SingletonImplTypeHolderReturnValid()
	{
		PatternImplType patternImplType = null;
		
		patternImplType = sut.createPatternImplType(SingletonImplTypeHolder.class);
		Assertions.assertTrue(patternImplType instanceof SingletonImplTypeHolder, "InsertSingleton.createPatternImplType(SingletonImplTypeHolder) did not return an instance of SingletonImplTypeHolder");
	}
	
	@Test
	void test_createPatternImplType_PatternImplTypeThrows()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> sut.createPatternImplType(PatternImplType.class), "InsertSingleton.createPatternImplType(PatternImplType) die not throw IllegalArgumentException");
	}
	
	@Test
	void test_createPatternImplType_SingletonImplTypeThrows()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> sut.createPatternImplType(SingletonImplType.class), "InsertSingleton.createPatternImplType(SingletonImplType) die not throw IllegalArgumentException");
	}
	
}
