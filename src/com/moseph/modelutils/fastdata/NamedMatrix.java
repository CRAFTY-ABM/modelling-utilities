package com.moseph.modelutils.fastdata;

import java.io.IOException;

import java.util.*;

import com.csvreader.*;
import com.moseph.modelutils.serialisation.DoubleStringMapping;


public class NamedMatrix<T extends Indexed & Named, S extends Indexed & Named> extends DoubleMatrix<T,S>
{
	NamedIndexSet<T> colIndexes;
	NamedIndexSet<S> rowIndexes;
	public NamedMatrix( Collection<? extends T> colIndexes, Collection<? extends S> rowIndexes )
	{
		this( colIndexes, rowIndexes, 0 );
	}

	public NamedMatrix( Collection<? extends T> colIndexes, Collection<? extends S> rowIndexes, double value )
	{
		this( new NamedArrayIndexSet<T>( colIndexes ), new NamedArrayIndexSet<S>( rowIndexes ), value );
	}
	
	public NamedMatrix( Collection<? extends T> colIndexes, Collection<? extends S> rowIndexes, CsvReader values ) throws NumberFormatException, IOException
	{
		this( colIndexes, rowIndexes, 0 );
		readCSV( values );
	}
	
	public NamedMatrix( NamedIndexSet<T> colIndexes, NamedIndexSet<S> rowIndexes, double initial )
	{
		super( colIndexes, rowIndexes, initial );
		this.colIndexes = colIndexes;
		this.rowIndexes = rowIndexes;
	}
	
	
	public void readCSV( CsvReader values ) throws NumberFormatException, IOException
	{
		while( values.readRecord() )
		{
			String row = values.get( 0 );
			if( ! rowIndexes.contains( row ))
			{
				//log.error( "Asked for unknown row: " + row );
				continue;
			}
			String[] headers = values.getHeaders();
			for( int i = 1; i < headers.length; i++ )
			{	
				String col = headers[i];
				if( ! colIndexes.contains( col ) )
				{
					//log.error( "Asked for unknown column: " + col );
					continue;
				}
				if( values.get(col).length() > 0)
					put( col, row, Double.parseDouble( values.get( col )));
			}
		}
	}
	
	public List<DoubleStringMapping> toStringMappings()
	{
		List<DoubleStringMapping> list = new ArrayList<DoubleStringMapping>(numCols * numRows);
		for( T col : colIndexes ) for( S row : rowIndexes )
			if( get( col, row ) != initial )
				list.add( new DoubleStringMapping( col, row, get( col, row ) ) );
		return list;
	}
	

	public void put( String col, String row, double value )
	{
		put( colIndexes.forName( col ), rowIndexes.forName( row ), value );
	}
	
	public double get( String col, String row )
	{
		return get( colIndexes.forName( col ), rowIndexes.forName( row ) );
	}
	
	public void write( CsvWriter writer ) throws Exception
	{
		String[] output = new String[colIndexes.size()+2];
		output[0] = "";
		for( int i = 0; i < colIndexes.size(); i++ ) output[i+1] = colIndexes.get( i ).getName();
		output[colIndexes.size()+1] = "Total";
		writer.writeRecord( output );
		for( S row : rowIndexes )
		{
			output[0] = row.getName();
			for( int i = 0; i < colIndexes.size(); i++ ) output[i+1] = get( colIndexes.get(i), row ) + "";
			output[ colIndexes.size() + 1 ] = getRowTotal( row ) + "";
			writer.writeRecord( output );
		}
		output[0] = "Total";
		for( int i = 0; i < colIndexes.size(); i++ )
			output[i+1] = getColumnTotal( colIndexes.get( i ) ) + "";
		output[colIndexes.size()+1] = getTotal() + "";
		writer.writeRecord( output );
	}
}
