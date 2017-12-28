#!/bin/bash
java -ea -Xmx12G -Djava.library.path=./locallib/lin64 -XX:+UseCompressedOops -jar caseid.jar $1 $2 $3 $4 | grep -v "INITIALIZEDbfp_etaPFI"
