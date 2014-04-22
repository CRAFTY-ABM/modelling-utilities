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


import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Intended to form a Map-like structure for storing doubles, which avoides autoboxing
 * and takes advantage of structure in the key set (consecutive indices!)
 * 
 * Essentially, syntactic sugar around using arrays for defined sets of keys which have unique ids
 * @author dmrust
 *
 * @param <T>
 */
public class DoubleMatrix<T extends Indexed, S extends Indexed>
{
	double[][] data;
	int numRows;
	int numCols;
	IndexSet<T> colIndexes;
	IndexSet<S> rowIndexes;
	double initial = 0;
	DoubleMap<T> colTotals;
	DoubleMap<S> rowTotals;
	boolean dirtyTotal;
	boolean dirtyMaxMin;
	boolean dirtyWeightedTotal;
	double total;
	double average;
	T maxCol;
	T minCol;
	S maxRow;
	S minRow;
	NumberMap<T> colWeightings;
	NumberMap<S> rowWeightings;
	DoubleMap<T> colWeightedTotals;
	DoubleMap<S> rowWeightedTotals;
	private double vMin;
	private double vMax;
	
	public DoubleMatrix( Collection<? extends T> colIndexes, Collection<? extends S> rowIndexes )
	{
		this( colIndexes, rowIndexes, 0 );
	}
	
	public DoubleMatrix( Collection<? extends T> colIndexes, Collection<? extends S> rowIndexes, double initial )
	{
		this( new ArrayIndexSet<T>( colIndexes ), new ArrayIndexSet<S>( rowIndexes ), initial );
	}
	
	public DoubleMatrix( IndexSet<T> colIndexes, IndexSet<S> rowIndexes )
	{
		this( colIndexes, rowIndexes, 0 );
	}
	
	public DoubleMatrix( IndexSet<T> colIndexes, IndexSet<S> rowIndexes, double initial )
	{
		this.colIndexes = colIndexes;
		this.rowIndexes = rowIndexes;
		this.initial = initial;
		numRows = rowIndexes.getMaxIndex() + 1;
		numCols = colIndexes.getMaxIndex() + 1;
		data = new double[numCols][numRows];
		colTotals = new DoubleMap<T>( colIndexes );
		rowTotals = new DoubleMap<S>( rowIndexes );
		colWeightedTotals = new DoubleMap<T>( colIndexes );
		rowWeightedTotals = new DoubleMap<S>( rowIndexes );
		
		clear();
	}
	
	public IndexSet<T> cols() { return colIndexes; }
	public IndexSet<S> rows() { return rowIndexes; }
	
	public double get( int col, int row )
	{
		return data[col][row];
	}
	public double get( T col, S row )
	{
		return data[col.getIndex()][row.getIndex()];
	}
	
	public double[][] getAll()
	{
		return data;
	}
	
	public void put( T col, S row, double value )
	{
		data[col.getIndex()][row.getIndex()] = value;
		dirty();
	}
	
	public void put( int col, int row, double value )
	{
		data[col][row] = value;
		dirty();
	}
	
	public void put( double[][] in )
	{
		for( int i = 0; i < data.length; i++ ) {
			System.arraycopy( in[i], 0, data[i], 0, data[i].length );
		}
	}
	
	/**
	 * Transposed version of put, if it is easer to have the input data in transposed form
	 * @param in
	 */
	public void putT( double[][] in )
	{
		for(int i = 0; i < colIndexes.size(); i++ ) {
			for( int j = 0; j < rowIndexes.size(); j++ ) {
				data[i][j] = in[j][i];
			}
		}
	}
	
	public void increment( T col, S row )
	{
		add( col, row, 1 );
	}
	
	public void add( T col, S row, double amount )
	{
		data[col.getIndex()][row.getIndex()] += amount;
		dirty();
	}
	
	
	public void clear()
	{
		for( double[] d : data ) {
			Arrays.fill( d, initial );
		}
		total = initial * size();
		dirty();
	}
	
	public int size()
	{
		return (numCols) * (numRows);
	}
	
	public double getTotal()
	{
		if( dirtyTotal ) {
			updateTotals();
		}
		return total;
	}
	
