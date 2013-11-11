package com.moseph.modelutils.fastdata;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import static com.moseph.modelutils.fastdata.Columns.*;
import static com.moseph.modelutils.fastdata.Rows.*;

public class DoubleMatrixTest
{
	Columns[] cs = { Columns.A, Columns.B, Columns.C, Columns.D };
	Rows[] rs = { Rows.X, Rows.Y, Rows.Z };
	Columns A = Columns.A;
	Columns B = Columns.B;
	Columns C = Columns.C;
	Columns D = Columns.D;
	Rows X = Rows.X;
	Rows Y = Rows.Y;
	Rows Z = Rows.Z;
	
	@Test
	public void testBasicOperations()
	{
		//Should become:
		//  A B C D
		//X 1 2 3 4
		//Y 5 6 7 8
		//Z 9 101112
		DoubleMatrix<Columns, Rows> d = new DoubleMatrix<Columns, Rows>( cols, rows );
		fill(d, new double[][] {
			{ 1, 2, 3, 4 },
			{ 5 ,6, 7, 8 },
			{ 9, 10, 11, 12 }
		});
		
		assertEquals( 6, d.get( B, Y ), 0.0001 );
		assertEquals( 12, d.get( D, Z ), 0.0001 );
		assertEquals( 15, d.getColumnTotal( A ), 0.0001 );
		assertEquals( 5, d.getColAverage( A ), 0.0001 );
		assertEquals( 6, d.getColAverage( B ), 0.0001 );
		assertEquals( 10, d.getRowTotal( X ), 0.0001 );
		
		DoubleMatrix<Columns, Rows> d2 = new DoubleMatrix<Columns, Rows>( cols, rows );
		fill(d2, new double[][] {
			{ 1, 1, 1, 1 },
			{ 1 ,1, 1, 1 },
			{ 1, 1, 1, 1 }
		});
		d2.addInto( d );
		
		assertEquals( 7, d.get( B, Y ), 0.0001 );
		assertEquals( 13, d.get( D, Z ), 0.0001 );
		assertEquals( 18, d.getColumnTotal( A ), 0.0001 );
		assertEquals( 6, d.getColAverage( A ), 0.0001 );
		assertEquals( 7, d.getColAverage( B ), 0.0001 );
		assertEquals( 14, d.getRowTotal( X ), 0.0001 );
	}
	
	@Test 
	public void testWeightedSums()
	{
		DoubleMatrix<Columns, Rows> d = new DoubleMatrix<Columns, Rows>( cols, rows );
		fill(d, new double[][] {
			{ 1, 2, 3, 4 },
			{ 5 ,6, 7, 8 },
			{ 9, 10, 11, 12 }
		});
		
		assertEquals( 10, d.getWeightedRowTotal( X ), 0.001 );
		DoubleMap<Columns> colW = new DoubleMap<Columns>( cols );
		d.setColumnWeightings( colW );
		assertEquals( 0, d.getWeightedRowTotal( X ), 0.001 );
		colW.put( A, 2 );
		d.dirtyWeightedTotal = true;
		assertEquals( 2, d.getWeightedRowTotal( X ), 0.001 );
	}
	
	@Test
	public void testExtraction()
	{
		DoubleMatrix<Columns, Rows> d = new DoubleMatrix<Columns, Rows>( cols, rows );
		fill(d, new double[][] {
				{ 1, 2, 3, 4 },
				{ 5 ,6, 7, 8 },
				{ 9, 10, 11, 12 }
			});
		DoubleMap<Columns> rowX = d.getRow( Y );
		assertEquals( 5, rowX.get( A ), 0.0001 );
		assertEquals( 6, rowX.get( B ), 0.0001 );
		assertEquals( 7, rowX.get( C ), 0.0001 );
		
		DoubleMap<Rows> colB = d.getColumn( B );
		assertEquals( 2, colB.get( X ), 0.0001 );
		assertEquals( 6, colB.get( Y ), 0.0001 );
		assertEquals( 10, colB.get( Z ), 0.0001 );
	}



	
	public void fill( DoubleMatrix<Columns, Rows> map, double[][] values )
	{
		for( int i = 0; i < values.length; i++ )
		{
			double[] v = values[i];
			Rows r = rs[i];
			for( int j = 0; j < v.length; j++ )
			{
				Columns c = cs[j];
				map.put( c, r, values[i][j] );
			}
		}
	}
	
}
