package de.patternizer.eclipse.patterns;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public interface InsertPattern
{
	PatternConfigData configurePatternInsertion(ExecutionEvent event, List<Class<? extends PatternImplType>> patternImplementations) throws ExecutionException;
	
	void insertPattern(ExecutionEvent event, PatternConfigData configData);
}
