--------------------------------------------------
polarity-propagation-0.5.0.jar - Java Application
-------------------------------------------------

Table of contents:
------------------
1. Introduction
2. Software Requirements
3. How to run the application
4. Description of the provided files

1. INTRODUCTION
---------------
	This is a Java implementation of the algorithm described in:
	Santos, A. P., Oliveira, H. G., Ramos, C., & Marques, N. C. (2012). 
	A Bootstrapping Algorithm for Learning the Polarity of Words. In Proceedings 
	of 10th International Conference - Computational Processing of the Portuguese 
	Language (PROPOR 2012), volume 7243 of LNCS, pp. 229-234. Coimbra, Portugal. 
	Springer.
 	
	Running the PolarityPropagation.jar application in the command line, will 
	produce a list of words classified as «positive», «negative», and «neutral».
	The algorithm is language independent. 
	The implementation is provided with two examples. One for english and other 
	for Portugese.
		
2. SOFTWARE REQUIREMENTS
-------------------------
- Java JRE (or JDK) version 1.6 or higher
		
3. HOW TO RUN THE APPLICATION
-----------------------------
	To run the "data/example01" example, just type:
	"java -jar polarity-propagation-0.5.0" (without quotes) at the command line.
	
	DETAILS:
		1. Open a command line.
		2. Change to the same directory as PolarityPropagation.jar 
		(e.g. cd PolarityPropagationImplementation).
		3. Type the following command:
			java -jar polarity-propagation-0.5.0.jar

	OUTPUT:
		- A list of words classified as posite, negative, neutral, and ambiguous.
		TIP: If this list contains words with a polarity that you disagree, try 
		to insert them in the seed words file (reed the next description).

	The above command:
		1. Reads the "config.properties" file and loads its configurations.
		Two important configurations are the "fileAdjacencyList" and "seedWordsFile".
			- The "fileAdjacencyList" configuration has the name of the file 
			containing a graph of synonymous and antonymous words. 
			- The "seedWordsFile" configuration has the name of the file
			containing seed words. This means, a list of words manually labeled 
			as positive, negative and neutral.
		2. Outputs a list of words with their polarities according to the 
		configuration file. 
		
4. DESCRIPTION OF THE PROVIDED FILES 
-------------------------------------
	readme.txt 
		The current file.
		
	config.properties (input file) 
		Configuration file used by PolarityPropagation.jar. 
	
	PolarityPropagation.jar
		The java implementation of the polarity propagation algorithm described 
		in the paper above mentioned.
		
	data
		This directory has two example files. Each example is numbered and has
		three files:
		
		- example01-tiny-graph-english.txt and example02-graph-of-synonymou-portuguese.txt 
		These INPUT files are examples of files containing a graph. 
		The	example01-tiny-graph-english.txt file is a tiny graph of English 
		synonymous and anonymous words. 
		The example02-graph-of-synonymou-portuguese.txt is a graph of Portuguese 
		synonymous words. This file is part of PAPEL V.2.0 lexical resource for Portuguese. 
		PAPEL contains several semantic relations between words, however for 
		polarity propagation we just used the synonymous relation.
		For more information about PAPEL, see: http://www.linguateca.pt/ or 
		directly at http://www.linguateca.pt/PAPEL/papel.html
		
		- example01-seed-words-english.txt and example02-seed-words-portuguese.txt 
		These are INPUT FILES. They are list of words manually classified as 
		positive(1), negative(-1), and neutral(0).
		
		- example01-list-of-polarities-OUTPUT-english.csv and 
		example02-list-of-polarities-OUTPUT-portuguese.
		These are OUTPUT files. They are list of words classified as positive(+), 
		negative(-), neutral(0), and ambiguous(A), by the "machine"/algorithm.

3. Read me file version 
-----------------------		
Created at : April 21, 2012
Last update: Wed May 28 17:20:52 2014		