package com.github.gudiasoliveira.javaautomata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Program {
	
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("No option selected!");
			System.exit(1);
			return;
		}
		if (args[0].equals("--help")) {
			displayHelp();
			return;
		}
		
		Automata.Builder<String, String> automataBuilder = new Automata.Builder<>();
//		// For initial tests
//		automata.setStates("A", "B", "C", "D");
//		automata.addFinalStates("B", "D");
//		automata.setInitialState("A");
//		automata.setSymbols("x", "y", "z");
//		automata.transitionFunction("A", "x", "A");
//		automata.transitionFunction("A", "y", "D");
//		automata.transitionFunction("A", "z", "D");
//		automata.transitionFunction("D", "x", "B");
//		automata.transitionFunction("B", "y", "D");
//		automata.transitionFunction("B", "z", "A");
		
		String line = readInput();
		if (line == null) {
			System.err.println("Empty input!");
			System.exit(1);
			return;
		}
			
		if (!line.equalsIgnoreCase("states:")) {
			System.err.println("Invalid input! \"states:\" expected");
			System.exit(1);
			return;
		}
		
		List<String> states = new ArrayList<>(), finalStates = new ArrayList<>();
		String initialState = null;
		while (!"transitions:".equalsIgnoreCase(line = readInput())) {
			if (line == null) {
				System.err.println("Invalid input! No transitions");
				System.exit(1);
				return;
			}
			String[] inputs = line.split(" ");
			states.add(inputs[0].trim());
			if (inputs.length > 1) {
				switch (inputs[1].trim()) {
				case "S": case "SF": case "FS":
					if (initialState != null) {
						System.err.println("Invalid input! Just one initial state allowed");
						System.exit(1);
						return;
					} else {
						initialState = inputs[0].trim();
					}
				case "F":
					if (!inputs[1].trim().equals("S"))
						finalStates.add(inputs[0].trim());
				}
			}
		}
		if (initialState == null) {
			System.err.println("Invalid input! It must have an initial state");
			System.exit(1);
			return;
		}
		automataBuilder.setStates(states);
		automataBuilder.addFinalStates(finalStates);
		automataBuilder.setInitialState(initialState);
		
		List<String> symbols = new ArrayList<String>();
		while ((line = readInput()) != null) {
			String[] inputs = line.split(" ");
			if (inputs.length < 3) {
				System.err.println(
						"Invalid input! Expected three inputs for transition: <from state> <symbol> <to state>");
				System.exit(1);
				return;
			}
			if (!states.contains(inputs[0].trim())) {
				System.err.println("Invalid input! Non-declared state \"" + inputs[0].trim() + "\"");
				System.exit(1);
				return;
			}
			if (!states.contains(inputs[2].trim())) {
				System.err.println("Invalid input! Non-declared state \"" + inputs[2].trim() + "\"");
				System.exit(1);
				return;
			}
			if (!symbols.contains(inputs[1].trim()))
				symbols.add(inputs[1].trim());
			automataBuilder.transitionFunction(inputs[0].trim(), inputs[1].trim(), inputs[2].trim());
		}
		automataBuilder.setSymbols(symbols);
		
		Automata<String, String> automata = automataBuilder.build();
		
		String[] symbolsArgs = new String[args.length - 1];
		for (int i = 0; i < symbolsArgs.length; i++)
			symbolsArgs[i] = args[i+1];
		switch(args[0]) {
		case "--dot-graph":
			System.out.println(toDotGraph(automata));
			break;
		case "--transitions":
			String currentState = automata.getInitialState();
			System.out.println(currentState);
			for_transitions:
			for (int i = 1; i < args.length; i++) {
				currentState = automata.transition(currentState, args[i]);
				if (currentState == null)
					break for_transitions;
				System.out.println(currentState);
			}
			break;
		case "--transition":
			String state = automata.getInitialState();
			state = automata.transition(state, symbolsArgs);
			if (state != null)
				System.out.println(state);
			break;
		case "--accept":
			System.exit(automata.accept(symbolsArgs) ? 0 : 1);
			break;
		case "--nfa2dfa":
			String separator = args.length >= 2 ? args[1].trim() : ",";
			if (!separator.isEmpty())
				separator = separator.substring(0, 1);
			Automata<String, String> nfa = automata.nfa2dfa(separator);
			StringBuilder str = new StringBuilder();
			str.append("states:\n");
			for (String s : nfa.getStates()) {
				str.append(s);
				str.append(" ");
				if (nfa.isInitialState(s))
					str.append("S");
				if (nfa.isFinalState(s))
					str.append("F");
				str.append("\n");
			}
			str.append("\ntransitions:\n");
			for (String s : nfa.getStates()) {
				for (String sy : nfa.getSymbols()) {
					String toS = nfa.transition(s, sy);
					if (toS != null) {
						str.append(s);
						str.append(" ");
						str.append(sy);
						str.append(" ");
						str.append(toS);
						str.append("\n");
					}
				}
			}
			System.out.println(str);
			break;
		case "--help":
			break;
		default:
			System.err.println("Invalid option!");
			System.exit(1);
			return;
		}
	}
	
	private static void displayHelp() {
		System.out.println("Options:");
		System.out.println("--dot-graph\n  "
				+ "Outputs a DOT language Graph of the automata");
		System.out.println("--transitions [sequence of symbols]\n  "
				+ "Outputs the sequence of transitions for the symbols sequence");
		System.out.println("--transition [sequence of symbols]\n  "
				+ "Outputs the current state after a sequence of transitions from the symbols. Outputs none if no state");
		System.out.println("--accept [sequence of symbols]\n  "
				+ "Return status code of success if the sequence of transitions took to a accepter (final) state."
				+ " Failure status code otherwise");
		System.out.println("--nfa2dfa [separator character]\n  "
				+ "Read a non-deterministic automata from input, then output an automata converted to deterministic "
				+ "formatted to this program automata format, where the states label are each state set separating each "
				+ "state by [separator character] (or comma(',') if not specified) (example: \"A,B,D\")");
		System.out.println("--help\n  "
				+ "Display this help");
	}
	
	private static String readInput() {
		String line;
		do {
			try {
				line = reader.readLine();
				if (line == null)
					return null;
				line = line.trim();
			} catch (IOException e) {
				return null;
			}
		} while (line.isEmpty() || line.startsWith("#"));
		return line;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String toDotGraph(Automata automata) {
		StringBuilder str = new StringBuilder();
		str.append("digraph {\n");
		for (Object state : automata.getStates()) {
			Iterator<Map.Entry> it = automata.getTransitions(state).entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry trans = it.next();
				str.append("    \"");
				str.append(state.toString().replace("\\", "\\\\").replace("\"", "\\\""));
				str.append("\" -> \"");
				str.append(trans.getValue().toString().replace("\\", "\\\\").replace("\"", "\\\""));
				str.append("\" [label=\"");
				str.append(trans.getKey().toString().replace("\\", "\\\\").replace("\"", "\\\""));
				str.append("\"]\n");
			}
		}
		str.append("    \"\" [shape=none]\n    \"\" -> \"");
		str.append(automata.getInitialState().toString().replace("\\", "\\\\").replace("\"", "\\\""));
		str.append("\"\n");
		for (Object finalState : automata.getFinalStates()) {
			str.append("    \"");
			str.append(finalState.toString().replace("\\", "\\\\").replace("\"", "\\\""));
			str.append("\" [peripheries=2]\n");
		}
		str.append("}");
		return str.toString();
	}

}