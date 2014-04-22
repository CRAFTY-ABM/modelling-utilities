package com.moseph.modelutils;
import static com.moseph.modelutils.Statistics.calculateGini;
import static com.moseph.modelutils.Utilities.nextDouble;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cesr.uranus.core.URandomService;

public class StatisticsTest
{
	@Test
	public void testBasicGINI()
	{
		assertEquals( 0, calculateGini( new double[] {1, 1, 1, 1, 1, 1, 1, 1 }), 0 );
		assertEquals( 0, calculateGini( new double[] {0, 0, 0, 0, 0, 0, 0, 0 }), 0 );
		assertEquals( 0.8, calculateGini( new double[]{ 0, 0, 0, 0, 1 } ), 0 );
		double[] values = new double[40000];
		for (int i = 0; i < 40000; i++) {
			values[i] = Math.pow( nextDouble(URandomService.getURandomService(), null), 2 );
		}
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
