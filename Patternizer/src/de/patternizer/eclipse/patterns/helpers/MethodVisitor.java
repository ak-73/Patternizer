package de.patternizer.eclipse.patterns.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Just visits to add to a {@code List} that can be retrieved.
 * @author Alexander Kalinowski
 *
 */
public class MethodVisitor extends ASTVisitor
{
	List<MethodDeclaration> methods = new ArrayList<>();
	
	@Override
	public boolean visit(MethodDeclaration node)
	{
		methods.add(node);
		return super.visit(node);
	}
	
	public List<MethodDeclaration> getMethods()
	{
		return methods;
	}
	
}
