for VAR in 0
do
mkdir 20casesR$VAR
./executesimulator.sh -args 20casesR$VAR/ 20 10 5 0
./executedatamerger.sh 0.20 JA 20casesR$VAR/ 20casesR$VAR/mergedlog1.csv
./executedatamerger.sh 0.20 simTF 20casesR$VAR/ 20casesR$VAR/mergedlog2.csv
./executeaccuracy.sh 20casesR$VAR/mergedlog1.csv.csv 20casesR$VAR/ JA
./executeaccuracy.sh 20casesR$VAR/mergedlog2.csv.csv 20casesR$VAR/ simTF	
done

