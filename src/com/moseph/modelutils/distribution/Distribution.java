package com.moseph.modelutils.distribution;

import org.simpleframework.xml.Root;

/**
 * A distribution can be sampled to give a randomly chosen value
 * @author dmrust
 *
 */
@Root
public interface Distribution
{
	public double sample();

}
