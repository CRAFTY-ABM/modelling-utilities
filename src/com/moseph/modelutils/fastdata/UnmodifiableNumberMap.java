package com.moseph.modelutils.fastdata;

import java.util.Map;

public interface UnmodifiableNumberMap<T extends Indexed>
{
	public double getDouble( T key );
	public Iterable<? extends T> getKeySet();
	public int size();
	public double getAverage();
	public double getDoubleTotal();
	public T getMax();
	public T getMin();
	public Iterable<? extends T> getKeys();
	public void copyInto( NumberMap<T> target );
	public void addInto( NumberMap<T> target );
	public Map<T, ? extends Number> toMap();
	public double dotProduct( UnmodifiableNumberMap<T> other );
	public  boolean same( UnmodifiableNumberMap<T> other );
	public  boolean same( UnmodifiableNumberMap<T> other, double threshold );
	public String prettyPrint();
	
	public double DEFAULT_THRESHOLD = 0.0000001;
}
