import java.util.*;
import java.util.Map.Entry;
import java.io.*;

 public class Main {

   static Map<String, ArrayList<String>> rules = new HashMap<String,ArrayList<String>>();
   static ArrayList<String> usedLetters = new ArrayList<String>();
   static ArrayList<String> nonterminals = new ArrayList<String>();
   static ArrayList<String> terminals = new ArrayList<String>();
   static ArrayList<Character> alphabet = new ArrayList<Character>();
   static String[] types =  new String[] {"TERMINAL","RULES","START"};
   static Map<String, ArrayList<String>> dict = new HashMap<String,ArrayList<String>>();

    @SuppressWarnings("unchecked")
	public static void main(String args[]) 
    {
    	// alphabet created
    	   for(char c1 = 'A'; c1<='Z';c1++) {
    		   alphabet.add(c1);
    	   }
    	   for(char c2 = 'a'; c2<'z';c2++) {
    		   if(c2 == 'e') {
    			   continue;
    		   }
    		   alphabet.add(c2);
    	   }
    	   for(char c3 = '0'; c3<= '9';c3++) {
    		   alphabet.add(c3);
    	   }
    	   // read file
    	   try {
    		   File file = new File("G1.txt");
    		   Scanner reader = new Scanner(file);
			   Boolean isRules = false;
			   String S = "";
			   String start = "";
			   String end = "";
    		   while(reader.hasNextLine()) {
    			   String data = reader.nextLine();
    			   if(data.equals("RULES")) {
    				   isRules = true;
    			   }
    			   if(data.equals("START")) {
    				   isRules = false;
    			   }
    			   if(!reader.hasNextLine()) {
    				   S = data;
    			   }
    			   if(isRules && !data.equals("RULES") && !data.equals("START")) {
    				   start = data.split(":")[0];
    				   end = data.split(":")[1];
    			   }
    			   for(int i=0;i<start.length();i++) {
					   String item = Character.toString(start.charAt(i));
					   if(start.charAt(i) != 'e' && !usedLetters.contains(item)) {
						   usedLetters.add(item);
					   }
					   if(alphabet.contains(start.charAt(i))) {
						   int ind = alphabet.indexOf(start.charAt(i));
						   alphabet.remove(ind);
					   }
				   }
    			   for(int i=0;i<end.length();i++) {
					   String item = Character.toString(end.charAt(i));
					   if(end.charAt(i) != 'e' && !usedLetters.contains(item)) {
						   usedLetters.add(item);
					   }
					   if(alphabet.contains(end.charAt(i))) {
						   int ind = alphabet.indexOf(end.charAt(i));
						   alphabet.remove(ind);
					   }
				   }
    			   if(start.length() > 0 && isRules) {
        		   if(rules.containsKey(start)) {
        			   rules.get(start).add(end);
        		   }
        		   else if(!rules.containsKey(start)) {
        			   ArrayList<String> endList = new ArrayList<String>(); 
        			   endList.add(end);
        			   rules.put(start,endList);
        		   }
    			   }
    		   }
    		   reader.close();
  			 HashMap<String, ArrayList<String>> dict = new HashMap<>();
  			ArrayList<Object> obj;
    		   obj = longRule(rules, alphabet, usedLetters);
    		   rules = (Map<String, ArrayList<String>>) obj.get(0);
    		   alphabet = (ArrayList<Character>) obj.get(1);
    		   usedLetters = (ArrayList<String>) obj.get(2);
    		   // remove epsilons 
    		   ArrayList<Object> obj2;
    		   obj2 = removeEpsilon(rules, usedLetters); 
    		   rules = (Map<String, ArrayList<String>>) obj2.get(0);
    		   usedLetters = (ArrayList<String>) obj2.get(1) ;
    		   //remove unit transitions 
    		   ArrayList<Object> obj3;
    		   obj3 = removeUnitTransitions(rules,usedLetters);
    		   rules = (Map<String, ArrayList<String>>) obj3.get(0);
    		   dict = (HashMap<String, ArrayList<String>>) obj3.get(1);
    		   // last step
    		   ArrayList<Object> obj4;
    		   obj4 = lastStep(rules,dict,S);
    		   String newStart = S + "0";
    		   ArrayList<String> newTransition = new ArrayList<String>();
    		   newTransition.add(S);
    		   rules.put(newStart,newTransition);
    		   for(int i=0;i<usedLetters.size();i++) {
    			   if(!(usedLetters.get(i).charAt(0) >= 'A' && usedLetters.get(i).charAt(0) <= 'Z')) {
    				   terminals.add(usedLetters.get(i));
    			   }
    			   else {
    				   nonterminals.add(usedLetters.get(i));
    			   }
    		   }
    		   System.out.println("NONTERMNIALS");
    		   for(int i=0;i<nonterminals.size();i++) {
    			   System.out.println(nonterminals.get(i));
    		   }
    		   System.out.println("TERMINAL");
    		   for(int i=0;i<terminals.size();i++) {
    			   System.out.println(terminals.get(i));
    		   }
    		   System.out.println("RULES");
    		   for(Object key: rules.keySet()) {
    			   String rule = key.toString() + ":";
    			   for(int j=0;j<rules.get(key).size();j++) {
    				   System.out.println(rule + rules.get(key).get(j));
    			   }
    		   }
    		   System.out.println("START");
    		   System.out.println(S);

    	   } catch(FileNotFoundException e) {
    		   System.out.println("There is an error.");
    		   e.printStackTrace();
    	   }
    }
    
    private static ArrayList<Object> lastStep(Map<String, ArrayList<String>> rules2, HashMap<String, ArrayList<String>> dict,
			String s) {
    	for(Object key: dict.get(s)) {
			if(((rules.get(s) == null  && rules.get(key) == null))) {
				for(int i=0;i<rules.get(key).size();i++) {
					if(!rules.get(s).contains(rules.get(key))) {
						rules.get(s).add(rules.get(key).get(i));
					}
				}
			}
		}
    	ArrayList<Object> res = new ArrayList<Object>();
    	res.add(rules);
		return res;
	}

	private static ArrayList<Object> removeUnitTransitions(Map<String, ArrayList<String>> rules2, ArrayList<String> usedLetters2) {
    	HashMap<String, ArrayList<String>> dict = new HashMap<>();
    	for(int i=0;i<usedLetters2.size();i++) {
    		ArrayList<String> dictVal = new ArrayList<>();
    		dictVal.add(usedLetters2.get(i));
    		dict.put(usedLetters2.get(i), dictVal);
    	}
    	for(int i=0;i<usedLetters2.size();i++) {
    		for(Object key: rules2.keySet()) {
    			if(dict.get(usedLetters2.get(i)).contains(key)) {
    				ArrayList<String> values = rules2.get(key);
    				for(int j=0;j<values.size();j++) {
    					if(values.get(j).length() == 1 && !dict.get(usedLetters2.get(i)).contains(values.get(j))) {
    						dict.get(usedLetters2.get(i)).add(values.get(j));
    					}
    				}
    			}
    		}
    	}
    	ArrayList<Object> res = new ArrayList<Object>();
    	res = removeShortTransitions(rules2,dict);
		return res;
	}

	private static ArrayList<Object> removeShortTransitions(Map<String, ArrayList<String>> rules2,
			HashMap<String, ArrayList<String>> dict) {
		Map<String, ArrayList<String>> deepCopy = deepCopyWorkAround(rules2);
		for(Object key: deepCopy.keySet()) {
			ArrayList<String> values = deepCopy.get(key);
			for(int i=0;i<values.size();i++) {
				if(values.get(i).length() == 1) {
					rules2.get(key).remove(i);
				}
			}
			if(rules.get(key).size() == 0) {
				rules2.remove(key,null);
			}
		}
		for(Object key: rules2.keySet()) {
			ArrayList<String> values = rules2.get(key);
			for(int i=0;i<values.size();i++) {
				for(int j=0;j<dict.get(Character.toString(values.get(i).charAt(0))).size();j++) {
					for(int k=0;k<dict.get(Character.toString(values.get(i).charAt(1))).size();k++) {
						if(!values.contains((dict.get(Character.toString(values.get(i).charAt(0))).get(j)  + dict.get(Character.toString(values.get(i).charAt(1))).get(k)))) {
							rules.get(key).add((dict.get(Character.toString(values.get(i).charAt(0))).get(j)  + dict.get(Character.toString(values.get(i).charAt(1))).get(k))) ;
						}
					}
				}
			}
		}
		ArrayList<Object> res = new ArrayList<Object>();
		res.add(rules2);
		res.add(dict);
		return res; 
	}

	public static <T> Map<String, ArrayList<String>> deepCopyWorkAround(Map<String, ArrayList<String>> rules2)
    {
        HashMap<String, ArrayList<String>> copy = new HashMap<>();
        for (Entry<String, ArrayList<String>> entry : rules2.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }


	private static ArrayList<Object> longRule(Map<String, ArrayList<String>> rules2, ArrayList<Character> alphabet2,
			ArrayList<String> usedLetters2) {
		Map<String, ArrayList<String>> deepCopy = deepCopyWorkAround(rules2);
		for(String key: deepCopy.keySet()) {
			ArrayList<String> values = deepCopy.get(key);
			for(int i=0;i<values.size();i++) {
				if(values.get(i).length() > 2) {
					Character newKey = null;
					for(int j=0;j<values.get(i).length() -2;j++) {
						if(j == 0) {
							for(int k=0;k<rules2.get(key).size();k++) {
								if(k == i) {
									String variable = Character.toString(rules2.get(key).get(i).charAt(0))  + alphabet2.get(0);
									rules2.get(key).remove(i);
									rules2.get(key).add(i, variable);
								}
						}
						}
						else {
							ArrayList<String> newValues = new ArrayList<String>();
							newValues.add(Character.toString(values.get(i).charAt(j)) + alphabet2.get(0));
							rules2.put(key, newValues);
						}
						usedLetters2.add(Character.toString(alphabet2.get(0))) ;
						newKey = alphabet2.get(0);
						alphabet2.remove(0);
					}
					ArrayList<String> newValues = new ArrayList<String>();
					newValues.add(values.get(i).substring(values.get(i).length() -2 ));
					rules2.put(Character.toString(newKey), newValues);
				}
			}
		}
			ArrayList<Object> res = new ArrayList<Object>();  
			res.add(rules2);
			res.add(alphabet2);
			res.add(usedLetters2);
		   return res;
	}


	private static ArrayList<Object> removeEpsilon(Map<String, ArrayList<String>> rules2, ArrayList<String> usedLetters2) {
		ArrayList<String> epsList = new ArrayList<String>(); 
		Map<String, ArrayList<String>> deepCopy = deepCopyWorkAround(rules2);
		for(String key: deepCopy.keySet()) {
			ArrayList<String> values = deepCopy.get(key);
			for(int i=0;i<values.size();i++) {
				if(values.get(i).equals("e") && !(epsList).contains(key)) {
					(epsList).add(key);
					 rules2.get(key).remove(values.get(i));
				}
			}

			if(rules2.get(key).size()  == 0 ) {
				if(!rules2.containsKey(key)) {
					int index = usedLetters2.indexOf(key);
					usedLetters2.remove(index);
				}
				rules2.remove(key, null);
			}
		}
		 deepCopy = deepCopyWorkAround(rules2);
		 for(String key: deepCopy.keySet()) {
			ArrayList<String> values = deepCopy.get(key);
			for(int i=0;i<values.size();i++) {
				if(values.get(i).length() == 2) {
					if((epsList).contains(Character.toString(values.get(i).charAt(0)))  && key != Character.toString((values.get(i).charAt(1)))) {
						rules2.get(key).add(Character.toString(values.get(i).charAt(1)));

					}
					if((epsList).contains(Character.toString(values.get(i).charAt(1))) && key != Character.toString((values.get(i).charAt(0)))) {
						if(values.get(i).charAt(1) != values.get(i).charAt(0)) {
							rules2.get(key).add(Character.toString(values.get(i).charAt(0)));
						}
					}
 				}
			}
		}
		 ArrayList<Object> res = new ArrayList<Object>(); 
		 res.add(rules2);
		 res.add(usedLetters2);
			return res;
	}
	
}