/**
 * 
 */
package com.moseph.modelutils.curve;

import org.simpleframework.xml.Attribute;

/**
 * @author Sascha Holzhauer
 *
 */
public class PowerCurve implements Curve {

	@Attribute(required = true)
	double exponent = 1.0;

	/**
	 * @see com.moseph.modelutils.curve.Curve#sample(double)
	 */
	@Override
	public double sample(double position) {
		return Math.pow(position, this.exponent);
	}
}
