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
package com.moseph.modelutils.serialisation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.filter.Filter;
import org.simpleframework.xml.stream.InputNode;

import com.csvreader.CsvReader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.moseph.gis.raster.Raster;
import com.moseph.gis.raster.RasterReader;
import com.moseph.modelutils.curve.LinearInterpolator;
import com.moseph.modelutils.fastdata.DoubleMap;
import com.moseph.modelutils.fastdata.DoubleMatrix;
import com.moseph.modelutils.fastdata.Indexed;
import com.moseph.modelutils.fastdata.Named;
import com.moseph.modelutils.fastdata.NamedIndexSet;

/**
 * Class to deal with persistence for the general models. Lets base directories
 * be set to make nested data structures easier
 * 
 * Also includes some utility functions to help dealing with CSV files, and reading them into
 * useful data structures. Also provides a layer of abstraction to the underlying
 * CSV library, which might be changed later.
 * 
 * There are lots of temporary file based methods; this is not used in the normal sense
 * of anonymous temporary files, but instead a file, normally in the ./tmp directory,
 * named after the class of object being (de)serialised. These are mostly used for testing,
 * and simply provide an easy way to store and reload an object.
 * 
 * Classloader stuff is defensive against Repast's classloader issues
 * @author dmrust
 *
 */
public class EasyPersister extends Persister
{
	ClassLoader classLoader; //The classloader to use
	String baseDir = null; //File loading is relative to this path
	String tmpDir = "./test-data/tmp/"; // Temp files for serialisation are
										// stored here
	Logger log = Logger.getLogger( getClass()  ); //Logger
	Map<String, String> context = new HashMap<String, String>();
	
	/*
	 * Constructors
	 */
	public EasyPersister()
	{
		classLoader = getClass().getClassLoader();
	}
	
	public EasyPersister(Filter filter) {
		super(filter);
		classLoader = getClass().getClassLoader();
	}

	public EasyPersister( ClassLoader classLoader )
	{
		this.classLoader = classLoader;
	}
	
	/*
	 * File path methods
	 */
	
	public String getBaseDir() { return baseDir; }
	public void setBaseDir( String baseDir ) { this.baseDir = baseDir; }
	public String getTmpDir() { return tmpDir; }
	public void setTmpDir( String tmpDir ) { this.tmpDir = tmpDir; }

	public String getFullPath( String path ) { return getFullPath( path, baseDir ); }
	public String getFullPath( String path, String baseDir ) { return getFullPath( path, baseDir, null ); }
	/**
	 * Contextualises both the path and the baseDir (if it exists), using the extra values passed if necessary
	 * @param path
	 * @param baseDir
	 * @param extra
	 * @return
	 */
	public String getFullPath( String path, String baseDir, Map<String,String> extra )
	{
		path = contextualise( path, extra );
		if( baseDir != null ) {
			return contextualise( baseDir, extra ) + "/" + path;
		}
		return path;
	}
	
	/**
	 * Makes sure that the given directory, inside the baseDir exists. Mostly useful
	 * for creating output directories
	 * @param directory
	 * @param baseDir
	 * @param isDirectory set to false if the filename is a file - then it'll make the containing directory
	 * @param extra any extra substitutions to be used
	 * @return
	 */
	public String ensureDirectoryExists( String directory, String baseDir, boolean isDirectory, Map<String,String> extra )
	{
		String opDirName = getFullPath( directory, baseDir, extra );
		File opDir = new File( opDirName );
		if( ! isDirectory ) {
			opDir = opDir.getParentFile();
		}
		try
		{
			if( ! opDir.exists() ) {
				if( !opDir.mkdirs() ) {
					log.fatal( "Couldn't make output directory - not sure why (" + opDir.getAbsolutePath() + ")" );
				}
			}
		} catch( Exception e ) { log.fatal( "Couldn't make output directory: " + e ); }
		return opDirName;
	}
	
	/*
	 * Basic XML methods
	 */

	/**
	 * Used to split tags apart when they are all in one field
	 * 
	 * Splits on zero or more spaces either side of a comma or semicolon
	 * @param field
	 * @return
	 */
	public static Set<String> splitTags( String field )
	{
		if( field == null ) {
			return new HashSet<String>();
		}
		return new HashSet<String>( Arrays.asList( field.split( "\\s*[,;]\\s*" ) ) );
	}

