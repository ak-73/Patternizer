package de.patternizer.eclipse.patterns;

import org.eclipse.jdt.core.dom.AST;

/**
 * Marker interface for sub-interfaces that offer pattern
 * implementation-specific <i>implementation methods</i>.
 * <p>
 * For example,
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSimple
 * SingletonImplTypeSimple} provides the methods structure (in other words, the
 * steps that need to be taken) to implement a simple singleton pattern. But it
 * does not specify <i>how</i> this is concretely implemented/inserted.
 * 
 * <p>
 * It is the responsibility of implementing classes to do the actual insertion
 * per pattern, using the chosen method. The above
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonImplTypeSimple
 * SingletonImplTypeSimple} may, for example, be implemented by programmatically
 * manipulating the {@link AST} for the current Java file. On the other hand,
 * there might be a different implementation method available that deserializes
 * {@code AST}s appropriate to the selected pattern implementation and adapts
 * them to the current Java file before inserting them.
 * 
 * <p>
 * As implementation of pattern insertion methods is pattern-specific,
 * implementing classes are also expected to implement a sub-interface that is
 * specific to the given pattern. These sub-interfaces follow the naming
 * convention {@code <Pattern>InsertMethod}, for example
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonInsertMethod
 * SingletonInsertMethod}, whereas implementing classes add the method name as a
 * suffix:
 * <p>
 * {@code <Pattern>InsertMethod<Methodname>} (Example:
 * {@link de.patternizer.eclipse.patterns.singleton.SingletonInsertMethodProgrammatically
 * SingletonInsertMethodProgrammatically}).
 * 
 * 
 * @author Alexander Kalinowski
 *
 */
public interface PatternInsertMethod
{
	
}
