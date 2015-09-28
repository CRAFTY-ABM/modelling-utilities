package com.moseph.modelutils.curve;

import org.simpleframework.xml.Attribute;


/**
 * Implements:
 * 
 * {a*x^p/(h^p + abs(x^p))}
 * 
 * @author dmrust
 * 
 */
public class SigmoidFunction implements Curve
{
	// asymptote (defaults to 1)
	@Attribute( required=true )
	double A = 1;
	// x-value when 0.5a is reached (defaults to 1)
	@Attribute( required=false)
	double H = 1;
	// power - controls steepness (defaults to 3, note that even numbers result in values all >= 0)
	@Attribute( required=false)
	double P = 3;
	
	/**
	 * Simplest constructor.
	 * 
	 * @param A
	 * @param H
	 * @param P
	 */
	public SigmoidFunction(@Attribute(name = "A") double A, @Attribute(name = "H") double H,
			@Attribute(name = "P") double P)
	{
		this.A = A;
		this.H = H;
		this.P = P;
	}

	/**
	 * @see com.moseph.modelutils.curve.Curve#sample(double)
	 */
	public double sample( double position )
	{
		return A * Math.pow(position, P) / (Math.pow(H, P) + Math.abs(Math.pow(position, P)));
	}
}