	/*
	 * XML Functions
	 */
	/**
	 * Reads an XML file relative to the persister's base directory.
	 * This is almost certainly the function you want for reading XML
	 * @param <T>
	 * @param type
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public <T> T readXML( Class<? extends T> type, String filename ) throws Exception
	{
		try{
			return read( type, getFullPath( filename ) );
		} catch( Exception e ) {
			log.error( "Couldn't read file '" + getFullPath( filename ) + "' for class " + type.getSimpleName(), e );
		}
		return null;
	}
	
	/**
	 * Writes the object as XML, with the given filename, relative to the persister's base directory
	 * @param object
	 * @param filename
	 * @throws Exception
	 */
	public void writeXML( Object object, String filename) throws Exception
	{
		write( object, new File( getFullPath( filename ) ));
	}
	
	/*
	 * Raster Reading/Writing
	 * 
	 *
	 */
	public Raster readRaster( String filename ) throws Exception
	{
		RasterReader reader = new RasterReader();
		return reader.readRaster( getFullPath( filename ) );
	}
	
	/**
	 * Checks that a csv file exists, and has (at least) the required set of fields
	 * @param caller
	 * @param filename
	 * @param requiredFields
	 * @return
	 */
	
	/*
	 * CSV file testing
	 */
	
	public boolean csvFileOK( Class<?> caller, String filename, String...requiredFields )
	{
		return csvFileOK( caller.getSimpleName(), filename, true, requiredFields );
	}
	
	/**
	 * Conveneience method - assumes that we want to check required fields
	 * @param caller
	 * @param filename
	 * @param requiredFields
	 * @return
	 */
	public boolean csvFileOK( String caller, String filename, String...requiredFields )
	{
		return csvFileOK( caller, filename, true, requiredFields );
	}
	
	/**
	 * Checks to make sure:
	 * - the give filename is non-null, and points to a valid file
	 * - the file can be opened
	 * - the file contains the required headers
	 * 
	 * 
	 * @param caller calling class/function, for output purposes
	 * @param filename the relative filename
	 * @param checkRequiredNotNull, then all requiredFields will be checked for non-null and length > 0
	 * @param requiredFields all the fields which should be in the file
	 * @return
	 */
	public boolean csvFileOK( String caller, String filename, boolean checkRequiredNotNull, String...requiredFields )
	{
		return csvFileOK( caller, filename, checkRequiredNotNull, Arrays.asList( requiredFields ) );
	}
	
	/**
	 * Checks to make sure:
	 * - the give filename is non-null, and points to a valid file
	 * - the file can be opened
	 * - the file contains the required headers
	 * 
	 * 
	 * @param caller calling class/function, for output purposes
	 * @param filename the relative filename
	 * @param checkRequiredNotNull, then all requiredFields will be checked for non-null and length > 0
	 * @param requiredFields all the fields which should be in the file
	 * @return
	 */
	public boolean csvFileOK( String caller, String filename, boolean checkRequiredNotNull, Collection<String> requiredFields )
	{
		if( filename == null ) {
			return false;
		}
		if( filename.length() == 0 ) {
			return false;
		}
		File f = new File( getFullPath( filename ));
		String abs = f.getAbsolutePath();
		List<String> req = new ArrayList<String>( requiredFields );
		if( ! checkRequiredNotNull )
		{
			for( String s : requiredFields ) {
				if( s == null || s.length() == 0 ) {
					log.fatal( "Missing required field for " + caller + " opening " + abs + ": " + req );
				}
			}
		}
		if( ! f.exists() )
		{
			log.fatal( "Tried to open nonexistent file for " + caller + ": " + abs );
		}
		try
		{
			CsvReader r = getCSVReader( filename );
			Set<String> headers = new HashSet<String>( Arrays.asList( r.getHeaders() ));
			Set<String> required = new HashSet<String>( req );
			required.removeAll( headers );
			if( required.size() > 0 )
			{
				log.fatal(  String.format( "Missing headers for %s in %s. Missing: %s (required: %s, got: %s",
						caller, abs, required, req, headers ) );
				return false;
			}
			r.close();
		} catch (IOException e)
		{
			log.fatal(  "Couldn't read CSV file for " + caller, e );
		}
		return true;
		
	}
	
