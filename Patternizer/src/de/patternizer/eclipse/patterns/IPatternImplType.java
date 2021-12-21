package de.patternizer.eclipse.patterns;

public interface IPatternImplType
{
	public String getDescription();
	public void execute(IPatternConfigData configData, InsertionHelper insertionHelper);
}
