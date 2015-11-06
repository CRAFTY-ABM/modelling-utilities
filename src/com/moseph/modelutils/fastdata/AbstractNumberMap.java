package com.moseph.modelutils.fastdata;

//import static repast.simphony.random.RandomHelper.*;

import static java.lang.Math.abs;

import java.text.DecimalFormat;
import java.util.Collection;
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
public abstract class AbstractNumberMap<T extends Indexed> implements NumberMap<T>
{
	IndexSet<T> indexes;
	boolean dirtyTotal;
	boolean dirtyAverage;
	boolean dirtyMaxMin;
	double average;
	T max;
	T min;
	int maxIndex = 0;

	public AbstractNumberMap( Collection<? extends T> indexes )
	{
		this( new ArrayIndexSet<T>( indexes ) );
	}
	
	public AbstractNumberMap( IndexSet<T> indexes )
	{
		this.indexes = indexes;
		maxIndex = indexes.getMaxIndex();
	}
	
	public abstract double getDouble( T key );
	public abstract void putDouble( T key, double value );
	public abstract void addDouble( T key, double value );
	public abstract void clear();
	abstract void updateTotals();
	abstract void updateAverage();
	
	
	public Iterable<? extends T> getKeySet()
	{
		return indexes;
	}
	
	public int size()
	{
		return maxIndex+1;
	}
	
	public double getAverage()
	{
		if( dirtyAverage ) updateAverage();
		return average;
	}
	
	public T getMax()
	{
		if( dirtyMaxMin ) updateMaxMin();
		return max;
	}
	
	public T getMin()
	{
		if( dirtyMaxMin ) updateMaxMin();
		return min;
	}
	
	void updateMaxMin()
	{
		double vMax = -Double.MAX_VALUE;
		double vMin = Double.MAX_VALUE;
		for( T k : indexes )
		{
			double val = getDouble( k );
			if( val > vMax )
			{
				vMax = val;
				max = k;
			}
			if( val < vMin )
			{
				vMin = val;
				min = k;
			}
		}
	}
	
	public Iterable<? extends T> getKeys()
	{
		return indexes;
	}
	
	void dirty()
	{
		dirtyTotal = true;
		dirtyAverage = true;
		dirtyMaxMin = true;
	}
	
	public void copyInto( NumberMap<T> target )
	{
		for( T k : indexes ) target.putDouble(k, getDouble( k ) );
	}
	
	public void copyFrom( UnmodifiableNumberMap<T> source )
	{
		source.copyInto( this );
	}
	
	public void copyFrom( Map<T, ? extends Number> source )
	{
		for( T k : indexes ) putDouble(k, source.get( k ).doubleValue() );
	}
	
	public void addInto( NumberMap<T> target )
	{
		for( T k : indexes ) target.addDouble(k, getDouble(k) );
	}
	
	public void multiplyInto( double v, NumberMap<T> target )
	{
		for( T k : indexes ) target.putDouble(k, v * getDouble(k) );
	}
	
	public double dotProduct( UnmodifiableNumberMap<T> other )
	{
		double value = 0;
		for( T key : indexes )
			value += getDouble( key ) * other.getDouble( key );
		return value;
	}
	
	/**
	 * Subtracts subtractor from this, and puts the result into target
	 * @param subtractor
	 * @param target
	 */
	public void subtractInto( UnmodifiableNumberMap<T> subtractor, NumberMap<T> target )
	{
		for( T key : indexes )
			target.putDouble( key, getDouble( key ) - subtractor.getDouble( key ));
	}
	
	public T sample()
	{
		return sample( false );
	}
	public T sample( boolean allowNull )
	{
		double max = allowNull ? 1 : getDoubleTotal();
		//double num = nextDoubleFromTo( 0, max );
		double num = 0;
		double cur = 0;
		for( T k : getKeys() )
		{
			cur += getDouble( k );
			if( cur >= num ) return k;
		}
		return null;
	}
	
	public  boolean same( UnmodifiableNumberMap<T> other ) { return same( other, DEFAULT_THRESHOLD ); }
	public  boolean same( UnmodifiableNumberMap<T> other, double thresh )
	{
		for( T t : getKeys() )
			if( abs( other.getDouble( t ) - getDouble( t ) ) > thresh ) return false;
		return true;
	}
	
	public String prettyPrint()
	{
		String r = "{ ";
		for( T t : getKeys() ) r += t + ":" + getDouble(t) + " ";
		return r + "}";
	}

	/**
	 * @param other
	 * @param format
	 * @return String representation of the dot product
	 */
	public String prettyPrintDotProduct(UnmodifiableNumberMap<T> other, DecimalFormat format) {
		double value = 0;
		StringBuffer buffer = new StringBuffer();
		for (T key : indexes) {
			buffer.append("\t\t" + key + "> " + format.format(getDouble(key)) + " * "
					+ format.format(other.getDouble(key)) + " = "
					+ format.format(getDouble(key) * other.getDouble(key)) + System.getProperty("line.separator"));
			value += getDouble(key) * other.getDouble(key);
		}
		buffer.append("\t\t" + "Product: " + format.format(value));
		return buffer.toString();
	}
}