	/*
	 * Basic CSV reading functionality
	 */

	/**
	 * Returns a CSV reader based on the path given and the base path
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public CsvReader getCSVReader( String relativePath ) throws IOException
	{
		char delimiter = ',';
		if( relativePath.toLowerCase().matches( ".*\\.tsv")) {
			delimiter = '\t';
		}
		return getCSVReader( relativePath, delimiter );
	}
	
	/**
	 * Return a CSV reader with the given delimiter
	 * @param relativePath
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public CsvReader getCSVReader( String relativePath, char delimiter ) throws IOException
	{
		CsvReader reader = new CsvReader( getFullPath( relativePath ), delimiter );
		reader.readHeaders();
		return reader;
	}

	/*
	 * CSV Reading and conversion functionality
	 */
	
	
	/**
	 * Expects a csv file, which has the names of things in one column, and another column
	 * which holds numeric data which is of interest. For example:
	 * 
	 * Name, Price, Size
	 * Wheat, 200, 1000
	 * Beets, 600, 20
	 * 
	 * This could be turned into a map from name to size, or from name to price, depending on the
	 * arguments passed in to csvToMap
	 * @param csvFile the filename
	 * @param nameColumn the column which holds the name of the object
	 * @param dataColumn the column which holds the interesting data about the object
	 * @return
	 * @throws IOException
	 */
	public Map<String, Double> csvToNumericMap( String csvFile, String nameColumn, String dataColumn ) throws IOException
	{
		// TODO check if LinkedHashMap required
		Map<String, Double> map = new LinkedHashMap<String, Double>();
		CsvReader reader = getCSVReader( csvFile );
		Set<String> headers = new HashSet<String>( Arrays.asList( reader.getHeaders() ) );
		if( ! headers.contains( dataColumn )) {
			log.fatal( "Looking for data column " + dataColumn + " in " + csvFile + " but it doesn't exist" );
		}
		if( ! headers.contains( nameColumn )) {
			log.fatal( "Looking for name column " + nameColumn + " in " + csvFile + " but it doesn't exist" );
		}
		while( reader.readRecord() )
		{
			if( reader.get( dataColumn ) != null && reader.get( dataColumn ).length() > 0 )
			{
				map.put( reader.get( nameColumn ), Double.parseDouble( reader.get( dataColumn ) ) );
			} else {
				log.debug( "No value for " + reader.get( nameColumn ));
			}
		}
		reader.close();
		return map;
	}
	
