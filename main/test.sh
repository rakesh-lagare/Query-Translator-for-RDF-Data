ARQ_PATH="/home/pyati/EIS/apache-jena-3.4.0/bin"

OLD_PATH_D1="./data/d1/fullOrg.n3"
NEW_PATH_D1="./data/d1/fullFact.n3"
OLD_QUERIES_PATH="./queries/old"
NEW_QUERIES_PATH="./transOutputFiles"
OUTPUT_DIR_OLD_D1="./output/D1/old"
OUTPUT_DIR_FACT_D1="./output/D1/factorized"
OLD_PATH_D2="./data/d2/fullOrg.n3"
NEW_PATH_D2="./data/d2/fullFact.n3"
OUTPUT_DIR_OLD_D2="./output/D2/old"
OUTPUT_DIR_FACT_D2="./output/D2/factorized"
OLD_PATH_D3="./data/d3/fullOrg.n3"
NEW_PATH_D3="./data/d3/fullFact.n3"
OUTPUT_DIR_OLD_D3="./output/D3/old"
OUTPUT_DIR_FACT_D3="./output/D3/factorized"

echo "##-------------------------------------------------------------------------------------------##"
echo "			Removing the old queries and output files"
echo "##-------------------------------------------------------------------------------------------##"
rm -rf /home/pyati/EIS/rework/transOutputFiles/*
echo
echo
echo "##-------------------------------------------------------------------------------------------##"
echo "				Query Translation phase began:"
echo "##-------------------------------------------------------------------------------------------##"
echo

for query in /home/pyati/EIS/rework/queries/old/*
do
	java -jar ./translator.jar $query > /dev/null 2>&1 
	echo "Translated query: ${query}"
done


echo
echo "##-------------------------------------------------------------------------------------------##"
echo "			Running the queries against the DATASET-1"
echo "##-------------------------------------------------------------------------------------------##"
echo

for query in ${OLD_QUERIES_PATH}/*
do
	${ARQ_PATH}/arq --data ${OLD_PATH_D1} --query ${query} >${OUTPUT_DIR_OLD_D1}/`basename $query`
	echo "DONE: Run original query: ${query}"
done
echo
for query in ${NEW_QUERIES_PATH}/*
do
	${ARQ_PATH}/arq --data ${NEW_PATH_D1} --query ${query} >${OUTPUT_DIR_FACT_D1}/`basename $query`
	echo "DONE: Run translated query: ${query}"
done


echo
echo "##-------------------------------------------------------------------------------------------##"
echo "			Running the queries against the DATASET-2"
echo "##-------------------------------------------------------------------------------------------##"
echo
for query in ${OLD_QUERIES_PATH}/*
do
	${ARQ_PATH}/arq --data ${OLD_PATH_D2} --query ${query} >${OUTPUT_DIR_OLD_D2}/`basename $query`
	echo "DONE: Run original query: ${query}"
done
echo
for query in ${NEW_QUERIES_PATH}/*
do
	${ARQ_PATH}/arq --data ${NEW_PATH_D2} --query ${query} >${OUTPUT_DIR_FACT_D2}/`basename $query`
	echo "DONE: Run translated query: ${query}"
done



echo
echo "##-------------------------------------------------------------------------------------------##"
echo "			Running the queries against the DATASET-3"
echo "##-------------------------------------------------------------------------------------------##"
echo
for query in ${OLD_QUERIES_PATH}/*
do
	${ARQ_PATH}/arq --data ${OLD_PATH_D3} --query ${query} >${OUTPUT_DIR_OLD_D3}/`basename $query`
	echo "DONE: Run original query: ${query}"
done
echo
for query in ${NEW_QUERIES_PATH}/*
do
	${ARQ_PATH}/arq --data ${NEW_PATH_D3} --query ${query} >${OUTPUT_DIR_FACT_D3}/`basename $query`
	echo "DONE: Run translated query: ${query}"
done
echo
echo


echo "##-------------------------------------------------------------------------------------------##"
echo "					Comparing the results"
echo "##-------------------------------------------------------------------------------------------##"
echo
for file in "q1" "q3" "q4" "q6" "q8" "q9" "q10" "q11"
do
	sort ${OUTPUT_DIR_OLD_D1}/${file} | uniq > f1
	sort ${OUTPUT_DIR_FACT_D1}/${file}| uniq > f2
	echo
	cmp f1 f2 >/dev/null
	if [ $? -eq 0 ]
	then
		echo "Comparing ${OUTPUT_DIR_OLD_D1}/${file} with ${OUTPUT_DIR_FACT_D1}/${file} :: RESULTS OK"
	else
		echo "Comparing ${OUTPUT_DIR_OLD_D1}/${file} with ${OUTPUT_DIR_FACT_D1}/${file} :: RESULTS NOT-OK"
	fi
done
echo "##-------------------------------------------------------------------------------------------##"
echo
for file in "q1" "q3" "q4" "q6" "q8" "q9" "q10" "q11"
do
	sort ${OUTPUT_DIR_OLD_D2}/${file} | uniq > f1
	sort ${OUTPUT_DIR_FACT_D2}/${file}| uniq > f2
	echo
	cmp f1 f2 >/dev/null
	if [ $? -eq 0 ]
	then
		echo "Comparing ${OUTPUT_DIR_OLD_D2}/${file} with ${OUTPUT_DIR_FACT_D2}/${file} :: RESULTS OK"
	else
		echo "Comparing ${OUTPUT_DIR_OLD_D2}/${file} with ${OUTPUT_DIR_FACT_D2}/${file} :: RESULTS NOT-OK"
	fi
done
echo "##-------------------------------------------------------------------------------------------##"
echo
for file in "q1" "q3" "q4" "q6" "q8" "q9" "q10" "q11"
do
	sort ${OUTPUT_DIR_OLD_D3}/${file} | uniq > f1
	sort ${OUTPUT_DIR_FACT_D3}/${file}| uniq > f2
	echo
	cmp f1 f2 >/dev/null
	if [ $? -eq 0 ]
	then
		echo "Comparing ${OUTPUT_DIR_OLD_D3}/${file} with ${OUTPUT_DIR_FACT_D3}/${file} :: RESULTS OK"
	else
		echo "Comparing ${OUTPUT_DIR_OLD_D3}/${file} with ${OUTPUT_DIR_FACT_D3}/${file} :: RESULTS NOT-OK"
	fi
done
