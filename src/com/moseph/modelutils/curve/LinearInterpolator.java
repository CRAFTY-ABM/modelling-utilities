package com.moseph.modelutils.curve;

import java.util.*;

import org.simpleframework.xml.*;

/**
 * A Curve which interpolates between a set of Breakpoints that set
 * an x and y value.
 *
 * Sampling after the last Breakpoint returns the value of that breakpoint,
 * and sampling before the first returns the first value (i.e. the lines extend horizontally)
 * @author dmrust
 *
 */
@Root
public class LinearInterpolator implements Curve
{
	@ElementList(inline=true)
	SortedSet<Breakpoint> breakpoints = new TreeSet<Breakpoint>();

	public LinearInterpolator()
	{

	}

	public double sample( double position )
	{
		if( breakpoints.size() == 0 ) return 0;
		if( position < breakpoints.first().position ) return breakpoints.first().level;
		Breakpoint previous = null;
		for( Breakpoint bp : breakpoints )
		{
			//We've already checked that the first BP is less that  the sampling position
			if( bp.position > position )
			{
				return previous.level +
					//Proportion of interval
					((position-previous.position)/(bp.position-previous.position))
					//Range over interval
					* (bp.level - previous.level );
			}
			previous = bp;
		}
		return  breakpoints.last().level;
	}

	public void addPoint( double position, double level )
	{
		breakpoints.add( new Breakpoint( position, level ) );
	}

	public List<Breakpoint> getBreakpoints()
	{
		return new ArrayList<Breakpoint>( breakpoints );
	}

	public void setBreakpoints( List<Breakpoint> bp )
	{
		breakpoints.clear();
		breakpoints.addAll( bp );
	}

	public String displayString()
	{
		String s = "Interpolator: ";
		for( Breakpoint b : breakpoints )
			s += b.toString() + " ";
		return s;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return displayString();
	}
}
