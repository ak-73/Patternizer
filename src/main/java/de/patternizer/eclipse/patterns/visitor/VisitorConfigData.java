package de.patternizer.eclipse.patterns.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.helpers.InsertionDataDefault;

/**
 * Visitor-specific config data that adds fields for
 * <ul>
 * <li>an enum for configuring the currently active source code file as a visitor class or as a visitee (a class that accepts visitors),</li>
 * <li>the primary type of the currently active source code file,</li>
 * <li>the user-selected base class for the to-be-inserted visitor methods (all subtypes of this will have visitor methods inserted as well),</li>
 * <li>the user-selected base class for the to-be-inserted visitee methods (all subtypes of this will have visitor-accepting methods inserted as well),</li>
 * <li>the identifier for the visitor methods as a String; and,</li> 
 * <li>the identifier for the visitees' visitor-accepting methods as a String.</li>
 * </ul>
 * 
 * @author Alexander Kalinowski
 *
 */
public class VisitorConfigData extends PatternConfigData
{
	
	//ENUMS
	public enum Goal { VISITOR, VISITEE }
	
	//FIELDS	
	InsertionDataDefault insertionHelper = null;
	Goal currentTypeGoal = Goal.VISITOR;	
	TypeDeclaration selectedType = null;
	IType visitorBaseType = null;
	IType visiteeBaseType = null;
	String visitorMethodIdentifier = null;
	String visiteeMethodIdentifier = null;
	
	List<ICompilationUnit> units = new ArrayList<ICompilationUnit>();

	//HostApplicability applyToHosts = HostApplicability.SINGLE;
	//VisitorApplicability applyToVisitors = VisitorApplicability.SINGLE;	
	
	//CONSTRUCTORS
	public VisitorConfigData()
	{
		
	}
	
	
	//GETTERS & SETTERS
	public InsertionDataDefault getInsertionHelper()
	{
		return insertionHelper;
	}

	public void setInsertionHelper(InsertionDataDefault insertionHelper)
	{
		this.insertionHelper = insertionHelper;
	}
	
	public TypeDeclaration getSelectedType()
	{
		return selectedType;
	}
	
	public void setSelectedType(TypeDeclaration selectedType)
	{
		this.selectedType = selectedType;
	}


	public IType getVisitorBaseType()
	{
		return visitorBaseType;
	}


	public void setVisitorBaseType(IType visitorBaseType)
	{
		this.visitorBaseType = visitorBaseType;
	}


	public IType getVisiteeBaseType()
	{
		return visiteeBaseType;
	}


	public void setVisiteeBaseType(IType visiteeBaseType)
	{
		this.visiteeBaseType = visiteeBaseType;
	}


	public String getVisitorMethodIdentifier()
	{
		return visitorMethodIdentifier;
	}


	public void setVisitorMethodIdentifier(String visitorMethodIdentifier)
	{
		this.visitorMethodIdentifier = visitorMethodIdentifier;
	}


	public String getVisiteeMethodIdentifier()
	{
		return visiteeMethodIdentifier;
	}


	public void setVisiteeMethodIdentifier(String visiteeMethodIdentifier)
	{
		this.visiteeMethodIdentifier = visiteeMethodIdentifier;
	}


	public Goal getCurrentTypeGoal()
	{
		return currentTypeGoal;
	}


	public void setCurrentTypeGoal(Goal currentTypeGoal)
	{
		this.currentTypeGoal = currentTypeGoal;
	}


	public List<ICompilationUnit> getUnits()
	{
		return units;
	}


	public void setUnits(List<ICompilationUnit> units)
	{
		this.units = units;
	}

	

}
