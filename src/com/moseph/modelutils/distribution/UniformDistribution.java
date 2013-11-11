package com.moseph.modelutils.distribution;

import org.simpleframework.xml.Attribute;

import com.moseph.modelutils.Utilities;


public class UniformDistribution implements Distribution
{
	@Attribute(required=false)
	double min = 0;
	@Attribute(required=false)
	double max = 1;
	public UniformDistribution() {}
	public UniformDistribution( double min, double max ) 
	{
		this.min = min;
		this.max = max;
	}
	public double sample() { return Utilities.nextDoubleFromTo( min, max ); }

}
