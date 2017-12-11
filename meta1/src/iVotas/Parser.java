package iVotas;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parser {
	
	public static LinkedHashMap<String, String> parseInput(String input){

		String[] aux;
		LinkedHashMap<String, String> hashmap = new LinkedHashMap<String, String>();
		aux = input.split(";");
		for (String field : aux) {
			try {
				String[] split = field.split("\\|"); // | representa a função OR por isso tem de ser \\|
				String firstString = split[0].trim();
				String secondString = split[1].trim();
				hashmap.put(firstString, secondString);
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Message doesnt follow the protocol");
				return null;
			}
		}
		return hashmap;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static String HashmapToStringProtocol(String name, HashMap<Integer,String> hashmap){
	    Iterator it = hashmap.entrySet().iterator();
	    int i = 0;
    	String str = "type | "+name+"_list; "+name+"_count: "+hashmap.size()+"; ";
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
			str += name+"_" + i + "_id | " + pair.getKey() + "; " + name+"_" + i  + "_name | " + pair.getValue() + "; ";
	        it.remove(); // avoids a ConcurrentModificationException
	        i++;
	    }
		return str;
	}
} 