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
			String[] symbolsArgs = new String[args.length - 1];
			for (int i = 0; i < symbolsArgs.length; i++)
				symbolsArgs[i] = args[i+1];
			state = automata.transition(state, symbolsArgs);
			if (state != null)
				System.out.println(state);
			break;
		}
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
	
	private static String toDotGraph(Automata automata) {
		StringBuilder str = new StringBuilder();
		str.append("digraph {\n");
		for (Object state : automata.getStates()) {
			Iterator<Map.Entry> it = automata.getTransitions(state).entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry trans = it.next();
				str.append("    ");
				str.append(state);
				str.append(" -> ");
				str.append(trans.getValue());
				str.append(" [label=\"");
				str.append(trans.getKey());
				str.append("\"]\n");
			}
		}
		str.append("    \"\" [shape=none]\n    \"\" -> ");
		str.append(automata.getInitialState());
		str.append("\n");
		for (Object finalState : automata.getFinalStates()) {
			str.append("    ");
			str.append(finalState);
			str.append(" [peripheries=2]\n");
		}
		str.append("}");
		return str.toString();
	}

}