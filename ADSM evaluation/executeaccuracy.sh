#!/bin/bash
java -ea -Xmx12G -Djava.library.path=./locallib/lin64 -XX:+UseCompressedOops -jar testAccuracy.jar $1 $2 $3| grep -v "INITIALIZEDbfp_etaPFI"
