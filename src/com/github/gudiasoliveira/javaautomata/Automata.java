package com.github.gudiasoliveira.javaautomata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		public Builder<TState, TSymbol> setInitialState(TState state) {
			mInitialState = state;
			return this;
		}
		
		public Builder<TState, TSymbol> transitionFunction(TState fromState, TSymbol symbol, TState toState) {
			mTransitionFunction.add(new Transition<>(fromState, symbol, toState));
			return this;
		}
		
		public Automata<TState, TSymbol> build() {
			Automata<TState, TSymbol> automata = new Automata<>();
			
			automata.mStates = (TState[]) mStates.toArray();			
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
		TState[] states = (TState[]) new Object[mStates.length];
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
		TSymbol[] symbols = (TSymbol[]) new Object[mSymbols.length];
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
		TState[] finalStates = (TState[]) new Object[mFinalStateIndexes.length];
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
}