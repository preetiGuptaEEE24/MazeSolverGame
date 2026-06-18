#!/bin/bash
echo "Compiling..."
mkdir -p out
javac -d out src/maze/*.java
echo "Running..."
java -cp out maze.Main
