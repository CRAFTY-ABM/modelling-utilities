package com.moseph.modelutils.curve;

import static java.lang.Math.*;

import org.simpleframework.xml.Attribute;

/**
 * Should implement:
 * 
 * a+b*exp(c*x)
 * 
 * @author cbrown
 *
 */
public class ExponentialFunction implements Curve
{
	//The y asymptote 
	@Attribute( required=true )
	double A = 0.0;
	//The slope (defaults to 1)
	@Attribute( required=false)
	double B = 1.0;
	//The vertical stretch (defaults to 1)
	@Attribute( required=false)
	double C = 1.0;
	

	/**
	 * @param A
	 * @param B
	 * @param C
	 */
	public ExponentialFunction( 
			@Attribute(name="A")double A ) 
	{
		this.A = A;
	}
	
	public ExponentialFunction( double a, double b , double c)
	{
		super();
		A = a;
		B = b;
		C = c;
	}

	public double sample( double position )
	{
		 double ret = A + (B*exp(C*position));
		 return ret;
	}	
}
