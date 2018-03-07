package com.github.gudiasoliveira.javaautomata;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Automata<TState, TSymbol> {
	
	private static class Transition<TState, TSymbol> {
		TState stateIn, stateOut;
		TSymbol symbol;
		
		Transition() {
		}
		
		Transition(TState stateIn, TSymbol symbol, TState stateOut) {
			this.stateIn = stateIn;
			this.symbol = symbol;
			this.stateOut = stateOut;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Transition))
				return false;
			Transition other = (Transition) obj;
			if (!this.stateIn.equals(other.stateIn))
				return false;
			if (!this.symbol.equals(other.symbol))
				return false;
			if (!this.stateOut.equals(other.stateOut))
				return false;
			return true;
		}
	}
	
	
	private TSymbol[] mSymbols;
	private TState[] mStates;
	private int mInitialStateIndex;
	private int[] mFinalStateIndexes;
	private Transition<TState, TSymbol>[] mTransitionFunction;
	
	private Automata() {
	}
	
	
	public static class Builder<TState, TSymbol> {
		private List<TSymbol> mSymbols = new ArrayList<>();
		private List<TState> mStates = new ArrayList<>();
		private TState mInitialState;
		private List<TState> mFinalStates = new ArrayList<>();
		private List<Transition<TState, TSymbol>> mTransitionFunction = new ArrayList<>();
		
		public Builder<TState, TSymbol> setSymbols(List<TSymbol> symbols) {
			mSymbols.clear();
			mSymbols.addAll(symbols);
			return this;
		}
		
		public Builder<TState, TSymbol> setSymbols(TSymbol... symbols) {
			mSymbols.clear();
			for (TSymbol symbol : symbols)
				mSymbols.add(symbol);
			return this;
		}
		
		public Builder<TState, TSymbol> setStates(List<TState> states) {
			mStates.clear();
			mStates.addAll(states);
			return this;
		}
		
		public Builder<TState, TSymbol> setStates(TState... states) {
			mStates.clear();
			for (TState state : states)
				mStates.add(state);
			return this;
		}
		
		public Builder<TState, TSymbol> addFinalStates(TState...states) {
			for (TState state : states) {
				mFinalStates.add(state);
			}
			return this;
		}
		
		public Builder<TState, TSymbol> addFinalStates(List<TState> states) {
			for (TState state : states) {
				mFinalStates.add(state);
			}
			return this;
		}
		
		public Builder<TState, TSymbol> setInitialState(TState state) {
			mInitialState = state;
			return this;
		}
		
		public Builder<TState, TSymbol> transitionFunction(TState fromState, TSymbol symbol, TState toState) {
			mTransitionFunction.add(new Transition<>(fromState, symbol, toState));
			return this;
		}
		
		@SuppressWarnings("rawtypes")
		private void removeDuplicates(List list) {
			List newList = new ArrayList();
			for (Object e : list)
				if (!newList.contains(e))
					newList.add(e);
			list.clear();
			list.addAll(newList);
		}
		
		public Automata<TState, TSymbol> build() {
			Automata<TState, TSymbol> automata = new Automata<>();
			
			removeDuplicates(mStates);
			removeDuplicates(mSymbols);
			removeDuplicates(mFinalStates);
			removeDuplicates(mTransitionFunction);
			
			automata.mStates = (TState[]) mStates.toArray((TState[]) (
					!mStates.isEmpty()
					? Array.newInstance(mStates.get(0).getClass(), mStates.size())
					: new Object[0]));
			automata.mSymbols= (TSymbol[]) mSymbols.toArray();
			
			automata.mInitialStateIndex = 0;
			int i = 0;
			for (TState state : automata.mStates) {
				if (state.equals(mInitialState)) {
					automata.mInitialStateIndex = i;
					break;
				}
				i++;
			}
			
			List<Integer> finalStateIndexes = new ArrayList<>();
			i = 0;
			for (TState state : automata.mStates) {
				if (mFinalStates.contains(state)) {
					finalStateIndexes.add(i);
				}
				i++;
			}
			removeDuplicates(finalStateIndexes);
			automata.mFinalStateIndexes = new int[finalStateIndexes.size()];
			for (i = 0; i < finalStateIndexes.size(); i++)
				automata.mFinalStateIndexes[i] = finalStateIndexes.get(i);
			
			automata.mTransitionFunction = new Transition[mTransitionFunction.size()];
			i = 0;
			for (Transition<TState, TSymbol> trans : mTransitionFunction) {
				automata.mTransitionFunction[i] = trans;
				i++;
			}
			
			return automata;
		}
	}
	
	
	public TState transition(TState state, TSymbol... symbols) {
		TState currentState = state;
		for (TSymbol symbol : symbols) {
			boolean found = false;
			for (Transition<TState, TSymbol> trans : mTransitionFunction) {
				if (trans.stateIn.equals(currentState) && trans.symbol.equals(symbol)) {
					currentState = trans.stateOut;
					found = true;
					break;
				}
			}
			if (!found)
				return null;
		}
		return currentState;
	}
	
	public Map<TSymbol, TState> getTransitions(TState state) {
		Map<TSymbol, TState> transitions = new HashMap<>();
		for (Transition<TState, TSymbol> trans : mTransitionFunction) {
			if (trans.stateIn.equals(state)) {
				transitions.put(trans.symbol, trans.stateOut);
			}
		}
		return transitions;
	}
	
	public TState getState(int index) {
		return mStates[index];
	}
	
	public TState[] getStates() {
		TState[] states = (TState[]) (mStates.length > 0 ? Array.newInstance(mStates[0].getClass(), mStates.length) : new Object[0]);
		for (int i = 0; i < states.length; i++)
			states[i] = mStates[i];
		return states;
	}
	
	public int getStatesCount() {
		return mStates.length;
	}
	
	public TSymbol getSymbol(int index) {
		return mSymbols[index];
	}
	
	public TSymbol[] getSymbols() {
		TSymbol[] symbols = (TSymbol[]) (mSymbols.length > 0 ? Array.newInstance(mSymbols[0].getClass(), mSymbols.length) : new Object[0]);
		for (int i = 0; i < symbols.length; i++)
			symbols[i] = mSymbols[i];
		return symbols;
	}
	
	public int getSymbolsCount() {
		return mSymbols.length;
	}
	
	public TState getInitialState() {
		return mStates[mInitialStateIndex];
	}
	
	public boolean isInitialState(TState state) {
		return mStates[mInitialStateIndex].equals(state);
	}
	
	public TState[] getFinalStates() {
		TState[] finalStates = (TState[]) (mStates.length > 0 ? Array.newInstance(mStates[0].getClass(), mFinalStateIndexes.length) : new Object[0]);
		for (int i = 0; i < finalStates.length; i++)
			finalStates[i] = mStates[mFinalStateIndexes[i]];
		return finalStates;
	}
	
	public boolean isFinalState(TState state) {
		int index = -1;
		for (int i = 0; i < mStates.length; i++) {
			if (mStates[i].equals(state)) {
				index = i;
				break;
			}
		}
		
		for (int i = 0; i < mFinalStateIndexes.length; i++)
			if (mFinalStateIndexes[i] == index)
				return true;
		return false;
	}
	
	public boolean accept(TSymbol... symbols) {
		return isFinalState(transition(getInitialState(), symbols));
	}
	
	public HashSet<TState> transitionNFA(TState stateIn, TSymbol... symbols) {
		HashSet<TState> statesIn = new HashSet<>();
		statesIn.add(stateIn);
		return transitionNFA(statesIn, symbols);
	}
	
	private HashSet<TState> transitionNFA(HashSet<TState> statesIn, TSymbol... symbols) {
		HashSet<TState> currentStates = new HashSet<>();
		currentStates.addAll(statesIn);
		for (TSymbol symbol : symbols) {
			HashSet<TState> nextStates = new HashSet<>();
			for (Transition<TState, TSymbol> transition : mTransitionFunction) {
				if (transition.symbol.equals(symbol) && currentStates.contains(transition.stateIn) && !nextStates.contains(transition.stateOut))
					nextStates.add(transition.stateOut);
			}
			currentStates.clear();
			currentStates.addAll(nextStates);
		}
		return currentStates;
	}
	
	public Automata<HashSet<TState>, TSymbol> nfa2dfa() {
		Automata.Builder<HashSet<TState>, TSymbol> automataB = new Automata.Builder<>();
		
		automataB.mInitialState = new HashSet<>();
		automataB.mInitialState.add(this.getInitialState());
		
		automataB.mStates = new ArrayList<>();
		automataB.mStates.add(automataB.mInitialState);
		
		automataB.mSymbols = new ArrayList<>();
		automataB.mTransitionFunction = new ArrayList<>();
		for (TSymbol symbol : this.mSymbols) {
			automataB.mSymbols.add(symbol);
			HashSet<TState> nextStates = transitionNFA(automataB.mInitialState, symbol);
			if (!automataB.mStates.contains(nextStates) && !nextStates.isEmpty())
				automataB.mStates.add(nextStates);
			
			if (!nextStates.isEmpty()) {
				Transition<HashSet<TState>, TSymbol> trans = new Transition<>();
				trans.stateIn = automataB.mStates.get(0);
				trans.stateOut = automataB.mStates.get(automataB.mStates.indexOf(nextStates));
				trans.symbol = symbol;
				automataB.mTransitionFunction.add(trans);
			}
		}
		
		for (int i = 1; i < automataB.mStates.size(); i++) {
			for (TSymbol symbol : this.mSymbols) {
				HashSet<TState> nextStates = transitionNFA(automataB.mStates.get(i), symbol);
				if (!automataB.mStates.contains(nextStates) && !nextStates.isEmpty())
					automataB.mStates.add(nextStates);
				
				if (!nextStates.isEmpty()) {
					Transition<HashSet<TState>, TSymbol> trans = new Transition<>();
					trans.stateIn = automataB.mStates.get(i);
					trans.stateOut = automataB.mStates.get(automataB.mStates.indexOf(nextStates));
					trans.symbol = symbol;
					automataB.mTransitionFunction.add(trans);
				}
			}
		}
		
		automataB.mFinalStates = new ArrayList<>();
		TState[] thisFinalStates = this.getFinalStates();
		for (HashSet<TState> state : automataB.mStates) {
			for (TState finalState : thisFinalStates) {
				if (state.contains(finalState)) {
					automataB.mFinalStates.add(state);
					break;
				}
			}
		}
		
		return automataB.build();
	}
	
	public<TStateTo> Automata<TStateTo, TSymbol> nfa2dfa(NFAStateConverter<TState, TStateTo> converter) {
		Automata<HashSet<TState>, TSymbol> rawAutomata = nfa2dfa();
		
		HashMap<HashSet<TState>, TStateTo> convertStatesMap = new HashMap<>();
		for (HashSet<TState> rawState : rawAutomata.mStates)
			convertStatesMap.put(rawState, converter.convertNFAState(rawState));
		
		Automata<TStateTo, TSymbol> a = new Automata<TStateTo, TSymbol>();
		a.mInitialStateIndex = rawAutomata.mInitialStateIndex;
		a.mFinalStateIndexes = rawAutomata.mFinalStateIndexes;
		a.mSymbols = rawAutomata.mSymbols;
		
		int length = ((Object[]) rawAutomata.mStates).length;
		a.mStates = (TStateTo[]) (length > 0 ? Array.newInstance(
				convertStatesMap.get(rawAutomata.mStates[0]).getClass(),
				length) : new Object[0]);
		length = ((Object[]) rawAutomata.mTransitionFunction).length;
		a.mTransitionFunction = (Transition<TStateTo, TSymbol>[]) Array.newInstance(Transition.class, length);
		
		for (int i = 0; i < a.mStates.length; i++)
			a.mStates[i] = convertStatesMap.get(rawAutomata.mStates[i]);
		for (int i = 0; i < a.mTransitionFunction.length; i++) {
			a.mTransitionFunction[i] = new Transition<TStateTo, TSymbol>(
					convertStatesMap.get(rawAutomata.mTransitionFunction[i].stateIn), // stateIn
					rawAutomata.mTransitionFunction[i].symbol, // symbol
					convertStatesMap.get(rawAutomata.mTransitionFunction[i].stateOut) // stateOut
			);
		}
		
		return a;
	}
	
	public Automata<String, TSymbol> nfa2dfa(String stateSeparator) {
		return nfa2dfa(new NFAStateConverter<TState, String>() {
			@Override
			public String convertNFAState(HashSet<TState> state) {
				StringBuilder str = new StringBuilder();
				boolean first = true;
				for (TState s : state) {
					if (!first)
						str.append(stateSeparator);
					str.append(s);
					first = false;
				}
				return str.toString();
			}
		});
	}
	
	public interface NFAStateConverter<TStateFrom, TStateTo> {
		TStateTo convertNFAState(HashSet<TStateFrom> state);
	}
}