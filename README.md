

The Active and Decorative Sensor Mapping (ADSM) plugin is part of the thesis project entitled ”Exploring Process Mining in IoT Environments”, written by Amine Abbad Andaloussi At DTU Compute.

1.To import this project to eclipse the following dependencies are required:

http://www.apache.org/dist/ant/ivyde/updatesite/
https://dl.bintray.com/subclipse/releases/subclipse/latest/

2. A Jar Runnable version of ProM with ADSM plugin can be executed from runProMwithADSM.sh

3. The folder "ADSM evaluation/" contains the material used to evaluate the ADSM approach. 

3.1 The folders "ADSM evaluation/20cases", "ADSM evaluation/100cases", "ADSM evaluation/1000cases" contain the data sets generated
by the BPS sinulator (available on: https://github.com/DTU-SE/AmaSmart)

3.2 Use the Shell Script File "ADSM evaluation/evaluation.sh" to generate new datasets, to apply the ADSM plugin, and to evaluate the merged event log. (please Remember to change the folder name "20casesR" to a new name)

3.3 The folder "ADSM evaluation/run4_100cases_TFIDF_mergedlog" contains an XES file generated from the dataset in "ADSM evaluation/100cases/100casesR4/" using TF-IDF similarity function.
The XES file can be imported to ProM and used by other ProM plugins.

4. The implementation of the ICI plugin is available in the folder "src/iciPlugin/"

5. The folder "ADSM ICI evaluation/" contains the material used to evaluate the ICI approach.

5.1 The folder "ICI evaluation/eventsLogsWithTop1000events/" contains the event logs used to evaluate the ICI appraoch.

5.2 The Folder "/ICI evaluation/executabe JAR/" contains a Runnable Jar file of the ICI plugin used for the evaluation.
The jar can be executed from the Shell Script File "ICI evaluation/executabe JAR/executeCaseId.sh".
The folder also contains an example event log "ICI evaluation/executabe JAR/BPI_Challenge_2012_1.csv", a cluster Job file "ICI evaluation/executabe JAR/exampleJob.job" allowing to submit the job to a cluster.
