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

echo "Running build of custom Arquillian Cube necessary for OCP 4.6+ "
git clone https://github.com/penehyba/arquillian-cube.git
cd arquillian-cube
git checkout ocp46
mvn -B clean install -q -DskipTests -DskipITs -Denforcer.skip=true -Dmaven.javadoc.skip=true
cd ..
ls ~/.m2/repository/org/arquillian/cube/arquillian-cube-bom/
sed -i -e 's|<version.org.arquillian.cube>.*</version.org.arquillian.cube>|<version.org.arquillian.cube>1.18.X</version.org.arquillian.cube>|' pom.xml

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