	/**
	 * Expects a csv file, which has the names of things in one column, and another column
	 * which holds mappings from them
	 * 
	 * External Name, Internal Name
	 * Orge d'Hiver, Winter Wheat
	 * Beets, Sugar Beet
	 * 
	 * This could be turned into a map from name to size, or from name to price, depending on the
	 * arguments passed in to csvToMap
	 * @param csvFile the filename
	 * @param keys the column which holds the keys
	 * @param values the column which holds their mappings
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> csvToStringMap( String csvFile, String keys, String values ) throws IOException
	{
		// TODO check if LinkedHashMap required
		Map<String, String> map = new LinkedHashMap<String, String>();
		CsvReader reader = getCSVReader( csvFile );
		while( reader.readRecord() )
		{
			if( reader.get( keys ) != null && reader.get( keys ).length() > 0 ) {
				map.put( reader.get( keys ), reader.get( values )+"" );
			}
		}
		return map;
	}
	
	/**
	 * Takes a csv file, which has a column containing Integers (e.g. years) and one
	 * containing strings, separated with either semicolons or commas (see splitTags(String))
	 * @param csvFile
	 * @param yearColumn
	 * @param tagColumn
	 * @return
	 * @throws IOException
	 */
	public Multimap<Integer, String> csvToIntegerMultiString( String csvFile, String yearColumn, String tagColumn ) throws IOException
	{
		Map<String, String> settings = csvToStringMap( csvFile, yearColumn, tagColumn );
		Multimap<Integer, String> result = HashMultimap.create();
		for( Entry<String, String> e : settings.entrySet() )
		{
			int year = Integer.parseInt( e.getKey() );
			Set<String> tags = splitTags( e.getValue() );
			result.putAll( year, tags );
		}
		return result;
	}
	
	
	/**
	 * Returns a map of Map<String,Double> where the first mapping is the columns (except the name colum)
	 * and the second map contains mappings from entries in the name column to the relevant numbers.
	 * So:
	 * Name, Price, Size
	 * Wheat, 200, 1000
	 * Beets, 600, 20
	 * 
	 * Would give:
	 * {Price = {Wheat = 200, Beets = 600}
	 * Size  =  {Wheat = 1000,Beets = 20} 
	 * }
	 *          
	 *         
	 * @param csvFile
	 * @param nameColumn
	 * @return
	 * @throws IOException
	 */
	public Table<String,String,Double> csvToDoubleTable( String csvFile, String nameColumn ) throws IOException
	{
		return csvToDoubleTable( csvFile, nameColumn, ',', null, null );
	}
	/**
	 * As before, but setting delimiter character, an optional set of strings which are the
	 * only columns to get, and an optional set of columns to ignore
	 * @param csvFile
	 * @param nameColumn
	 * @param delimiter
	 * @param columnsToGet (may be null)
	 * @param ignoreColumns (may be null)
	 * @return
	 * @throws IOException
	 */
	public Table<String,String,Double> csvToDoubleTable( String csvFile, String nameColumn, char delimiter, Set<String> columnsToGet, Set<String> ignoreColumns ) throws IOException
	{
		//Map<String,Map<String, Double>> map = new HashMap<String, Map<String,Double>>();
		Table<String,String,Double> map = HashBasedTable.create();
		CsvReader reader = getCSVReader( csvFile, delimiter );
		Set<String> cols = columnsToGet;
		if( cols == null ) {
			cols = new HashSet<String>( Arrays.asList( reader.getHeaders() ) );
		}
		cols.remove( nameColumn );
		if( ignoreColumns != null ) {
			cols.removeAll( ignoreColumns );
		}
		
		while( reader.readRecord() ) {
			for( String s : cols )
			{
				String val = reader.get(s);
				if( val != null && val.length() > 0 ) {
					map.put( reader.get( nameColumn ), s, Double.parseDouble( val ) );
				}
			}
		}
		return map;
	}
	
	
	
	/**
	 * Returns a mapping from names to breakpoint interpolators. The xCol
	 * gives a set of x positions, then every other column gives y positions
	 * for a curve (named after the column)
	 * 
	 * e.g.
	 * Time	Height Weight
	 * 0	5		9
	 * 1	10		12
	 * 4	3		6
	 * 
	 * Gives: Map{
	 * Height => curve( 0=5,1=10,4=3)
	 * Weight => curve(0=9,1=12,4=6)
	 * }
	 * @param csvFile
	 * @param xCol the name of the x column (Time in the above example). Defaults to first column if not present
	 * @param columns the names of columns to get. Defaults to all except xCol
	 * @return
	 * @throws IOException
	 */
	public Map<String, LinearInterpolator> csvVerticalToCurves( String csvFile, String xCol, Collection<String> columns ) throws IOException
	{
		// TODO check if LinkedHashMap required
		Map<String, LinearInterpolator> map = new LinkedHashMap<String, LinearInterpolator>();
		CsvReader reader = getCSVReader( csvFile );
		
		if( xCol == null ) {
			xCol = reader.getHeaders()[0];
		}
		
		if( columns == null || columns.size() == 0) {
			columns = new ArrayList<String>( Arrays.asList( reader.getHeaders() ));
		}
		columns.remove( xCol );
		
		for( String s : columns ) {
			map.put( s, new LinearInterpolator() );
		}
		
		while( reader.readRecord() && reader.get( xCol ).length() > 0 )
		{
			double year = Double.parseDouble( reader.get( xCol ));
			for( String s : columns ) {
				map.get( s ).addPoint( year, Double.parseDouble( reader.get( s ) ) );
			}
		}
		return map;
	}
	
