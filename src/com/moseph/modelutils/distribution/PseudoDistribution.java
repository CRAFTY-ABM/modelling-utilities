/**
 * 
 */
package com.moseph.modelutils.distribution;

import org.simpleframework.xml.Attribute;

import de.cesr.uranus.core.UranusRandomService;

/**
 * This pseudo distribution is useful for easy parameter sweeps between
 * variation and fixed values.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class PseudoDistribution implements Distribution {

	@Attribute(required = false)
	double mean = 0;

	/**
	 * @see com.moseph.modelutils.distribution.Distribution#sample()
	 */
	@Override
	public double sample() {
		return this.mean;
	}

	/**
	 * @see com.moseph.modelutils.distribution.Distribution#init(de.cesr.uranus.core.UranusRandomService, java.lang.String)
	 */
	@Override
	public void init(UranusRandomService rService, String generatorName) {
		// nothing to do
	}

	/**
	 * @see com.moseph.modelutils.distribution.Distribution#isInitialised()
	 */
	@Override
	public boolean isInitialised() {
		return true;
	}
}
