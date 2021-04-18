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
package com.moseph.modelutils;

import static java.lang.Math.floor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

//import com.sun.org.slf4j.internal.Logger;

import de.cesr.uranus.core.UranusRandomService;

public class Utilities {
	/*
	 * Slightly ugly chunk of code, to make it easy to use Repast's repeatable
	 * RNGs if they are present and the system RNG otherwise
	 */
	static RandHelp randHelp = new SystemRandomHelper();

	public static double nextDoubleFromTo(double start, double end,
			UranusRandomService rService, String generatorName) {
		return start + (end - start) * rService.nextRaw(generatorName);
	}

	public static double nextDouble(UranusRandomService rService,
			String generatorName) {
		return rService.nextDouble(generatorName);
	}

	public static int nextIntFromTo(int start, int end,
			UranusRandomService rService, String generatorName) {
		return (int) (start + (long) ((1L + end - start) * rService
				.nextRaw(generatorName)));
	}

	{
		try {
			randHelp = new RepastRandomHelper();
			System.out.println("Using Repast RNG");
		} catch (Exception e) {
			System.out.println("No Repast RNG found!");
		}
	}

	public static void setRandomHelper(RandHelp r) {
		randHelp = r;
	}

	public static void setSeed(long seed) {
		randHelp.setSeed(seed);
	}

	public static <T> T sample(Map<T, Double> probabilities,
			UranusRandomService rService, String generatorName) {
		return sample(probabilities, false, rService, generatorName);
	}

	/**
	 * Samples from the map of probabilities (i.e. T -> prob of T) (roulette
	 * wheel)
	 * 
	 * If forceNormalised is true, then the sum of probabiliies is calculated,
	 * so it works without normalised values. This is slower (has to sum
	 * values), but safer if you *need* a return value.
	 * 
	 * Alternatively, without forceNormalised, you can supply a set of
	 * probabilities that add up to less than 1, and have the chance of getting
	 * null, e.g. if it is allowed for an event not to happen
	 * 
	 * For example, if you have probabilities of giving birth to different types
	 * of agents, but also not giving birth, then just put in absolute
	 * probabilities for the different birth types: sample( { boy:0.1, girl:0.1
	 * }, false ) would give a 20% birth rate, evenly split between boys and
	 * girls
	 * 
	 * 
	 * @param <T>
	 * @param probabilities
	 * @param forceNormalised
	 * @return
	 */
	public static <T> T sample(Map<T, Double> probabilities,
			boolean forceNormalised, UranusRandomService rService,
			String generatorName) {
		double current = 0;
		double maxProb = 1;
		if (forceNormalised) {
			maxProb = 0;
			for (double d : probabilities.values()) {
				maxProb += d;
			}
			if (maxProb == 0) {
				// uniformly distributed:
				return sample(probabilities.keySet(), rService, generatorName);
			}
		}
		// normalise:
		double random = rService.nextDouble(generatorName)
				* maxProb;
		for (Entry<T, Double> e : probabilities.entrySet()) {
			current += e.getValue();
			if (current > random) {
				return e.getKey();
			}
		}
		return null;
	}

	/**
	 * Gives the number of events from a given population and probability. The
	 * idea is that sometimes we'd like to do this non-stochastically, to make
	 * testing easier. In that case, it's (int)(population*prob). Otherwise, it
	 * samples the population at "prob".
	 * 
	 * Should be grown up and use a distribution!
	 * 
	 * @param population
	 * @param rate
	 * @param stochastic
	 * @return
	 */
	public static int applyProbability(int population, double rate,
			boolean stochastic, UranusRandomService rService,
			String generatorName) {
		if (stochastic) {
			// TODO: appalling lazy, Bernoulli would kill me but it's Saturday
			// morning and
			// I'm too lazy to do maths.
			int num = 0;
			for (int i = 0; i < population; i++) {
				if (rService.nextDouble(generatorName) < rate) {
					num++;
				}
			}
			return num;
		} else {
			return (int) (rate * population);
		}
	}

	/**
	 * Used when we need to have discrete events happen at an average rate
	 * 
	 * Not very sophisticated - ought to include variance, but this will at
	 * least give approximate rates
	 * 
	 * @param rate
	 * @return
	 */
	public static int sampleRate(double rate, UranusRandomService rService,
			String generatorName) {
		int value = (int) floor(rate);
		if (rService.nextDouble(generatorName) < rate - value) {
			value++;
		}
		return value;
	}

	/**
	 * Given a floating point rate at which events should occur and a population
	 * returns a number of events. By converting the target rate into a
	 * probability of occurrence based on the population size.
	 * 
	 * @param population
	 * @param targetRate
	 * @return
	 */
	public static int sampleNumEvents(int population, double targetRate,
			UranusRandomService rService, String generatorName) {
		double rate = targetRate / population;
		return applyProbability(population, rate, true, rService, generatorName);
	}

