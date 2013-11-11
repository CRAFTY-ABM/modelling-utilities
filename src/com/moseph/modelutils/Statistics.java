package com.moseph.modelutils;

import java.util.Arrays;

public class Statistics
{

	/**
	 * From Ogwang, 2000 "A convenient method of computing the Gini index and its standard error"
	 * G = (2/n)a - 1 - 1/n
	 * a = sum_n(i*x_i)/sum_n(x_i)
	 * @param values
	 * @return
	 */
	public static double calculateGini( double[] values )
	{
		return calculateGini( values, 0 );
	}
	
	public static double calculateGini(  double[] values, double offset )
	{
		Arrays.sort( values );
		double atop = 0;
		double abot = 0;
		double n = values.length;
		for( int i = 0; i< values.length; i++ )
		{
			atop += i * ( values[i] + offset );
			abot += values[i] + offset;
		}
		if( abot == 0 ) return 0;
		double gini = (2/n) * (atop/abot) - 1 + 1/n;
		return gini; 
	}
}
