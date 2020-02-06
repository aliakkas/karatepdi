#! /bin/sh

echo "***" run feature file and generate cucumber report "***"
if [ $# -eq 0 ]
then
  echo "Provide command to run: i.e. name=run-acceptancetest.feature"
  exit
fi

curl -i http://127.0.0.1:9999/karate/report?"$@" -u admin:hello123
