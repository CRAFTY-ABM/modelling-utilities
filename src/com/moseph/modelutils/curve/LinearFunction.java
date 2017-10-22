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
	/**
	 * Intercept. Default: 0
	 */
	@Attribute(required = false)
	double a = 0;

	/**
	 * Slope. Default: 1
	 */
	@Attribute(required = false)
	double b = 1;
	
	/**
	 * @param a
	 *        intercept
	 * @param b
	 *        slope
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

	public LinearFunction getDeepCopy(double addToA, double addToB) {
		return new LinearFunction(this.a + addToA, this.b + addToB);
	}
}
