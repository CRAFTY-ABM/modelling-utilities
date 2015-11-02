package com.moseph.modelutils.curve;

import org.simpleframework.xml.Attribute;

/**
 * Should implement:
 * 
 * a + bx
 * @author dmrust
 *
 */
public class LinearFunction implements Curve
{
	//The ending value
	@Attribute( required=true )
	double a = 0;
	//The starting value (defaults to 0)
	@Attribute( required=true)
	double b = 0;
	
	/**
	 * Simplest constructor. Give it a maximum value, a growth rate and a time of
	 * max growth, and it'll give you a curve
	 * @param K
	 * @param B
	 * @param M
	 */
	public LinearFunction( 
			@Attribute(name="a")double a, 
			@Attribute(name="b")double b )
	{
		this.a = a;
		this.b = b;
	}
	
	public double sample( double position )
	{
		return a + b*position;
	}

	public String toString()
	{
		return String.format( "y=%f x + %f", b, a );
	}
	
	public void setA(double a) {
		this.a = a;
	}

	public void setB(double b) {
		this.b = b;
	}
}