	public double getRowTotal( S row )
	{
		if( dirtyTotal ) {
			updateTotals();
		}
		return rowTotals.get( row );
	}
	
	public double getColumnTotal( T col )
	{
		if( dirtyTotal ) {
			updateTotals();
		}
		return colTotals.get( col );
	}
	
	public double getAverage()
	{
		return getTotal() / size();
	}
	
	public double getColAverage( T col )
	{
		return getColumnTotal( col ) / numRows;
	}
	
	public double getRowAverage( S row )
	{
		return getRowTotal( row ) / numCols;
	}
	
	public double getWeightedColAverage( T col )
	{
		if( rowWeightings != null ) {
			return getWeightedColTotal( col );
		}
		return getColAverage( col );
	}
	
	public double getWeightedRowAverage( S row )
	{
		if( colWeightings != null ) {
			return getWeightedRowTotal( row ) / colWeightings.getDoubleTotal();
		}
		return getRowAverage( row );
	}
	
	public DoubleMap<T> getWeightedColTotals()
	{
		if( dirtyWeightedTotal ) {
			updateWeightedTotals();
		}
		return colWeightedTotals;
	}
	
	public double getWeightedColTotal( T col )
	{
		if( dirtyWeightedTotal ) {
			updateWeightedTotals();
		}
		return colWeightedTotals.get( col );
	}
	
	public DoubleMap<S> getWeightedRowTotals()
	{
		if( dirtyWeightedTotal ) {
			updateWeightedTotals();
		}
		return rowWeightedTotals;
	}
	public double getWeightedRowTotal( S row )
	{
		if( dirtyWeightedTotal ) {
			updateWeightedTotals();
		}
		return rowWeightedTotals.get( row );
	}
	
	public T getMaxCol()
	{
		if( dirtyMaxMin ) {
			updateMaxMin();
		}
		return maxCol;
	}
	
	public T getMinCol()
	{
		if( dirtyMaxMin ) {
			updateMaxMin();
		}
		return minCol;
	}
	
	public S getMaxRow()
	{
		if( dirtyMaxMin ) {
			updateMaxMin();
		}
		return maxRow;
	}
	
	public S getMinRow()
	{
		if( dirtyMaxMin ) {
			updateMaxMin();
		}
		return minRow;
	}
	
	public double getMax()
	{
		if( dirtyMaxMin ) {
			updateMaxMin();
		}
		return vMax;
	}
	
	public double getMin()
	{
		if( dirtyMaxMin ) {
			updateMaxMin();
		}
		return vMin;
	}
	
	void updateTotals()
	{
		total = 0;
		colTotals.clear();
		rowTotals.clear();
		for( T c : colIndexes ) {
			for( S r : rowIndexes )
			{
				double value = get( c, r );
				colTotals.add( c, value );
				rowTotals.add( r, value );
				total += value;
			}
		}
		dirtyTotal = false;
	}
	
	public void updateWeightedTotals()
	{
		colWeightedTotals.clear();
		rowWeightedTotals.clear();
		for( T c : colIndexes ) {
			for( S r : rowIndexes )
			{
				double value = get( c, r );
				double rowWeight = 1;
				double colWeight = 1;
				if( rowWeightings != null )
				{
					rowWeight = rowWeightings.getDouble( r );
				}
				if( colWeightings != null ) 
				{
					colWeight = colWeightings.getDouble( c );
				}
				colWeightedTotals.add( c, value * rowWeight * colWeight );
				rowWeightedTotals.add( r, value * rowWeight * colWeight );
			}
		}
		dirtyWeightedTotal = false;
	}
	
	void updateMaxMin()
	{
		vMax = -Double.MAX_VALUE;
		vMin = Double.MAX_VALUE;
		for( T col : colIndexes ) {
			for( S row : rowIndexes )
		{
			double val = data[col.getIndex()][row.getIndex()];
			if( val > vMax )
			{
				vMax = val;
				maxCol = col;
				maxRow = row;
			}
			if( val < vMin )
			{
				vMin = val;
				minCol = col;
				minRow = row;
			}
		}
		}
	}
	
	
	public void dirty()
	{
		dirtyTotal = true;
		dirtyMaxMin = true;
		dirtyWeightedTotal = true;
	}
	
