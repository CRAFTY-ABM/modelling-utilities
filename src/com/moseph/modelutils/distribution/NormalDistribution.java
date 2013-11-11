package com.moseph.modelutils.distribution;

import org.simpleframework.xml.Attribute;

import com.moseph.modelutils.Utilities;


public class NormalDistribution implements Distribution
{
	@Attribute(required=false)
	double mean = 0;
	@Attribute(required=false)
	double sd = 1;
	public NormalDistribution() {}
	public NormalDistribution( double mean, double sd ) 
	{
		this.mean = mean;
		this.sd = sd;
	}
	public double sample() { 
		double rand = 4*sd*(Utilities.nextDoubleFromTo( -1, 1 )); 
		return (1/(sd*Math.sqrt(2.0*Math.PI)))*Math.exp(-((rand-mean)*(rand-mean))/(2*sd*sd));
	}
}
