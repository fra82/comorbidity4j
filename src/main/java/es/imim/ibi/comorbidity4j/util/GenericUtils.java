package es.imim.ibi.comorbidity4j.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import com.google.common.base.Strings;

/**
 * Generic utility
 * 
 * @author Francesco Ronzano
 *
 */
public class GenericUtils {
	
	public static String newLine = "<br/>";
	
	/**
	 * Print heap state
	 * 
	 * @return
	 */
	public static String printHeapState() {
		long mbyte = 1048576l;
		return "Heap size: " + ((double) Runtime.getRuntime().totalMemory() / mbyte) + " Mb" + 
		", Max heap size: " + ((double) Runtime.getRuntime().maxMemory() / mbyte) + " Mb" + 
		", Free heap size: " + ((double) Runtime.getRuntime().freeMemory() / mbyte) + " Mb";
	}


	/**
	 * If the returned String is not null an error occurred, described by the same string
	 * 
	 * @param utf8string
	 * @param filePath
	 * @return
	 */
	public static String storeUTF8stringToFile(String utf8string, String filePath) {
		if(utf8string != null) {
			Writer out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
				out.write(utf8string);
				return null;
			}
			catch(Exception e) {
				return "Impossible to write file > " + ((e.getMessage() != null) ? e.getMessage() : "-");
			}
			finally {
				try {
					out.close();
				} catch (IOException e) {
					return "Impossible to close the file writer > " + ((e.getMessage() != null) ? e.getMessage() : "-");
				}
			}
		}
		else {
			return "The input string is NULL.";
		}

	}

	/**
	 * Read file from resource folder Maven
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFile(String fileName) {

		StringBuilder result = new StringBuilder("");
		
		InputStream in = (new GenericUtils()).getClass().getResourceAsStream(fileName); 
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		try (Scanner scanner = new Scanner(reader)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();

	}
	
	/**
	 * Create directory from full path 
	 * 
	 * @param dirFullPath
	 * @return
	 */
	public static File createDir(String dirFullPath) {
		File webOutputDir = new File(dirFullPath);
		if (!webOutputDir.exists()) {
			System.out.println("creating directory: " + webOutputDir.getName());
			boolean result = false;
			try{
				webOutputDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				se.printStackTrace();
			}        
			if(result) {    
				System.out.println("DIR created: " + dirFullPath);  
			}
		}
		
		return webOutputDir;
	}
	
	/**
	 * Return the number of map entries that has a value equal to the matchValue (case insensitive)
	 * 
	 * @param matchValue
	 * @param diagnosisCodeStringPairedDiseasesMap
	 * @return
	 */
	public static int countMapValueMatching(String matchValue, Map<String, String> diagnosisCodeStringPairedDiseasesMap) {
		int retCount = 0;
		
		if(matchValue != null && diagnosisCodeStringPairedDiseasesMap != null && diagnosisCodeStringPairedDiseasesMap.size() > 0) {
			for(Entry<String, String> diagnosisCodeStringPairedDiseasesMapEntry : diagnosisCodeStringPairedDiseasesMap.entrySet()) {
				if(diagnosisCodeStringPairedDiseasesMapEntry != null && !Strings.isNullOrEmpty(diagnosisCodeStringPairedDiseasesMapEntry.getValue())) {
					String mappedDiseaseStr = diagnosisCodeStringPairedDiseasesMapEntry.getValue().trim().toLowerCase();
					
					if(mappedDiseaseStr.equals(matchValue.toLowerCase())) {
						retCount++;
					}
					
				}
			}
		}
		
		return retCount;
	}
	
	/**
	 * Sort map by value
	 * 
	 * @param map
	 * @param inverted
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, Boolean inverted) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (inverted == null || inverted == false) ? (o1.getValue()).compareTo( o2.getValue() ) : (o2.getValue()).compareTo( o1.getValue() );
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}


}
