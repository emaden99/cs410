import java.util.*;

import java.io.*;

public class Main {
	static ArrayList<String> alphabet = new ArrayList<String>();
	static ArrayList<String> states = new ArrayList<String>();
	static String startState = "";
	static String finalState = "";
	static ArrayList<String> finalStates = new ArrayList<String>();
	static ArrayList<String> transitions = new ArrayList<String>();
	static ArrayList<String> newTransitions = new ArrayList<String>();
	static ArrayList<String> newTransitionHeads = new ArrayList<String>();
	static ArrayList<String> newTransitionTails = new ArrayList<String>();
	static ArrayList<String> transitionDifferences = new ArrayList<String>();
	static ArrayList<String> filtered = new ArrayList<String>();
	static ArrayList<String> emptyTransitions = new ArrayList<String>();

	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
		ArrayList<T> newList = new ArrayList<T>();
		for (T element : list) {
			if (!newList.contains(element)) {

				newList.add(element);
			}
		}
		return newList;
	}

	static String removeDuplicate(char str[], int n) {
		int index = 0;
		for (int i = 0; i < n; i++) {
			int j;
			for (j = 0; j < i; j++) {
				if (str[i] == str[j]) {
					break;
				}
			}
			if (j == i) {
				str[index++] = str[i];
			}
		}
		return String.valueOf(Arrays.copyOf(str, index));
	}

	public static String sortString(String inputString) {
		char tempArray[] = inputString.toCharArray();
		Arrays.sort(tempArray);
		return new String(tempArray);
	}

	public static void readFile(String filename) throws FileNotFoundException {
		File NFAText = new File(filename);
		Scanner NFAScanner = new Scanner(NFAText);
		String name = "";
		while (NFAScanner.hasNextLine()) {
			String data = NFAScanner.nextLine();
			if (data.length() > 2 && data.split(" ").length == 1) {
				name = data;
			} else {
				if (name.equals("ALPHABET")) {
					alphabet.add(data);
				}
				if (name.equals("STATES")) {
					states.add(data);
				}
				if (name.equals("START")) {
					startState = data;
				}
				if (name.equals("FINAL")) {
					finalState = data;
				}
				if (name.equals("TRANSITIONS")) {
					transitions.add(data);
				}
			}

		}
		newTransitions = (ArrayList<String>) transitions.clone();
		finalStates.add(finalState);

		NFAScanner.close();
	}

	public static void writeFile(String filename, ArrayList<String> alphabet, ArrayList<String> states,
			String startState, ArrayList<String> finalStates, ArrayList<String> transitions) throws IOException {

		File file22 = new File(filename);
		BufferedWriter myWriter = new BufferedWriter(new FileWriter(file22));
		myWriter.write("ALPHABET");
		for (int i = 0; i < alphabet.size(); i++) {
			myWriter.write("\n" + alphabet.get(i));
		}
		myWriter.write("\n" + "STATES");
		for (int i = 0; i < states.size(); i++) {
			myWriter.write("\n" + states.get(i));

		}
		myWriter.write("\n" + "START");
		myWriter.write("\n" + startState);
		myWriter.write("\n" + "FINAL");
		for (int i = 0; i < finalStates.size(); i++) {
			myWriter.write("\n" + finalStates.get(i));
		}
		myWriter.write("\n" + "TRANSITIONS");
		for (int i = 0; i < transitions.size(); i++) {
			myWriter.write("\n" + transitions.get(i));
		}
		myWriter.write("\n" + "END");
		myWriter.close();
	}

	public static void NFAtoDFAConverter(String filename) throws IOException {
		readFile(filename);
		for (int i = 0; i < transitions.size(); i++) {
			newTransitionHeads.add(transitions.get(i).split(" ")[0]);
			newTransitionTails.add(transitions.get(i).split(" ")[transitions.get(i).split(" ").length - 1]);
		}
		for (int i = 0; i < newTransitionTails.size(); i++) {
			if (!newTransitionHeads.contains(newTransitionTails.get(i))) {
				transitionDifferences.add(newTransitionTails.get(i));
			}
		}
		int count = 0;
		while (count < states.size()) {
			for (int i = 0; i < states.size(); i++) {
				for (int j = 0; j < alphabet.size(); j++) {
					String searched = states.get(i) + " " + alphabet.get(j);
					filtered = new ArrayList<String>();

					for (int k = 0; k < transitions.size(); k++) {
						if (transitions.get(k).startsWith(searched)) {
							filtered.add(transitions.get(k));
						}
					}

					if (filtered.size() == 1) {
						newTransitions.add(filtered.get(0));
					}
					if (filtered.size() > 1) {
						String res = filtered.get(0);
						for (int l = 1; l < filtered.size(); l++) {
							res += filtered.get(l).split(" ")[filtered.get(l).split(" ").length - 1];
						}
						newTransitions.add(res);
					}

				}
			}
			newTransitionTails = new ArrayList<String>();
			newTransitionHeads = new ArrayList<String>();
			transitionDifferences = new ArrayList<String>();
			for (int m = 0; m < newTransitions.size(); m++) {
				String tail = sortString(newTransitions.get(m).split(" ")[newTransitions.get(m).split(" ").length - 1]);
				newTransitionTails.add(tail);
				String head = sortString(newTransitions.get(m).split(" ")[0]);
				newTransitionHeads.add(head);
			}
			newTransitionTails = removeDuplicates(newTransitionTails);
			newTransitionHeads = removeDuplicates(newTransitionHeads);
			for (int n = 0; n < newTransitionTails.size(); n++) {
				if (!newTransitionHeads.contains(newTransitionTails.get(n))) {
					transitionDifferences.add(newTransitionTails.get(n));
				}
			}
			while (transitionDifferences.size() > 0) {
				for (int i = 0; i < transitionDifferences.size(); i++) {
					for (int j = 0; j < alphabet.size(); j++) {
						String transitionLine = transitionDifferences.get(i) + " " + alphabet.get(j) + " ";
						String tails = "";
						for (int k = 0; k < transitionDifferences.get(i).length(); k++) {
							ArrayList<String> matching = new ArrayList<String>();
							for (int l = 0; l < newTransitions.size(); l++) {
								if (newTransitions.get(l)
										.contains((transitionDifferences.get(i).charAt(k) + " " + alphabet.get(j)))) {
									matching.add(newTransitions.get(l));
								}
							}
							if (matching.size() > 1 && matching.get(matching.size() - 1).split(" ").length > 2) {
								tails += sortString(matching.get(matching.size() - 1).split(" ")[2]);
							}
							if (!states.contains(transitionDifferences.get(i))) {
								states.add(transitionDifferences.get(i));
							}
						}
						tails = sortString(removeDuplicate(tails.toCharArray(), tails.length()));
						transitionLine += tails;
						newTransitions.add(transitionLine);
					}
				}
				newTransitionTails = removeDuplicates(newTransitionTails);
				for (int m = 0; m < newTransitions.size(); m++) {
					newTransitionHeads.add(newTransitions.get(m).split(" ")[0]);
				}
				newTransitionHeads = removeDuplicates(newTransitionHeads);
				transitionDifferences = new ArrayList<String>();
				for (int n = 0; n < newTransitionTails.size(); n++) {
					if (!newTransitionHeads.contains(newTransitionTails.get(n))) {
						transitionDifferences.add(newTransitionTails.get(n));
					}
				}
			}
			count += 1;
			for (int i = 0; i < states.size(); i++) {
				for (int j = 0; j < alphabet.size(); j++) {
					String searched = states.get(i) + " " + alphabet.get(j);

					filtered = new ArrayList<String>();

					for (int k = 0; k < newTransitions.size(); k++) {
						if (newTransitions.get(k).startsWith(searched)) {
							filtered.add(newTransitions.get(k));
						}
					}

					if (filtered.size() > 1) {
						for (int l = 0; l < filtered.size(); l++) {
							newTransitions.remove(filtered.get(l));
						}

					}

					if (filtered.size() < 1) {
						emptyTransitions.add(searched + " Ø");
					}
				}

			}
			for (int i = 0; i < states.size(); i++) {
				if (states.get(i).contains(finalState)) {
					finalStates.add(states.get(i));
				}
			}
		}
		emptyTransitions = removeDuplicates(emptyTransitions);
		newTransitions = removeDuplicates(newTransitions);
		newTransitions.addAll(emptyTransitions);
		finalStates = removeDuplicates(finalStates);
		filename = filename.replace(filename.charAt(0), 'D');
		writeFile(filename, alphabet, states, startState, finalStates, newTransitions);
	}

	public static void main(String[] args) throws IOException {
		NFAtoDFAConverter("NFA2.txt");
	}
}