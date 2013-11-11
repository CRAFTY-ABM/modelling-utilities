package com.moseph.modelutils;
import static org.junit.Assert.*;
import static com.moseph.modelutils.Statistics.*;
import static com.moseph.modelutils.Utilities.*;

import org.junit.Test;

public class StatisticsTest
{
	@Test
	public void testBasicGINI()
	{
		assertEquals( 0, calculateGini( new double[] {1, 1, 1, 1, 1, 1, 1, 1 }), 0 );
		assertEquals( 0, calculateGini( new double[] {0, 0, 0, 0, 0, 0, 0, 0 }), 0 );
		assertEquals( 0.8, calculateGini( new double[]{ 0, 0, 0, 0, 1 } ), 0 );
		double[] values = new double[40000];
		for( int i = 0; i < 40000; i++ ) values[i] = Math.pow( nextDouble(), 2 );
		double g = calculateGini( values );
		assertTrue( g > 0 );
		assertTrue( g < 1 );
		assertTrue( calculateGini( new double[] { 0, 0, 1, 1, 1 } ) < calculateGini( new double[] { 0, 0, 0, 1, 1 } ) );
		
		System.err.println( calculateGini( new double[] {1, 1, 1, 1, 2, 2, 2, 2 }));
		System.err.println( calculateGini( new double[] {3, 3, 3, 3, 4, 4, 4, 4 }));
		System.err.println( calculateGini( new double[] {3, 3, 3, 3, 6, 6, 6, 6 }));
		System.err.println( calculateGini( new double[] {-1, 1, 1, 1, 2, 2, 2, 2 }));
	}

}
