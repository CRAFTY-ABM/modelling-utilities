/**
 * 
 */
package com.moseph.modelutils.curve;

import org.simpleframework.xml.Attribute;


/**
 * A Breakpoint as used by the Linear Interpolator
 * 
 * Compare is based on x position only.
 * @author dmrust
 *
 */
public class Breakpoint implements Comparable<Breakpoint>
{
	@Attribute(name="position")
	double position;
	@Attribute(name="level")
	double level;
	
	public Breakpoint( @Attribute(name="position") double position, @Attribute(name="level")double level )
	{
		this.position = position;
		this.level = level;
	}
	
	public int compareTo( Breakpoint o ) { return Double.compare( position, o.position ); }
	public String toString() { return "[" + position + "::" + level + "]"; }
	public double getPosition() { return position; }
	public double getLevel() { return level; }


}