#!/bin/bash

# https://repo.maven.apache.org/maven2/io/thorntail/bom/
THORNTAIL_VERSION=2.7.0.Final
OUTPUT_DIRECTORY=./failsafe-reports
POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -v|--version.io.thorntail)
    THORNTAIL_VERSION="$2"
    shift
    shift
    ;;
    -o|--output)
    OUTPUT_DIRECTORY="$2"
    shift
    shift
    ;;
    -h|--help)
    echo "This is script for running Thorntail OCP smoke tests"
    echo "-v|--version.io.thorntail This parameter specify the Thorntail version. The default is ${THORNTAIL_VERSION}"
    echo "-o|--output This parameter specify output directory. The default is ${OUTPUT_DIRECTORY}"
    echo "-h|--help This parameter print this message"
    exit 1;
    shift
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

echo "Running smoke test for Thorntail ${THORNTAIL_VERSION}."

PROJECTS=common,http,topology/tests

mvn clean verify -Dversion.io.thorntail=$THORNTAIL_VERSION -pl $PROJECTS -am

mkdir -p $OUTPUT_DIRECTORY

for PROJECT in ${PROJECTS//","/" "}; do
  if [ $PROJECT = "common" ]; then
    continue
  fi
  for FILE in ./$PROJECT/target/failsafe-reports/*.xml; do
    FILENAME=$(basename $FILE)
    echo $FILENAME
    cp $FILE ./$OUTPUT_DIRECTORY/$FILENAME
  done
done

