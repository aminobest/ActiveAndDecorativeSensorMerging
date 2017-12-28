#!/bin/sh

###
## ProM specific
###
PROGRAM=ProM66
CP=./ProMwithADSMRunnable.jar
LIBDIR=./locallib/lin64
MAIN=org.processmining.contexts.uitopia.UI
###
## Main program
###

add() {
	CP=${CP}:$1
}

for lib in $LIBDIR/*.jar
do
	add $lib
done

java -version
java -classpath ${CP} -da -Djava.library.path=${LIBDIR} -Djava.util.Arrays.useLegacyMergeSort=true -Xmx4G -XX:MaxPermSize=256m -DsuppressSwingDropSupport=false ${MAIN}
