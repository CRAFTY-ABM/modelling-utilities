/**
 * 
 */
package com.moseph.modelutils.curve;

/**
 * @author Sascha Holzhauer
 *
 */
public class Identity implements Curve {

	/**
	 * @see com.moseph.modelutils.curve.Curve#sample(double)
	 */
	@Override
	public double sample(double position) {
		return position;
	}
}
