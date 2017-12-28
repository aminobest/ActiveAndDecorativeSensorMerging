#!/bin/bash
java -ea -Xmx12G -Djava.library.path=./locallib/lin64 -XX:+UseCompressedOops -jar simulator.jar $1 $2 $3 $4 $5 $6| grep -v "INITIALIZEDbfp_etaPFI"
