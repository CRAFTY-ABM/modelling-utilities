package com.moseph.modelutils.fastdata;

import java.util.*;

import org.junit.*;

import static com.moseph.modelutils.Utilities.*;

import static java.lang.Math.exp;
import static org.junit.Assert.*;
import static com.moseph.modelutils.fastdata.Columns.*;

public class IntMapTest 
{
	@Test
	public void testBasicFunctions()
	{
		IntMap<Columns> t = new IntMap<Columns>( cols );
		t.put( A, 4 );
		assertEquals( 4, t.get( A ));
		assertEquals( 4, t.getTotal() );
		//4 Household types to start with...
		assertEquals( 5, t.size() );
		assertEquals( 4.0/5.0, t.getAverage(), 0.0001 );
		assertEquals( A, t.getMax() );
		//Undefined - could be any of the other household types...
		assertFalse(  t.getMin() == A );
		
		t.put( B, 3 );
		assertEquals( 4, t.get( A ));
		assertEquals( 3, t.get( B ));
		assertEquals( 7, t.getTotal() );
		assertEquals( 5, t.size() );
		assertEquals( 7.0/5.0, t.getAverage(), 0.0001 );
		assertEquals( A, t.getMax() );
		//Undefined - could be any of the other household types...
		assertFalse(  t.getMin() == A );
		assertFalse(  t.getMin() == B );
		
		t.put( C, 6 );
		assertEquals( 6, t.get( C ));
		assertEquals( 13, t.getTotal() );
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
		assertEquals( 18+5+6+3+4, t.getTotal() );
		
		Map<Columns, Integer> expected = new HashMap<Columns, Integer>();
		expected.put( A, 4 );
		expected.put( B, 3 );
		expected.put( E, 18 );
		expected.put( D, 5 );
		expected.put( C, 6 );
		assertEquals( expected, t.toMap() );
		t.clear();
		assertEquals( 0, t.getTotal() );
	}
	
	@Test
	public void testConsumeIncrement()
	{
		IntMap<Columns> t1 = new IntMap<Columns>( cols );
		IntMap<Columns> t2 = new IntMap<Columns>( cols );
		IntMap<Columns> initial = new IntMap<Columns>( cols );
		IntMap<Columns> sum = new IntMap<Columns>( cols );
		for( Columns type : cols )
			initial.put( type, nextIntFromTo(0, 30 ) );
		initial.copyInto( t1 );
		assertEquals( initial.getTotal(), t1.getTotal() );
		for( int i = 0; i < initial.getTotal(); i++ )
		{
			Columns type = t1.consume();
			assertNotNull( type );
			t2.increment( type );
			t1.copyInto( sum );
			t2.addInto( sum );
			assertEquals( initial.getTotal(), sum.getTotal() );
			assertEquals( initial.toMap(), sum.toMap() );
		}
		assertEquals( 0, t1.getTotal() );
		assertEquals( initial.getTotal(), t2.getTotal() );
	}

}
