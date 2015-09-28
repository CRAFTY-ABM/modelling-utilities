package com.moseph.modelutils.curve;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

import org.simpleframework.xml.Attribute;


/**
 * Should implement:
 * 
 * a + ( (k-a) / (1 + q*exp(-b * ( x - m )))^(1/v))
 * 
 * LaTeX: A + {K-A \over {(1 + Q\exp(-B(x - m)))^{1\over v}}}
 * 
 * @author dmrust
 * 
 */
public class GeneralisedLogisticFunction implements Curve
{
	//The ending value
	@Attribute( required=true )
	double K = 0;
	//The starting value (defaults to 0)
	@Attribute( required=false)
	double A = 0;
	//Not quite sure!
	@Attribute( required=false)
	double Q = 0.5;
	//Growth rate
	@Attribute( required = true)
	double B;
	//Time of maximum growth
	@Attribute( required = true )
	double M;
	//Together with Q shifts the asymptote, but don't have good guidance at the moment.
	@Attribute( required = false )
	double v = 0.5;
	
	/**
	 * Simplest constructor. Give it a maximum value, a growth rate and a time of
	 * max growth, and it'll give you a curve
	 * @param K
	 * @param B
	 * @param M
	 */
	public GeneralisedLogisticFunction( 
			@Attribute(name="K")double K, 
			@Attribute(name="B")double B, 
			@Attribute(name="M")double M )
	{
		this.K = K;
		this.B = B;
		this.M = M;
	}
	
	public GeneralisedLogisticFunction( double k, double a, double q, double b, double m, double v )
	{
		super();
		K = k;
		A = a;
		Q = q;
		B = b;
		M = m;
		this.v = v;
	}

	public double sample( double position )
	{
		 double ret = A + ( (K-A) / 
				 pow(1 + Q*exp(-B * ( position - M )),1/v)
						 );
		//return A + (K - A) / pow( 1 + Q * exp( -B * ( position - M) ), 1/v );
		 return ret;
	}

	
}
