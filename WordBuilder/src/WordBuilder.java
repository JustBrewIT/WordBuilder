import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class WordBuilder {
	static int permSubCount = 0;
	static String key = "121a8ef5-2ef2-44c8-bc3e-dbca7b25b860";
	
	public static void main(String [] args) {
		final String PROMPT_STRING = "Type in a string of up to (5) letters [a-z] followed by <Enter> to generate a list of possible words, or only hit <Enter> to exit: ";
		final String EXIT_STRING = "Exiting WordBuilder.";
		final String DIVIDER_STRING = "=======================================================================\n";
		Scanner scnr = new Scanner(System.in);
		String input;
		ArrayList<String> words = new ArrayList<String>();
		System.out.println(PROMPT_STRING);
		input = scnr.nextLine();
		
		while (input.length() > 0) {
			permSubCount = 0;
			words = permute(words, "", input);
			int retryCount = 0;
			String word = "";
			words.set(0, "cat");
			int wordsSize = words.size();
			
			for(int i = 0; i < wordsSize; ++i) {
				word = words.get(i);
				try {
					if (!wordCheck(word)) {
						words.remove(word);
						--i;
						--wordsSize;
					}
				} catch (IOException e) {
					if(retryCount < 3) {
						++retryCount;
						System.out.printf("Cannot connect to API. Retrying attempt #%d....\n", retryCount);;
						--i;
					}
					else {
						System.out.printf("Cannot complete request. Program was only able to test %d out of %d strings. \n", i, words.size());
						break;
					}
				}
			}
			
			System.out.printf("\nYour input \"%s\" contains %d valid word(s) out of %d possibilities.\n", input,  words.size(), permSubCount);
			for(int i = 0; i < words.size(); ++i) {
				System.out.printf("%d. %s\n", i + 1, words.get(i));
			}
			
			words.clear();
			System.out.println(DIVIDER_STRING);
			System.out.printf("%s\n", PROMPT_STRING);
			input = scnr.nextLine();
		}
		
		System.out.println(EXIT_STRING);
	}
	
	public static ArrayList<String> permute(ArrayList<String> words, String head, String tail) {
		char current;
		String newTail;
		String newString;
		String newHead;
		int len;
		int i;
		current = '*';
		len = tail.length();
		
		if (len <= 1) {
			++permSubCount;
			newString = head + tail;
			words.add(newString);
		}
		else {
			for (i = len - 1; i >= 0; --i) {
				current = tail.charAt(i);
				newTail = tail.substring(0, i) + tail.substring(i + 1);
				++permSubCount;
				newHead = head + current;
				words.add(newHead);
	            
				permute(words, newHead, newTail);
			} 
		}
		
		return words;
	} 
	
	
	public static boolean wordCheck(String word) throws IOException {
		String jsonString = "";
		URL url = new URL(String.format("https://www.dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s", word, key));
		System.out.printf("Connecting to API to check if \"%s\" is a valid word...\n", word);
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
		con.disconnect();
		return jsonResp(jsonString);	
	}


	public static boolean jsonResp(String jsonString) {
		String[] badTypes = {"abbreviation"};
		JSONParser parse = new JSONParser();
		
		try {
			JSONArray jsonData = (JSONArray) parse.parse(jsonString);
			JSONObject jsonSubData = (JSONObject) jsonData.get(0);
			String fl = (String) jsonSubData.get("fl");
			for(String badType: badTypes) {
				if(fl.equals(badType)) {
					return false;
				}
			}
			return true;
		}
		catch (Exception e){
			 return false;
		}
	}

}


// https://www.srijan.net/blog/how-parse-json-data-rest-api-using-simple-json-library
// https://medium.com/programmers-blockchain/importing-gson-into-eclipse-ec8cf678ad52
// http://www.jsonschema2pojo.org/