	/**
	 * Wrapper for csvVerticalToCurves, uses first column as x col
	 * @param csvFile
	 * @param requiredHeaders
	 * @return
	 * @throws IOException
	 */
	public Map<String, LinearInterpolator> csvVerticalToCurves( String csvFile, String ... columns ) throws IOException
	{
		return csvVerticalToCurves( csvFile, null, Arrays.asList( columns ) );
	}
	
	/**
	 * Converts a CSV file into a set of curves describing the properties of some objects over time
	 * 
	 * For example:
	 * 
	 * Name,	2000,	2010,	2020,	2030,	2040
	 * Wheat,	100,	120,	140,	160,	180
	 * Barley,	110,	160,	100,	100,	100
	 * 
	 * Returns Map{
	 * Wheat => curve(2000=100, 2010=120, ... 2040=180)
	 * Barley => curve(2000=110, 2010=160, ... 2040=100) }
	 * 
	 * It returns a map of Curves, but internally uses BreakpointInterpolators, and sets
	 * a breakpoint at every datapoint given in the file.
	 * 
	 * @param csvFile
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public Map<String, LinearInterpolator> csvHorizontalToCurve( String csvFile ) throws NumberFormatException, IOException
	{
		// TODO check if LinkedHashMap required
		Map<String, LinearInterpolator> map = new LinkedHashMap<String, LinearInterpolator>();
		CsvReader reader = getCSVReader( csvFile );
		int[] columns = new int[reader.getHeaderCount()-1];
		for( int i = 1; i < reader.getHeaderCount(); i++ )
		{
			columns[i-1] = Integer.parseInt( reader.getHeaders()[i] );
		}
		while( reader.readRecord() )
		{
			String name = reader.get( 0 );
			LinearInterpolator bi = new LinearInterpolator();
			for( int i = 1; i < reader.getHeaderCount(); i++ )
			{
				double val = 0;
				String textVal =  reader.get( i );	
				try
				{
					val = Double.parseDouble( textVal );
					bi.addPoint( columns[i-1], val );
				} catch( Exception e )
				{
					log.error( "Bad string for value " + columns[i-1] + " line " + reader.getCurrentRecord() + " file " + csvFile + " was: '" + textVal + "'");
					log.error( e.getMessage() );
				}
			}
			map.put( name, bi );
		}
		return map;
	}
	
	/**
	 * Creates a double map from the given CSV file. Assumes that the first column is the names
	 * of each row. (Could be changed in the future if necessary)
	 * @param csvFile
	 * @param columns
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public <T extends Indexed & Named,S extends Indexed & Named> DoubleMatrix<T,S> csvToMatrix( String csvFile, NamedIndexSet<T> columns, NamedIndexSet<S> rows ) throws IOException
	{
		//Get the rows and columns we're working with
		Set<String> colNames = new HashSet<String>();
		for( T c : columns ) {
			colNames.add(c.getName());
		}
		Set<String> rowNames = new HashSet<String>();
		for( S r : rows ) {
			rowNames.add(r.getName());
		}
		
		//Check the file is OK and get a reader
		if( ! csvFileOK( "", csvFile, true, colNames )) {
			throw new RuntimeException("Bad CSV File");
		}
		CsvReader reader = getCSVReader( csvFile );
		
		//Make the target map
		DoubleMatrix<T, S> ret = new DoubleMatrix<T, S>( columns, rows );
		while( reader.readRecord() )
		{
			String row = reader.get( 0 );
			if( ! rowNames.contains( row )) {
				log.warn("Unknown row in " +csvFile + " at line " + reader.getCurrentRecord());
			}
			S r = rows.forName( row );
			rowNames.remove( row );
			for( T col : columns )
			{
				String s = reader.get( col.getName() );
				if( s != null && s.length() > 0 ) {
					ret.put( col, r, Double.parseDouble( s ) );
				}
			}
		}
		if( rowNames.size() > 0 ) {
			log.warn("Didn't find all rows in " + csvFile + ". Missing: " + rowNames );
		}
		return ret;
	}
	
	public <T extends Indexed & Named> DoubleMap<T> csvToDoubleMap( String csvFile, NamedIndexSet<T> rows, String column ) throws IOException
	{
		if( ! csvFileOK( "", csvFile, true, column )) {
			return null;
		}
		
		Set<String> rowNames = new HashSet<String>();
		for( T r : rows ) {
			rowNames.add(r.getName());
		}
		
		CsvReader reader = getCSVReader( csvFile );
		DoubleMap<T> map = new DoubleMap<T>( rows );
		while( reader.readRecord() )
		{
			String row = reader.get( 0 );
			if( ! rowNames.contains( row )) {
				log.warn("Unknown row in " +csvFile + " at line " + reader.getCurrentRecord());
			}
			T r = rows.forName( row );
			rowNames.remove( row );
			String s = reader.get( column );
			if( s != null && s.length() > 0 ) {
				map.put( r, Double.parseDouble( s ) );
			}
		}
		if( rowNames.size() > 0 ) {
			log.warn("Didn't find all rows in " + csvFile + ". Missing: " + rowNames );
		}
		return map;
	}
	
	/*
	 * Functionality improvements
	 */

