package com.moseph.modelutils.fastdata;

import java.util.*;

import static java.lang.Math.*;

/**
 * Simple Named IndexSet. Not modifiable after creation
 * Uses an ArrayList for backing, so quick and cheap. Uses a HashSet to test for containment, and a
 * hash map to get byName
 * @author dmrust
 *
 * @param <T>
 */
public class NamedArrayIndexSet<T extends Indexed & Named> extends ArrayIndexSet<T> implements NamedIndexSet<T>
{
	Map<String,T> byName = new HashMap<String,T>();
	
	public NamedArrayIndexSet( Collection<? extends T> values )
	{
		super( values );
		for( T t : list ) byName.put( t.getName(), t );
	}
	
	public NamedArrayIndexSet( T... values )
	{
		this( Arrays.asList(values));
	}

	public T forName( String name ) { return byName.get( name ); }
	public boolean contains( String name ) { return byName.containsKey( name ); }
	public Set<String> names() { return byName.keySet(); }
}
