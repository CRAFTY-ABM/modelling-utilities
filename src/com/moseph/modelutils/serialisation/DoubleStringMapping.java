package com.moseph.modelutils.serialisation;

import org.simpleframework.xml.*;

import com.moseph.modelutils.fastdata.*;

/**
 * Utility class for serialising multiple level maps in a type-safe way
 * @author dmrust
 *
 * @param <T>
 * @param <S>
 */
@Root
public class DoubleStringMapping
{
	@Attribute
	public String first;
	@Attribute
	public String second;
	@Attribute
	public double value;
	
	public DoubleStringMapping() {}
	
	public DoubleStringMapping( Named first, Named second, double value )
	{
		this( first.getName(), second.getName(), value );
	}
	public DoubleStringMapping( String first, String second, double value )
	{
		super();
		this.first = first;
		this.second = second;
		this.value = value;
	}
	
}