package com.moseph.modelutils.fastdata;


import java.util.*;


/**
 * Intended to form a Map-like structure for storing doubles, which avoides autoboxing
 * and takes advantage of structure in the key set (consecutive indices!)
 * 
 * Essentially, syntactic sugar around using arrays for defined sets of keys which have unique ids
 * @author dmrust
 *
 * @param <T>
 */
public class DoubleMap<T extends Indexed> extends AbstractNumberMap<T>
{
	double[] data;
	double initial = 0;
	double total;

	public DoubleMap( Collection<? extends T> ind )
	{
		this( ind, 0d );
	}
	
	public DoubleMap( Collection<? extends T> indexes, double initial )
	{
		this( new ArrayIndexSet<T>( indexes ), initial );
	}
	
	public DoubleMap( IndexSet<T> indexes)
	{
		this( indexes, 0 );
	}
	public DoubleMap( IndexSet<T> indexes, double initial )
	{
		super( indexes );
		this.initial = initial;
		data = new double[indexes.getMaxIndex()+1];
		clear();
	}
	
	public DoubleMap( IndexSet<T> indexes, double...values)
	{
		this( indexes, 0 );
		if( values.length != data.length )
			throw new RuntimeException("Wrong size array given to DoubleMap. Expecting "+ data.length+" got " + values.length);
		for( T t : indexes )
			data[t.getIndex()] = values[t.getIndex()];
	}
	
	public double get( T key )
	{
		return data[key.getIndex()];
	}
	
	public double[] getAll()
	{
		return data;
	}
	
	public void put( T key, double value )
	{
		data[key.getIndex()] = value;
		dirty();
	}
	
	public void put( double[] values )
	{
		if( values.length != data.length )
			throw new RuntimeException("Wrong length array passed to DoubleMap. Got " + values.length + " expected " + data.length);
		System.arraycopy( values, 0, data, 0, data.length );
	}
	
	public void increment( T key )
	{
		add( key, 1 );
	}
	
	public void add( T key, double amount )
	{
		data[key.getIndex()] += amount;
		dirty();
	}
	
	public void clear()
	{
		Arrays.fill( data, initial );
		total = initial * size();
		dirty();
	}
	
	public double getTotal()
	{
		if( dirtyTotal ) updateTotals();
		return total;
	}
	
	void updateTotals()
	{
		total = 0;
		for( double v : data ) total += v;
		dirtyTotal = false;
	}
	
	void updateAverage()
	{
		average = (double)getTotal() / size();
	}
	
	
	public Map<T, Double> toMap()
	{
		Map<T, Double> map =new HashMap<T, Double>();
		for( T k : getKeys() ) map.put( k, get(k) );
		return map;
	}
	
	public void addDouble( T key, double value ) { add( key, value ); }
	public double getDouble( T key ) { return get( key ); }
	public void putDouble( T key, double value ) { put( key, value ); }
	public double getDoubleTotal() { return getTotal(); }
	
	/**
	 * Creates a DoubleMap with the same structure, but no data in
	 * @return
	 */
	public DoubleMap<T> duplicate() { return new DoubleMap<T>( indexes, initial ); }
	/**
	 * Creates a copy of this, with the same data in
	 * @return
	 */
	public DoubleMap<T> copy() 
	{ 
		DoubleMap<T> ret = new DoubleMap<T>( indexes, data ); 
		ret.initial = initial;
		return ret;
	}
	
	public void setMin( double value )
	{
		for( int i = 0; i < data.length; i++ )
			data[i] = Math.max( value, data[i] );
	}
	
}
