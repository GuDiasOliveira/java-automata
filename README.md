# Automata Java data structure

## How the program works

The runnable is in <code>Program</code> class. It reads an automata input, then outpur a *dot* text data format for rendering graphs, in a program like [Graphviz](https://www.graphviz.org/).

## Program's data input format

```
states:
[state label] [S|F|SF|FS]
...

transitions:
[state from] [symbol] [state to]
...
```

The input must start with a <code>states:</code> line to indicate the automata states declaration.

Then a list of states, each one (separated by lines) named with a label, delimited by a whitespace. After the white space there can be a <code>S</code> to indicate the start state, a <code>F</code> to flag a final state (accepter), or even <code>SF</code> or <code>FS</code> to flag that the state is both the start and a final.

When found <code>transitions:</code> line, then it starts the transitions declarations, separated by lines until the end of input, to define the transition function of the automata.

\* You don't need the declare the symbols, like the states.

The first argument (until white space) is the current state it is in, the second one is the symbol label (delimited by whitespace) that is the path to go to the destination state (third argument).

## Tip to render the automata graphically in Linux

1. Install Graphviz:
```bash
sudo apt-get install graphviz
```

2. Install Xdot, a program to view dot graphs:
```bash
sudo apt-get install xdot
```

3. Run the program, then pipe the input to Xdot.

Export this project in a jar file, then run: (where <code>your_exported_program.jar</code> is the file name or path to you exported jar)
```bash
java -jar your_exported_program.jar < your_input_file.txt | xdot -
```