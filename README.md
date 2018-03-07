# Automata Java data structure

## How the program works

The runnable is in <code>Program</code> class. It reads an automata input, then outputs a *dot* text data format for rendering graphs, in a program like [Graphviz](https://www.graphviz.org/); and some other options.

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

## Program options

Run this command to display the help of the options:
```bash
java -jar your_exported_program.jar --help
```

### Example
![](ex.png)

Input

```
states:
A
B S
C F
D F

 # fdsnfjds
transitions:
A x A
A y B
A z B
B w C
C x A
C w D
```
\* Lines with <code>#</code> are comments, then ignored.

Running:
```bash
java -jar your_exported_program.jar --transitions w x x z w w < your_input_file.txt
```
Will output:
```bash
B
C
A
A
B
C
D
```

Running:
```bash
java -jar your_exported_program.jar --transition w x x z w w < your_input_file.txt
```
Will output:
```bash
D
```

Running:
```bash
java -jar your_exported_program.jar --accept w x x z < ex.txt && echo "Accepted!" || echo "Rejected"
```
Will output:
```bash
Rejected
```

Running:
```bash
java -jar your_exported_program.jar --accept w x x z w w < ex.txt && echo "Accepted!" || echo "Rejected"
```
Will output:
```bash
Accepted!
```

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
java -jar your_exported_program.jar --dot-graph < your_input_file.txt | xdot -
```

## Working with non-deterministic finite automatas (NFA)

You can input a NFA to output a converted to *deterministic finite automata* (DFA).

Command:
```bash
java -jar automata.jar --nfa2dfa < input_nfa.txt
```

Input:
```
states:
q0 S
q1 
q2 F

transitions:
q0 a q0
q0 b q0
q0 a q1
q1 b q2
```

Output:
```
states:
q0 S
q1,q0 
q2,q0 F

transitions:
q0 a q1,q0
q0 b q0
q1,q0 a q1,q0
q1,q0 b q2,q0
q2,q0 a q1,q0
q2,q0 b q0
```

You can view the converted automata graphically:
```bash
java -jar automata.jar --nfa2dfa < input_nfa.txt | java -jar automata.jar --dot-graph | xdot -
```
![](ex2.png) 

You can specify a custom separator caracter, for example:
```bash
java -jar automata.jar --nfa2dfa '-' < input_nfa.txt
```
```
states:
q0 S
q1-q0 
q2-q0 F

transitions:
q0 a q1-q0
q0 b q0
q1-q0 a q1-q0
q1-q0 b q2-q0
q2-q0 a q1-q0
q2-q0 b q0
```
Or empty separator:
```bash
java -jar automata.jar --nfa2dfa '' < input_nfa.txt
```
```
states:
q0 S
q1q0 
q2q0 F

transitions:
q0 a q1q0
q0 b q0
q1q0 a q1q0
q1q0 b q2q0
q2q0 a q1q0
q2q0 b q0
```

# Glossary
**NFA** - Non-deterministic finite automata
**DFA** - Deterministic finite automata