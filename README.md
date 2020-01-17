# Query-Translator-for-RDF-Data
As part of Enterprise Information Lab

# Prerequisits
Install Apache jena ARQ

# How to run/test

1. Down load "main" folder to your unix machine
2. Open the script: test.sh
3. Update ARQ_PATH variable with your local path where jena is installed.
4. Run the script as ./test.sh
5. Observe the console messages

# How it runs

1. translator.jar will take the original queries from different datasets.
2. It then trnslates them and stores to transOutputFiles directory.
3. script takes the old and translated queries and runs on original and factorized dataset respectively.
4. script will then comapre the results produced.

# exceptional cases in results:
The queries whic contains avg(x) will differ in results as the redundant data will be removed in factorized dataset ao the change in the average value.