	/**
	 * A wrapper around SimpleXML's read method, which uses our classloader
	 * instead of the default thread one. (This helps when Repast swaps classloaders)
	 * 
	 */
	@Override
	public <T> T read( Class<? extends T> type, InputNode source, boolean strict ) throws Exception
	{
		ClassLoader defaultLoader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader( classLoader );
			return super.read( type, source, strict );
			
		} finally
		{
			Thread.currentThread().setContextClassLoader( defaultLoader );
		}
	}

	/**
	 * Wrapper round a broken version from SimpleXML - probably not necessary any more
	 */
	@Override
	public <T> T read( Class<? extends T> type, String filename ) throws Exception
	{
		try
		{
			return read( type, new File( filename ));
		} catch (IOException e )
		{
			log.fatal( "Couldn't find file: " + new File( filename ).getAbsolutePath() );
			throw e;
		}
	}

	
	/*
	 * Testing utilities
	 */
	
	
	/**
	 * Constructs a temporary filename from the given object, and tries
	 * to read the file if it is there.
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public <T> T getTempVersion( Class<? extends T> type ) throws Exception
	{
		return read( type, new File( getTempFilename( type ) ) );
	}

	/**
	 * Writes the object out to a temporary file, then reads that
	 * file back in and returns it. 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public <T> T roundTripSerialise( T object ) throws Exception
	{
		return roundTripSerialise( object, getTempFilename( object ) );
	}

	/**
	 * Writes the object out to the given filename (creating non-existing dirs),
	 * then reads the file back and returns it
	 * 
	 * @param object
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T roundTripSerialise( T object, String filename) throws Exception
	{
		File f = new File( filename );
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		write( object, f );
		T t = read( (Class<? extends T>)object.getClass(), f );
	
		return t;
	}

	/**
	 * Creates a temporary filename for the given class
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getTempFilename( Class c )
	{
		return tmpDir + c.getSimpleName() + ".xml";
	}

	/**
	 * Creates a temporary filename for the given object
	 * @param c
	 * @return
	 */
	public String getTempFilename( Object c )
	{
		return getTempFilename( c.getClass() );
	}
	
	/**
	 * The context of the persister maps strings to other strings, to allow for
	 * general specifications which can be contextualised for certain situations.
	 * 
	 * For example, to allow file names which have a placeholder for the current run
	 * or scenario.
	 * 
	 * For each key k in the context, occurrences of %k in the string are replaced
	 * with the corresponding value.
	 * @param c
	 * @return
	 */
	public String contextualise( String c )
	{
		return contextualise( c, null );
	}
	
	public String contextualise( String c, Map<String,String> extra )
	{
		for( String k : context.keySet() ) {
			c = c.replaceAll( "%"+k, context.get( k ) );
		}
		if( extra != null ) {
			for( String k : extra.keySet() ) {
				c = c.replaceAll( "%"+k, extra.get( k ) );
			}
		}
		return c;
	}
	
	public void setContext( String key, String value )
	{
		context.put( key, value );
	}
}
