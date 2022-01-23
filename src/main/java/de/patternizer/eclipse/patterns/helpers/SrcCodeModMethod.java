package de.patternizer.eclipse.patterns.helpers;


/**
 * Encapsulates the two methods that JDT currently offers for modifying source
 * files: modifying and descriptive. Set in {@link de.patternizer.eclipse.patterns.PatternConfigData PatternConfigData}. If set to
 * {@code null}, {@link de.patternizer.eclipse.patterns.InsertPattern InsertPattern} is responsible for starting the recording
 * of modifications and writing to file. Else, the individual pattern in
 * question is responsible, employing an implementing class of this interface.
 * 
 * @author Alexander Kalinowski
 *
 */
public interface SrcCodeModMethod
{
	public void startRecording(InsertionData insertionData);
	
	public void writeRecordingsToAst(InsertionData insertionData);
}
