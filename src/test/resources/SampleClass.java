package de.patternizer.inner;

enum SampleCLassEnum
{
	LOW, MEDIUM, HIGH
}

public class SampleClass
{
	
	public int x = 13;
	protected int y;
	String label;
	
	public SampleClass()
	{
		super();
		this.x = 0;
		this.y = 0;
		this.label = "Default";
	}
	
	public SampleClass(int x, int y, String label)
	{
		super();
		this.x = x;
		this.y = y;
		this.label = label;
	}

	
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}

	protected int getY()
	{
		return y;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	final public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
	
	
	
	
}
