package com.moseph.modelutils.curve;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moseph.modelutils.serialisation.EasyPersister;

public class LinearFunctionTest
{
	
	@Test
	public void testBasicOperation() throws Exception
	{
		LinearFunction function = new LinearFunction( 3, 8 );
		function = new EasyPersister().roundTripSerialise( function );
		assertEquals( 3 + 4*8, function.sample(  4 ), 0.00001 );
		assertEquals( 3 , function.sample(  0 ), 0.00001 );
		assertEquals( 3 - 4*8, function.sample(  -4 ), 0.00001 );
	}

}
