package com.moseph.modelutils.curve;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moseph.modelutils.serialisation.EasyPersister;


public class LinearInterpolatorTest {
	
	@Test
	public void testBasicOperation() throws Exception
	{
		LinearInterpolator function = new LinearInterpolator();
		function.addPoint(-Double.MAX_VALUE, 0);
		function.addPoint(-0.000000000000001, 0);
		function.addPoint(0, 3);
		function.addPoint(Math.pow(10, 10), 0.5 * Math.pow(10, 10) + 3);
		function = new EasyPersister().roundTripSerialise( function );

		assertEquals(0.0, function.sample(-100), 0.00001);
		assertEquals(3, function.sample(0), 0.00001);
		assertEquals(3.5, function.sample(1), 0.00001);
		assertEquals(4.0, function.sample(2), 0.00001);
	}
}
