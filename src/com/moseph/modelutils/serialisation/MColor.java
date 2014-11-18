/**
 * 
 */
package com.moseph.modelutils.serialisation;

import java.awt.Color;

import org.simpleframework.xml.Attribute;

/**
 * Used to serialise colours, e.g. for AFT presentations.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class MColor extends Color {

	@Attribute(name = "r")
	int r = 0;

	@Attribute(name = "g")
	int g = 0;

	@Attribute(name = "b")
	int b = 0;

	/**
	 * @param r
	 * @param g
	 * @param b
	 */
	public MColor(@Attribute(name = "r") int r, @Attribute(name = "g") int g,
			@Attribute(name = "b") int b) {
		super(r, g, b);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -193302774593229738L;

}
