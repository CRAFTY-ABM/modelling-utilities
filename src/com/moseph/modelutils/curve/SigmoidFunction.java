package com.moseph.modelutils.curve;

import org.simpleframework.xml.Attribute;


/**
 * Implements (not considering normalisation):
 * 
 * {a*(x-c)^p/(h^p + abs((x-c)^p)) + d}
 * 
 * LaTeX: {a(x-c)^p} \over {(h^p + |(x-c)^p|)} + d
 * 
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
	// horizontal shift (defaults to 0)
	@Attribute(required = false)
	double C = 0;
	// vertical shift (defaults to 0)
	@Attribute(required = false)
	double D = 0;
	// relevant when D != 0: scales values to [-1,1] while keeping the turning point at level d
	@Attribute(required = false)
	boolean normalise = true;
	
	/**
	 * Simplest constructor.
	 * 
	 * @param H
	 */
	public SigmoidFunction(@Attribute(name = "H") double H) {
		this.H = H;
	}

	/**
	 * Complete constructor.
	 * 
	 * @param A
	 * @param H
	 * @param P
	 * @param C
	 * @param D
	 * @param normalise
	 */
	public SigmoidFunction(@Attribute(name = "A") double A, @Attribute(name = "H") double H,
			@Attribute(name = "P") double P, @Attribute(name = "C") double C, @Attribute(name = "D") double D,
			@Attribute(name = "normalise") boolean normalise)
	{
		this.A = A;
		this.H = H;
		this.P = P;
		this.C = C;
		this.D = D;
		this.normalise = normalise;
	}

	/**
	 * @see com.moseph.modelutils.curve.Curve#sample(double)
	 */
	public double sample( double position )
	{
		double f = A * Math.pow(position - C, P) / (Math.pow(H, P) + Math.abs(Math.pow(position - C, P))) + D;

		if (D != 0 && normalise) {
			f = f < D ? (D + (f - D) * (1.0 + D)) : (D + (f - D) * (1.0 - D));
		}
		return f;
	}
}
