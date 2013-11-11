package com.moseph.modelutils.fastdata;

import java.util.*;

/**
 * Intended to form a Map-like structure for storing ints, which avoides autoboxing
 * and takes advantage of structure in the key set (consecutive indices!)
 * 
 * Essentially, syntactic sugar around using arrays for defined sets of keys which have unique ids
 * @author dmrust
 *
 * @param <T>
 */
public class IntMap<T extends Indexed> extends AbstractNumberMap<T>
{
	int[] data;
	int initial = 0;
	int total;

	public IntMap( Collection<? extends T> indexes )
	{
		super( indexes );
		data = new int[size()];
		clear();
	}
	
	public IntMap( Collection<? extends T> indexes, int initial )
	{
		this( indexes );
		this.initial = initial;
		clear();
	}
	
	public int get( T key )
	{
		return data[key.getIndex()];
	}
	
	public void put( T key, int value )
	{
		data[key.getIndex()] = value;
		dirty();
	}
	
	public void increment( T key )
	{
		add( key, 1 );
	}
	
	public void add( T key, int amount )
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
	
	public int getTotal()
	{
		if( dirtyTotal ) updateTotals();
		return total;
	}
	
	void updateTotals()
	{
		total = 0;
		for( int v : data ) total += v;
		dirtyTotal = false;
	}
	
	void updateAverage()
	{
		average = (double)getTotal() / size();
	}
	
	public void copyInto( IntMap<T> target )
	{
		for( T k : indexes ) target.put(k, data[k.getIndex()] );
	}
	
	public void copyFrom( IntMap<T> source )
	{
		source.copyInto( this );
	}
	
	public void addInto( IntMap<T> target )
	{
		for( T k : indexes ) target.add(k, data[k.getIndex()] );
	}
	
	public T sample()
	{
		//int num = nextIntFromTo( 0, getTotal()-1 );
		int num = 0;
		int cur = 0;
		for( T k : getKeys() )
		{
			cur += get( k );
			if( cur > num ) return k;
		}
		return null;
	}
	
	public T consume()
	{
		T val = sample();
		if( val != null ) add( val, -1 );
		return val;
	}
	
	public String toString()
	{
		return toMap().toString();
	}
	
	public Map<T, Integer> toMap()
	{
		Map<T, Integer> map =new HashMap<T, Integer>();
		for( T k : getKeys() ) map.put( k, get(k) );
		return map;
	}

	@Override
	public void addDouble( T key, double value )
	{
		add( key, (int)value );
	}

	@Override
	public double getDouble( T key )
	{
		return get( key );
	}

	@Override
	public void putDouble( T key, double value )
	{
		put( key, (int)value );
	}

	public double getDoubleTotal()
	{
		return getTotal();
	}

}
