package de.patternizer.eclipse.patterns;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.patternizer.eclipse.patterns.singleton.SingletonConfigData;

public interface IPatternInserting
{
	SingletonConfigData configurePatternInsertion(ExecutionEvent event) throws ExecutionException;
	
	void insertSingleton(ExecutionEvent event, SingletonConfigData configData);
}
