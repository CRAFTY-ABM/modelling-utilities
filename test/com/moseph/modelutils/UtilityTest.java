package com.moseph.modelutils;

import static com.moseph.modelutils.Utilities.applyProbability;
import static com.moseph.modelutils.Utilities.consume;
import static com.moseph.modelutils.Utilities.getMaximum;
import static com.moseph.modelutils.Utilities.getMinimum;
import static com.moseph.modelutils.Utilities.nextIntFromTo;
import static com.moseph.modelutils.Utilities.sample;
import static com.moseph.modelutils.Utilities.sampleRate;
import static com.moseph.modelutils.Utilities.scale;
import static com.moseph.modelutils.Utilities.sortMap;
import static com.moseph.modelutils.fastdata.Columns.A;
import static com.moseph.modelutils.fastdata.Columns.B;
import static com.moseph.modelutils.fastdata.Columns.C;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.moseph.modelutils.fastdata.Columns;

import de.cesr.uranus.core.URandomService;

public class UtilityTest
{
	
	@Test
	public void testBasicSampling()
	{
		Set<Columns> types = new HashSet<Columns>();
		types.add( B );
		types.add( A );
		for( int i = 0; i < 1000; i++ ) {
			assertTrue( types.contains( sample( types, URandomService.getURandomService(), null) ) );
		}
	}
	
	@Test
	public void testSamplingDistribution()
	{
		Set<Columns> types = new HashSet<Columns>();
		types.add( B );
		types.add( A );
		types.add( C );
		Map<Columns, Integer> households = new HashMap<Columns, Integer>();
		households.put( B, 0 );
		households.put( A, 0 );
		households.put( C, 0 );
		for (int i = 0; i < 10000; i++)
		{
			Columns h = sample(types, URandomService.getURandomService(), null);
			households.put( h, households.get( h ) + 1 );
		}
		System.out.println("Households: " + households );
		assertTrue(households.get(B) > 2666);
		assertTrue(households.get(B) < 4000);
		assertTrue(households.get(A) > 2666);
		assertTrue(households.get(A) < 4000);
		assertTrue(households.get(C) > 2666);
		assertTrue(households.get(C) < 4000);
	}

	@Test
	public void testConsumption()
	{
		Map<Columns, Integer> households = new HashMap<Columns, Integer>();
		households.put( A, 10 );
		households.put( B, 30 );
		households.put( C, 5 );
		
		Map<Columns, Integer> input = new HashMap<Columns, Integer>( households );
		assertEquals( households, input );
		Map<Columns,Integer> result = new HashMap<Columns, Integer>();
		while( true )
		{
			Columns type = consume(input, URandomService.getURandomService(),
					null);
			if( type == null ) {
				break;
			}
			if( ! result.containsKey( type )) {
				result.put( type, 1 );
			} else {
				result.put( type, result.get( type ) + 1 );
			}
		}
		assertEquals( households, result );
	}
	
	@Test
	public void testRates()
	{
		int p1 = applyProbability(100, 0.1, true,
				URandomService.getURandomService(), null);
		System.err.println( "P1: " + p1 );
		assertTrue( p1 < 20 );
		assertTrue(p1 > 0);
		int p1s = applyProbability(100, 0.1, false,
				URandomService.getURandomService(), null);
		assertEquals( 10, p1s );
	}
	
	@Test
	public void testScaling()
	{
		assertEquals( 0, scale( -10, 0, 1 ), 0.0001 );
		assertEquals( 0, scale( 0, 0, 1 ), 0.0001 );
		assertEquals( 1, scale( 1, 0, 1 ), 0.0001 );
		assertEquals( 1, scale( 2, 0, 1 ), 0.0001 );
		
		assertEquals( 1, scale( 0, 1, 0 ), 0.0001 );
		assertEquals( 0, scale( 1, 1, 0 ), 0.0001 );
		
		assertEquals( 0.5, scale( 1, 0, 2 ), 0.0001  );
	}
	
	@Test
	public void testMaxMin()
	{
		Map<String, Integer> scores = new HashMap<String, Integer>();
		assertNull( getMaximum( scores ) );
		scores.put( "A", 1 );
		scores.put( "B", 2 );
		scores.put( "C", 3 );
		
		assertEquals( "A", getMinimum( scores ));
		assertEquals( "C", getMaximum( scores ));
		
		Map<String,Double> s = new HashMap<String, Double>();
		assertNull( getMaximum( s) );
		s.put( "D", 0.001 );
		s.put( "E", 0.002 );
		s.put( "F", 0.003 );
		
		assertEquals( "D", getMinimum( s ));
		assertEquals( "F", getMaximum( s ));
	}
	
	@Test
	public void testSortingHash()
	{
		Map<String, Integer> scores = new HashMap<String, Integer>();
		scores.put(  "A", 1 );
		scores.put(  "B", 2 );
		scores.put(  "C", 3 );
		List<String> expected = new ArrayList<String>( Arrays.asList("C","B","A"));
		assertEquals( expected, sortMap( scores ));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSamplingRates()
	{
		for( int i = 0; i < 1000; i++ ) {
			assertEquals( 1, sampleRate( 1, URandomService.getURandomService(), null) );
		}
		for( int i = 0; i < 1000; i++ ) {
			assertThat( sampleRate( 6.5, URandomService.getURandomService(), null), anyOf( equalTo( 6 ), equalTo( 7 ) ) );
		}
	}
	
	@Test
	public void testIntegerRandom()
	{
		for( int j = 0; j < 10; j++ )
		{
		int start = (int)(( Math.random() - 0.5 ) * 10 );
		int end = start + (int)(( Math.random() ) * 10 );
		for( int i = 0; i < 1000; i++ )
		{
				int val = nextIntFromTo(start, end,
						URandomService.getURandomService(), null);
			assertTrue( start + "<=" + val + "<" + end, val >= start );
			assertTrue( start + "<=" + val + "<" + end, val <= end );
		}
		}
	}
}