	public static <T> T consume(Map<T, Integer> population,
			UranusRandomService rService, String generatorName) {
		Iterator<Entry<T, Integer>> it = population.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getValue() <= 0) {
				it.remove();
			}
		}
		T value = sample(population.keySet(), rService, generatorName);
		if (value == null) {
			return null;
		}
		int pop = population.get(value);
		pop--;
		population.put(value, pop);
		if (pop <= 0) {
			population.remove(value);
		}
		return value;
	}

	public static <T> T consume(List<T> population,
			UranusRandomService rService, String generatorName) {
		int index = (int) ((1L + (population.size() - 1)) * rService
				.nextRaw(generatorName));
		T val = population.get(index);
		population.remove(index);
		return val;
	}

	/**
	 * Returns a random sample from the set
	 * 
	 * @param <T>
	 * @param objects
	 * @return
	 */
	public static <T> T sample(Collection<T> objects,
			UranusRandomService rService, String generatorName) {
		int num = nextIntFromTo(0, objects.size() - 1, rService, generatorName);
		if (objects instanceof List<?>) {
			return ((List<T>) objects).get(num);
		}
		int i = 0;
		for (T t : objects) {
			i++;
			if (i > num) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Takes the input, which is assumed to be between min and max, and scales
	 * it to be between 0 and 1, with min being 0 and max being 1. If min is
	 * larger than max, that still works, e.g. scale( 1, 1, 0 ) = 0
	 * 
	 * @param input
	 * @param worst
	 * @param best
	 * @return
	 */
	public static double scale(double input, double min, double max) {
		double val = (input - min) / (max - min);
		if (val > 1) {
			return 1;
		}
		if (val < 0) {
			return 0;
		}
		return val;
	}

	public static <T> T getMaximum(Map<T, ? extends Number> map) {
		return getMinimum(map, true);
	}

	public static <T> T getMinimum(Map<T, ? extends Number> map) {
		return getMinimum(map, false);
	}

	/**
	 * Returns the key in the map with the minimum value. If getMax is true,
	 * returns the max value instead
	 * 
	 * @param <T>
	 * @param map
	 * @param getMax
	 * @return
	 */
	public static <T> T getMinimum(Map<T, ? extends Number> map, boolean getMax) {
		if (map.isEmpty()) {
			return null;
		}
		double curMin = getMax ? -Double.MAX_VALUE : Double.MAX_VALUE;
		T curType = null;
		for (Entry<T, ? extends Number> t : map.entrySet()) {
			double num = t.getValue().doubleValue();
			// The XOR lets us use the same code for getMin and getMax
			if ((num < curMin) ^ getMax) {
				curMin = num;
				curType = t.getKey();
			}
		}
		return curType;
	}

	/**
	 * Returns a list of the keys of the map, sorted by their values, with the
	 * highest value first
	 * 
	 * @param <T>
	 * @param input
	 * @return
	 */
	public static <T> List<T> sortMap(final Map<T, ? extends Number> input) {
		Comparator<T> c = new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return -Double.compare(input.get(o1).doubleValue(),
						input.get(o2).doubleValue());
			}
		};
		List<T> output = new ArrayList<T>(input.keySet());
		Collections.sort(output, c);
		return output;
	}

	public static <T> T getBest(Collection<T> data, Evaluator<T> eval) {
		T t = null;
		double val = -Double.MAX_VALUE;
		for (T v : data) {
			double cur = eval.evaluate(v);
			if (t == null || cur > val) {
				t = v;
				val = cur;
			}
		}
		return t;
	}

	/**
	 * Returns up to the specified number of samples from the input. Get N items
	 * from a list. Alternate formulation to sampleN( Collection, number ) as it
	 * avoids copying the input.
	 * 
	 * 
	 * Warning: when input.size() == toSample, it returns the input collection with the same order.
	 *   
	 * @param <T>
	 * @param input
	 * @param toSample
	 * @return sampled set
	 */
	public static <T> Set<T> sampleN(Collection<T> input, int toSample,
			UranusRandomService rService, String generatorName) {
		Set<T> ret = new LinkedHashSet<T>(toSample);

		int nLeft = input.size();

		if (toSample >= nLeft) {
			ret.addAll(input);
			return ret;
		}

		Iterator<T> it = input.iterator();
		int toSampleVary = toSample;
		while (toSampleVary > 0 && it.hasNext()) {
			T cur = it.next();
			// -1 as it's inclusive
			int rand = nextIntFromTo(0, nLeft - 1, rService, generatorName);
			if (rand < toSampleVary) {
				ret.add(cur);
				toSampleVary--;
			}
			nLeft--;
		}

		//		// avoid hasNext() and next() 
		//		// Reference: https://www.javamex.com/tutorials/random_numbers/random_sample.shtml
		//		int toSampleVary = Math.min(toSample, input.size());
		//		int i=0;
		//
		//		// Object[] arr = input.toArray();  // slower?
		//		List<T> ls = new ArrayList<T> (input); // faster?
		//		// 	List<T> ls = input.stream().collect(Collectors.toList());
		//
		//		while (toSampleVary > 0) {
		//			// -1 as it's inclusive
		//			int rand = nextIntFromTo(0, nLeft - 1, rService, generatorName);
		//			if (rand < toSampleVary) {
		//
		//				// ret.add((T) arr[i]); // array version 
		//				ret.add((T) ls.get(i)); // list version
		//				toSampleVary--;
		//
		//			}
		//			nLeft--;
		//			i++;
		//		}

		return ret;
	}

	/**
	 * A simple interface for something which gets a numeric value from a
	 * certain type of object
	 *	
	 * @author dmrust
	 * @param <T>
	 */
	public static interface Score<T> {
		public double getScore(T t);
	}

	/* 
	 * scoreMap() was being the bottle neck when had large number of cells. 
	 * Parallelisation using pararallelStream() (02.01.2021)  
	 * @author abs 
	 */
	public static <T> Map<T, Double> scoreMap(Collection<T> items,
			Score<T> score) {
		//	  	Map<T, Double> map = new LinkedHashMap<T, Double>();
		//				
		//	  	for (T t : items) { // for-loop version
		//	  		map.put(t, score.getScore(t));
		//	  	}

		//		Map<T, Double> map = new LinkedHashMap<T, Double>();
		//		items.forEach(t-> map.put(t, score.getScore(t))); // for each version 

		Map<T, Double> map = items.parallelStream().collect(Collectors.toMap(t->t,
				t -> (score.getScore(t)), 
				(e1, e2) -> e1, LinkedHashMap::new)); // parallelised stream

		// Reference: 
		// https://dkbalachandar.wordpress.com/2017/04/03/java-8-create-linkedhashmap-with-collectors-tomap/

		return map;
	}

	/**
	 * Creates a comparator based on calculating a score for each object and
	 * storing the results to avoid recomputing
	 * 
	 * @author dmrust
	 * 
	 * @param <T>
	 */
	public static class LazyScoreComparator<T> implements Comparator<T> {
		Map<T, Double> scores = new HashMap<T, Double>();
		Score<T> score;

		public LazyScoreComparator(Collection<T> items, Score<T> score) {
			this.score = score;
		}

		@Override
		public int compare(T arg0, T arg1) {
			if (!scores.containsKey(arg0)) {
				scores.put(arg0, score.getScore(arg0));
			}
			if (!scores.containsKey(arg1)) {
				scores.put(arg1, score.getScore(arg1));
			}
			return scores.get(arg0).compareTo(scores.get(arg1));
		}
	}

	public static class ScoreComparator<T> implements Comparator<T> {
		Map<T, Double> scores;

		public ScoreComparator(Collection<T> items, Score<T> score) {
			scores = scoreMap(items, score);
		}

		public ScoreComparator(Map<T, Double> scores) {
			this.scores = scores;
		}

		@Override
		public int compare(T arg0, T arg1) {
			return scores.get(arg0).compareTo(scores.get(arg1));
		}
	}

	public static <T> List<T> sort(Collection<T> items, Score<T> score) {
		List<T> list = (items instanceof List<?>) ? (List<T>) items
				: new ArrayList<T>(items);
		Collections.sort(list, new ScoreComparator<T>(items, score));
		return list;
	}

	public static <T> boolean containsAny(Collection<T> a, Collection<T> b) {
		return !Collections.disjoint(a, b);
	}

	public static <T extends Comparable<T>, S> S getFirst(Map<T, S> map) {
		ArrayList<T> keys = new ArrayList<T>(map.keySet());
		Collections.sort(keys);
		return map.get(keys.get(0));
	}

	public static <T> T pickRandomItem(Collection<T> collection,
			UranusRandomService rService, String generatorName) {
		int i = nextIntFromTo(0, collection.size() - 1, rService, generatorName);
		int index = 0;
		for (T t : collection) {
			index++;
			if (index >= i) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Simple Fisher-Yates shuffle that uses the Repast RNG for repeatability
	 * 
	 * @param list
	 */
	public static <T> void shuffle(List<T> list, UranusRandomService rService,
			String generatorName) {
		for (int i = list.size() - 1; i >= 1; i--) {
			int j = nextIntFromTo(0, i, rService, generatorName);

			T a = list.get(i);
			list.set(i, list.get(j));
			list.set(j, a);
		}
	}

	public static <P, T extends P> void incrementHash(Map<P, Double> map,
			T key, double amount) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + amount);
		} else {
			map.put(key, amount);
		}
	}

	public static <P, T extends P> double hashDouble(Map<P, Double> map, T key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return 0;

	}

	public static <P, T extends P> void incrementHash(Map<P, Integer> map, T key) {
		incrementHash(map, key, 1);
	}

	public static <P, T extends P> void incrementHash(Map<P, Integer> map,
			T key, int amount) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + amount);
		} else {
			map.put(key, amount);
		}
	}

	public static <P, T extends P> int hashInt(Map<P, Integer> map, T key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return 0;
	}

	public static <P> P argmax(Map<P, ? extends Number> map) {
		double max = -Double.MAX_VALUE;
		P val = null;
		for (P p : map.keySet()) {
			if (map.get(p).doubleValue() > max) {
				val = p;
				max = map.get(p).doubleValue();
			}
		}
		return val;
	}

	public static <P> P argmin(Map<P, ? extends Number> map) {
		double max = Double.MAX_VALUE;
		P val = null;
		for (P p : map.keySet()) {
			if (map.get(p).doubleValue() < max) {
				val = p;
				max = map.get(p).doubleValue();
			}
		}
		return val;
	}

	/**
	 * Applies the rate update function to the values in the hash
	 * 
	 * @param <P>
	 * @param <T>
	 * @param map
	 * @param key
	 * @param rate
	 * @param update
	 * @param ignoreZero
	 * @param ignoreNegative
	 */
	public static <P, T extends P> void rateUpdate(Map<P, Double> map, T key,
			double rate, double update, boolean ignoreZero,
			boolean ignoreNegative) {
		double current = hashDouble(map, key);
		map.put(key,
				rateUpdate(current, update, rate, ignoreZero, ignoreNegative));
	}

	/**
	 * Updates the given number with the update provided, and the rate
	 * specified. Rate of 0 = use existing value, rate of 1 = take the new
	 * number rate 0.5 is halfway between
	 * 
	 * @param current
	 * @param update
	 * @param rate
	 * @param ignoreZero
	 * @param ignoreNegative
	 * @return
	 */
	public static double rateUpdate(double current, double update, double rate,
			boolean ignoreZero, boolean ignoreNegative) {
		if (ignoreZero && current == 0) {
			return update;
		}
		if (ignoreNegative && current < 0) {
			return update;
		}
		return rate * update + (1 - rate) * current;
	}

	/**
	 * Updates the current value with the given one at the given rate[
	 * 
	 * @param current
	 * @param update
	 * @param rate
	 * @return
	 */
	public static double rateUpdate(double current, double update, double rate) {
		return rateUpdate(current, update, rate, false, false);
	}

	public static double average(Collection<Double> vals) {
		if (vals.size() == 0) {
			return 0;
		}
		double total = 0;
		for (double d : vals) {
			total += d;
		}
		return total / vals.size();
	}

	interface RandHelp {
		public double nextDoubleFromTo(double start, double end);

		public double nextDouble();

		public int nextIntFromTo(int start, int end);

		public void setSeed(long seed);
	}

	class RepastRandomHelper implements RandHelp {
		Method dblFrom = null;
		Method intFrom = null;
		Method nextDbl = null;
		Method setSeed = null;

		public RepastRandomHelper() throws ClassNotFoundException,
		SecurityException, NoSuchMethodException {
			Class<?> clazz = Class
					.forName("repast.simphony.random.RandomHelper");
			dblFrom = clazz.getDeclaredMethod("nextDoubleFromTo", Double.TYPE,
					Double.TYPE);
			intFrom = clazz.getDeclaredMethod("nextIntFromTo", Double.TYPE,
					Double.TYPE);
			nextDbl = clazz.getDeclaredMethod("nextDouble");
			setSeed = clazz.getDeclaredMethod("setSeed");
		}

		@Override
		public double nextDoubleFromTo(double start, double end) {
			try {
				return ((Double) dblFrom.invoke(null, start, end))
						.doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public int nextIntFromTo(int start, int end) {
			try {
				return ((Integer) intFrom.invoke(null, start, end)).intValue();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public double nextDouble() {
			try {
				return ((Double) nextDbl.invoke(null)).doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		/* Untested */
		@Override
		public void setSeed(long seed) {
			try {
				setSeed.invoke(seed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static class SystemRandomHelper implements RandHelp {
		Random rand = new Random();

		@Override
		public double nextDoubleFromTo(double start, double end) {
			return rand.nextDouble() * (end - start) + start;
		}

		@Override
		public double nextDouble() {
			return rand.nextDouble();
		}

		@Override
		public int nextIntFromTo(int start, int end) {
			return (int) floor(rand.nextDouble() * (end - start + 1)) + start;
		}

		@Override
		public void setSeed(long seed) {
			rand.setSeed(seed);
		}
	}
}
