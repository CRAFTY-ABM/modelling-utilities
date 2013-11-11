package com.moseph.modelutils.serialisation;

import java.util.*;

import org.simpleframework.xml.*;

/**
 * Utility class for serialising multiple level maps in a type-safe way
 * @author dmrust
 *
 * @param <T>
 * @param <S>
 */
@Root
public class DoubleNumericMapping<T,S>
{
	@Element
	public T first;
	@Element
	public S second;
	@Attribute
	double value;
	
	public DoubleNumericMapping() {}
	
	public DoubleNumericMapping( T first, S second, double value )
	{
		super();
		this.first = first;
		this.second = second;
		this.value = value;
	}

	public void insert( Map<T,Map<S,Double>> target )
	{
		if( ! target.containsKey( first )) target.put( first, new HashMap<S, Double>() );
		target.get( first ).put( second, value );
	}
	
}