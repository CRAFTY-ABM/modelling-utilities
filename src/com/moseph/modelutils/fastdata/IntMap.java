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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
	
	@Override
	public void clear()
	{
		Arrays.fill( data, initial );
		total = initial * size();
		dirty();
	}
	
	public int getTotal()
	{
		if( dirtyTotal ) {
			updateTotals();
		}
		return total;
	}
	
	@Override
	void updateTotals()
	{
		total = 0;
		for( int v : data ) {
			total += v;
		}
		dirtyTotal = false;
	}
	
	@Override
	void updateAverage()
	{
		average = (double)getTotal() / size();
	}
	
	public void copyInto( IntMap<T> target )
	{
		for( T k : indexes ) {
			target.put(k, data[k.getIndex()] );
		}
	}
	
	public void copyFrom( IntMap<T> source )
	{
		source.copyInto( this );
	}
	
	public void addInto( IntMap<T> target )
	{
		for( T k : indexes ) {
			target.add(k, data[k.getIndex()] );
		}
	}
	
	@Override
	public T sample()
	{
		//int num = nextIntFromTo( 0, getTotal()-1 );
		int num = 0;
		int cur = 0;
		for( T k : getKeys() )
		{
			cur += get( k );
			if( cur > num ) {
				return k;
			}
		}
		return null;
	}
	
	public T consume()
	{
		T val = sample();
		if( val != null ) {
			add( val, -1 );
		}
		return val;
	}
	
	@Override
	public Map<T, Integer> toMap()
	{
		// TODO check if LinkedHashMap required
		Map<T, Integer> map = new LinkedHashMap<T, Integer>();
		for( T k : getKeys() ) {
			map.put( k, get(k) );
		}
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

	@Override
	public double getDoubleTotal()
	{
		return getTotal();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("IntMap: ");
		int count = 0;
		for (T k : getKeys()) {
			count++;
			buffer.append(k + " = " + get(k)
					+ (count <= this.maxIndex ? ", " : ""));
		}
		return buffer.toString();
	}
}
