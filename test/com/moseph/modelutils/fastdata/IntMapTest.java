 /**
 * This file is part of
 * 
 * ModellingUtilities
 *
 * Copyright (C) 2014 School of GeoScience, University of Edinburgh, Edinburgh, UK
 * 
 * ModellingUtilities is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * ModellingUtilities is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * School of Geoscience, University of Edinburgh, Edinburgh, UK
 * 
 */
package com.moseph.modelutils.fastdata;

import static com.moseph.modelutils.Utilities.nextIntFromTo;
import static com.moseph.modelutils.fastdata.Columns.A;
import static com.moseph.modelutils.fastdata.Columns.B;
import static com.moseph.modelutils.fastdata.Columns.C;
import static com.moseph.modelutils.fastdata.Columns.D;
import static com.moseph.modelutils.fastdata.Columns.E;
import static com.moseph.modelutils.fastdata.Columns.cols;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.cesr.uranus.core.URandomService;

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
		for( Columns type : cols ) {
			initial.put( type, nextIntFromTo(0, 30 , URandomService.getURandomService(), null) );
		}
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
