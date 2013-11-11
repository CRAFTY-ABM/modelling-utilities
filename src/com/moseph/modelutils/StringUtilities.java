package com.moseph.modelutils;

import java.util.*;

public class StringUtilities
{
	
	
	public static <T> String join( Collection<T> t )
	{
		return join( t, ", " );
	}
	
	public static String join( String delimiter, double...strings )
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < strings.length; i++ )
		{
			sb.append( strings[i] );
			if( ( i < strings.length-1 )) sb.append( delimiter );
		}
		return sb.toString();
	}
	
	public static <T> String join( Collection<T> t, String delimiter )
	{
		StringBuffer sb = new StringBuffer();
		Iterator<T> it = t.iterator();
		while( it.hasNext() )
		{
			sb.append( it.next().toString() );
			if( it.hasNext() ) sb.append( delimiter );
		}
		return sb.toString();
	}
}
