import java.util.*;
import java.io.*;


 public class Main {

	 static String[] states;
	 static String initialState ;
	 static String acceptState;
	 static String rejectState;
	 static char blankSymbol;
	 static ArrayList<Character> tapeAlphabet = new ArrayList<Character>();
	 static ArrayList<Character> tape = new ArrayList<Character>();
	 static int tapeHead = 0 ;
	 static String state;

	 static ArrayList<String> transitionList = new ArrayList<>();
     
     static HashMap<String, HashMap<Character, Object[]>> transitionMap = new HashMap<>();
     static ArrayList<String> searchedStrings = new ArrayList<>();
     static ArrayList<Object[]> result = new ArrayList<>();

	public static void main(String args[]) throws IOException 
    {
        readFile("Input_Ezgi_Maden_S018268.txt");
        for (String t : transitionList) {
            if (!transitionMap.containsKey((t.split(" ")[0])))
                transitionMap.put(t.split(" ")[0], new HashMap<>());
            transitionMap.get(t.split(" ")[0]).put(t.split(" ")[1].charAt(0), new Object[]{t.split(" ")[2], t.split(" ")[3], t.split(" ")[4]});
        }
        result = readStrings();
        printResult(result);
    }


	private static void readFile(String filename) {
		// TODO Auto-generated method stub
		try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      Integer count = 1;
		      while (myReader.hasNextLine()) {
		    	String data = myReader.nextLine();
		        if(count == 1) {
		        	String[] line = data.split(" ");
		        	blankSymbol = line[2].toCharArray()[0];
		        	for(int i=0;i<line.length;i++) {
		        		tapeAlphabet.add(line[i].toCharArray()[0]);
		        	}
		        }
		        else if(count == 2) {
		        	states = data.split(" ");
		        }
		        else if(count == 3) {
		        	initialState = data;
		            state = initialState;
		        }
		        else if(count == 4) {
		        	acceptState = data;
		        }
		        else if(count == 5) {
		        	rejectState = data;
		        }
		        else if(!myReader.hasNextLine()) {
		        	String[] line = data.split(" ");
		        	for(int i=0;i<line.length;i++) {
		        		searchedStrings.add(line[i]);
		        	}
		        }
		        else {
		        	transitionList.add(data);
		        }
		    	
		        count += 1;
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}


	private static void printResult(ArrayList<Object[]> result2) throws IOException {
		// TODO Auto-generated method stub
		String result_string = "";
        for (Object[] result : result2) {
            boolean accepted = (boolean) result[0];
            @SuppressWarnings("unchecked")
			ArrayList<String> route = (ArrayList<String>) result[1];
            result_string += "ROUT:     ";
            for (String state : route)
                result_string += state + " ";
            result_string += "\nRESULT:   ";
            if (accepted)
                result_string += "Accepted\n";
            else
                result_string += "Rejected\n";
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
        writer.write(result_string);
        writer.close();
        System.out.println(result_string);
	}


	private static ArrayList<Object[]> readStrings() {
		// TODO Auto-generated method stub
		ArrayList<Object[]> results = new ArrayList<>();
        for (String string : searchedStrings) {
            results.add(readString(string));
            state = initialState;
        }
               return results;
	}
	


	private static Object[] readString(String string) {
		// TODO Auto-generated method stub
		tape.clear();
        for (char c : string.toCharArray()) {
        	tape.add(c);
        }
        tape.add(blankSymbol);
        tapeHead = 0;
        ArrayList<String> route = new ArrayList<>();
        route.add(initialState);
        return getTransitions(route);
	}


	private static Object[] getTransitions(ArrayList<String> route) {
		// TODO Auto-generated method stub
        if (state.equals(acceptState)) {
            return new Object[]{true, route};}
        if (state.equals(rejectState)) {
        	return new Object[]{false, route};

        }
        Object[] transitionResult;
        if (transitionMap.containsKey(state) &&
                transitionMap.get(state).containsKey((tape.get(tapeHead)))) {
            transitionResult = transitionMap.get(state).get((tape.get(tapeHead)));
        } else {
            route.add(rejectState);
            return new Object[]{false, route};
        }
        char write = transitionResult[0].toString().charAt(0);
        char direction = transitionResult[1].toString().charAt(0);
        String state2 =  transitionResult[2].toString();
        tape.set(tapeHead, write);
        if (direction == 'L')
            tapeHead = Integer.max(tapeHead - 1, 0);
        else
            tapeHead += 1;
        if (!route.get(route.size() - 1).equals(state2))
            route.add(state2);
        state = state2;
        updateBlanks();
        return getTransitions(route);
	}


	private static void updateBlanks() {
		// TODO Auto-generated method stub
		if (tape.get(tape.size() - 1) != blankSymbol) {
			tape.add(blankSymbol);
		}
	}
    
 }