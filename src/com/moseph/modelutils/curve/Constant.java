package com.moseph.modelutils.curve;

import org.simpleframework.xml.Attribute;

public class Constant implements Curve
{
	@Attribute( name="value")
	double value;
	
	public Constant( @Attribute(name="value") double value )
	{
		this.value = value;
	}

	public double sample( double position )
	{
		return value;
	}

}
