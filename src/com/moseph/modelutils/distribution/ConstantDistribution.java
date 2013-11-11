package com.moseph.modelutils.distribution;

import org.simpleframework.xml.Attribute;

public class ConstantDistribution implements Distribution
{
	@Attribute(required=false)
	double value = 0;
	public double sample() { return value; }
}
