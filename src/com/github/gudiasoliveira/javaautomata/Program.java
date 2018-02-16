package com.github.gudiasoliveira.javaautomata;

import java.util.Iterator;
import java.util.Map;

public class Program {

	public static void main(String[] args) {
		Automata.Builder<String, String> automata = new Automata.Builder<>();
		automata.setStates("A", "B", "C", "D");
		automata.addFinalStates("B", "D");
		automata.setInitialState("A");
		automata.setSymbols("x", "y", "z");
		automata.transitionFunction("A", "x", "A");
		automata.transitionFunction("A", "y", "D");
		automata.transitionFunction("A", "z", "D");
		automata.transitionFunction("D", "x", "B");
		automata.transitionFunction("B", "y", "D");
		automata.transitionFunction("B", "z", "A");
		System.out.println(toDotGraph(automata.build()));
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