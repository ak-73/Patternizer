package de.patternizer.eclipse.patterns.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;

public class FieldVisitor extends ASTVisitor
{
	List<FieldDeclaration> methods = new ArrayList<>();
	
	@Override
	public boolean visit(FieldDeclaration node)
	{
		methods.add(node);
		return super.visit(node);
	}
	
	public List<FieldDeclaration> getFields()
	{
		return methods;
	}
	
}