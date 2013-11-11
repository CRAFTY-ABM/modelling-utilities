package com.moseph.modelutils.fastdata;

import java.util.*;



import org.junit.Test;

import static org.junit.Assert.*;
import static com.moseph.modelutils.fastdata.Columns.*;

public class DoubleMapTest
{
	@Test
	public void testBasicFunctions()
	{
		DoubleMap<Columns> t = new DoubleMap<Columns>( cols );
		t.put( A, 4 );
		assertEquals( 4, t.get( A ), 0.0001 );
		assertEquals( 4, t.getTotal(), 0.0001  );
		//4 Household types to start with...
		assertEquals( 5, t.size() );
		assertEquals( 4.0/5.0, t.getAverage(), 0.0001 );
		assertEquals( A, t.getMax() );
		//Undefined - could be any of the other household types...
		assertFalse(  t.getMin() == A );
		
		t.put( B, 3 );
		assertEquals( 4, t.get( A ), 0.0001 );
		assertEquals( 3, t.get( B ), 0.0001 );
		assertEquals( 7, t.getTotal(), 0.0001  );
		assertEquals( 5, t.size() );
		assertEquals( 7.0/5.0, t.getAverage(), 0.0001 );
		assertEquals( A, t.getMax() );
		//Undefined - could be any of the other household types...
		assertFalse(  t.getMin() == A );
		assertFalse(  t.getMin() == B );
		
		t.put( C, 6 );
		assertEquals( 6, t.get( C ), 0.0001 );
		assertEquals( 13, t.getTotal(), 0.0001  );
		assertEquals( 5, t.size() );
		assertEquals( 13/5.0, t.getAverage(), 0.0001 );
		assertEquals( C, t.getMax() );
		//Undefined - could be any of the other household types...
		assertFalse(  t.getMin() == A );
		assertFalse(  t.getMin() == B );
		
		t.put( D, 5 );
		t.put( E, 18 );
		assertEquals( B, t.getMin() );
		assertEquals( E, t.getMax() );
		assertEquals( 18+5+6+3+4, t.getTotal(), 0.0001  );
		
		Map<Columns, Double> expected = new HashMap<Columns, Double>();
		expected.put( A, 4d );
		expected.put( B, 3d );
		expected.put( E, 18d );
		expected.put( D, 5d );
		expected.put( C, 6d );
		assertEquals( expected, t.toMap() );
		t.clear();
		assertEquals( 0, t.getTotal(), 0.0001  );
	}
	
}