	public void copyInto( DoubleMatrix<T,S> target )
	{
		for( T col : colIndexes ) {
			for( S row : rowIndexes ) {
				target.put(col, row, get( col, row ) );
			}
		} 
	}
	
	public void copyFrom( DoubleMatrix<T,S> source )
	{
		source.copyInto( this );
	}
	
	public void addInto( DoubleMatrix<T,S> target )
	{
		for( T col : colIndexes ) {
			for( S row : rowIndexes ) {
				target.add(col, row, get( col, row ) );
			}
		} 
	}
	
	public void copyInto( Map<T, Map<S, Double>> target )
	{
		for( T col : colIndexes )
		{
			// TODO check if LinkedHashMap required
			if (!target.containsKey(col)) {
				target.put( col, new LinkedHashMap<S, Double>() );
			}
			for( S row : rowIndexes ) {
				target.get( col ).put( row, get( col, row ) );
			}
		}
	}
	
	public void copyFrom( Map<? extends T, Map<? extends S, Double>> source )
	{
		for( T col : colIndexes )
		{
			if( ! colIndexes.containsKey( col )) {
				continue;
			}
			for( S row : rowIndexes )
			{
				if( ! rowIndexes.containsKey( row )) {
					continue;
				}
					put(col, row, source.get( col ).get( row ) );
			}
		}
	}
	
	@Override
	public String toString()
	{
		return toMap().toString();
	}
	
	public Map<T, Map<S,Double>> toMap()
	{
		// TODO check if LinkedHashMap required
		Map<T, Map<S, Double>> map = new LinkedHashMap<T, Map<S, Double>>();
		copyInto( map );
		return map;
	}
	
	public void setColumnWeightings( NumberMap<T> colW )
	{
		dirtyWeightedTotal = true;
		colWeightings = colW;
	}
	
	public void setRowWeightings( NumberMap<S> rowW )
	{
		dirtyWeightedTotal = true;
		rowWeightings = rowW;
	}

	public int getNumCols()
	{
		return numCols;
	}

	public int getNumRows()
	{
		return numRows;
	}
	
	public DoubleMap<S> getColumn( T column )
	{
		DoubleMap<S> col = new DoubleMap<S>( rowIndexes );
		for( S s : rowIndexes ) {
			col.put( s, get( column, s ) );
		}
		return col;
	}
	
	public DoubleMap<T> getRow( S row )
	{
		DoubleMap<T> ret = new DoubleMap<T>( colIndexes );
		for( T t : colIndexes ) {
			ret.put( t, get( t, row ) );
		}
		return ret;
	}
	
	public T getColKey( int index ) { return colIndexes.get( index ); }
	public S getRowKey( int index ) { return rowIndexes.get( index ); }
	
	/**
	 * Creates a new matrix with the same structure (but no data)
	 * @return
	 */
	public DoubleMatrix<T, S> duplicate()
	{
		return new DoubleMatrix<T, S>( colIndexes, rowIndexes );
	}
	
	public String prettyPrint()
	{
		String colTitle = "RowNames";
		int width = colTitle.length();
		int pres = 1;
		double max = getMax();
		double min = getMin();
		System.out.println("Max: " + max + " Min: " + min);
		if( abs(max )< 1 || ( abs(min) < 1 && abs(min) > 0 )) {
			pres = width-3;
		}
		for( T c : colIndexes ) {
			width = max(width,(c+"").length());
		}
		for( S r : rowIndexes ) {
			width = max(width,(r+"").length());
		}
		System.out.println("Width: " + width);
		String stringF = "| %"+width+"s ";
		String doubleF = "| %"+width+"."+pres+"f ";
		//String doubleF = "%9.3f";
		String lineEnd = " |\n";
		StringBuffer out = new StringBuffer(String.format( stringF, "Rownames" ));
		for( T c : colIndexes ) {
			out.append( String.format( stringF, c.toString() ) );
		}
		out.append(lineEnd);
		for( S r : rowIndexes ) 
		{
			out.append(String.format( stringF, r.toString()));
			for( T c : colIndexes ) {
				out.append( String.format( doubleF, get(c,r) ) );
			}
			out.append(lineEnd);
		}
		return out.toString();
	}
}
