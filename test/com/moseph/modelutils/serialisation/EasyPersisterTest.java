package com.moseph.modelutils.serialisation;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.log4j.*;
import org.junit.*;

import com.moseph.modelutils.fastdata.*;
import com.moseph.modelutils.serialisation.EasyPersisterTest.Col;
import com.moseph.modelutils.serialisation.EasyPersisterTest.Row;

public class EasyPersisterTest
{

	@Test
	public void testReadingMatrix() throws IOException
	{
		EasyPersister pers = new EasyPersister();
		pers.setBaseDir( "test-data" );
		DoubleMatrix<Col, Row> mat = pers.csvToMatrix( "testMatrix.csv", cols, rows );
		assertEquals(1, mat.get( A, X  ), 0.0001 );
		assertEquals(2, mat.get( B, X  ), 0.0001 );
		assertEquals(3, mat.get( C, X  ), 0.0001 );
		assertEquals(4, mat.get( A, Y  ), 0.0001 );
		assertEquals(5, mat.get( B, Y  ), 0.0001 );
		assertEquals(6, mat.get( C, Y  ), 0.0001 );
		assertEquals(7, mat.get( A, Z  ), 0.0001 );
		assertEquals(8, mat.get( B, Z  ), 0.0001 );
		assertEquals(9, mat.get( C, Z  ), 0.0001 );
	}
	
	@Test
	public void testReadingDoubleMap() throws IOException
	{
		EasyPersister pers = new EasyPersister();
		pers.setBaseDir( "test-data" );
		DoubleMap<Row> mat = pers.csvToDoubleMap( "testMatrix.csv", rows, "A" );
		assertEquals( 3, rows.getMaxIndex() );
		assertEquals(1, mat.get( X  ), 0.0001 );
		assertEquals(4, mat.get( Y  ), 0.0001 );
		assertEquals(7, mat.get( Z  ), 0.0001 );
		
	}

	public static class NamedInd implements Named, Indexed
	{
		int index;
		String name;
		public NamedInd( String name, int index )
		{
			this.name = name;
			this.index = index;
		}

		public int getIndex() { return index; } 
		public String getName() { return name; }
		
	}
	
	public static class Row extends NamedInd 
	{
		public Row( String name, int index ) { super( name, index ); }
	}
	public static class Col extends NamedInd 
	{
		public Col( String name, int index ) { super( name, index ); }
	}
	
	public static Row X = new Row( "X", 1 ) ;
	public static Row Y = new Row("Y",2);
	public static Row Z = new Row("Z", 3);
	public static Col A = new Col("A",1);
	public static Col B = new Col("B",2);
	public static Col C = new Col("C",3);
	
	public static NamedArrayIndexSet<Row> rows = new NamedArrayIndexSet<Row>( X, Y, Z );
	public static NamedArrayIndexSet<Col> cols = new NamedArrayIndexSet<Col>( A, B, C );
}
