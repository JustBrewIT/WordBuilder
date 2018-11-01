import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.net.*;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.Iterator;

public class WordBuilder {
	static int permCount = 0;
	static String key = "121a8ef5-2ef2-44c8-bc3e-dbca7b25b860";
	
	public static void main(String [] args) throws IOException {
		//final String PROMPT_STRING = "Enter a string to permute in reverse (<Enter> to exit): ";
		//Scanner scnr = new Scanner(System.in);
		//String input;
		
		// Get input and permute the string
		//System.out.println(PROMPT_STRING);
		//input = scnr.nextLine();
		//while (input.length() > 0) {
		//permCount = 0;
		//permuteString("", input);
		//System.out.println(PROMPT_STRING);
		//input = scnr.nextLine();
	//} 
	//System.out.println("Done.");
		ArrayList<String> words = new ArrayList<String>();
		String input = "abcd";
		permCount = 0;
		words = permute(words, "", input);
		System.out.println(words);		
	} 
	
	public static ArrayList<String> permute(ArrayList<String> words, String head, String tail) throws IOException {
		char current;
		String newTail;
		String newString;
		int len;
		int i;
		current = '*';
		len = tail.length();
		if (len <= 1) {
			++permCount;
			//System.out.println(permCount + ") " + head + tail);
			newString = head + tail;
			words.add(newString);
		}
		else {
			for (i = len - 1; i >= 0; --i) {
				current = tail.charAt(i);
				newTail = tail.substring(0, i) + tail.substring(i + 1);
				//System.out.print("c: " + current + " | ");
				//System.out.print(tail.substring(0, i) + "+ " + tail.substring(i + 1) + " | ");
	            permute(words, head + current, newTail);
			} 
		}
		words.set(0, "cat");
		for(String word: words) {
			if (!wordCheck(word)) {
				words.remove(word);
			}
		}
		return words;
	} 
	
	
	// https://www.srijan.net/blog/how-parse-json-data-rest-api-using-simple-json-library
	// https://medium.com/programmers-blockchain/importing-gson-into-eclipse-ec8cf678ad52
	// http://www.jsonschema2pojo.org/
	public static boolean wordCheck(String word) throws IOException {

		String jsonString = "";
		URL url = new URL(String.format("https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s", word, key));
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.connect();
		int responseCode = con.getResponseCode();
		if(responseCode != 200) {
			throw new RuntimeException(String.format("HttpResponseCode:", responseCode));
		}
		else {
			Scanner scnr = new Scanner(url.openStream());
			while(scnr.hasNext()) {
				jsonString += scnr.nextLine();
			}
		}
		//System.out.print(jsonData);
		con.disconnect();
		return jsonResp(jsonString);	
	}


	public static boolean jsonResp(String jsonString) throws IOException {
		
		JSONParser parse = new JSONParser();
//		JSONObject jsonResp = (JSONObject)parse.parse(jsonString);
//		for(Iterator iterator = jsonResp.keySet().iterator(); iterator.hasNext();) {
//		    String key = (String) iterator.next();
//		    System.out.println(jsonResp.get(key));
//		}
		try {
			JSONArray jsonData = (JSONArray) parse.parse(jsonString);
			//System.out.print(jsonData);
			JSONObject meta = (JSONObject) jsonData.get(0);
			return true;
		}
		catch (Exception e){
			return false;
		}

	}
}
