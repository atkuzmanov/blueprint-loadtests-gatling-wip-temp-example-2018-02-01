#!/bin/sh
set -e

if [ -z "$1" ];
then
  echo "You must provide a simulation to run"
  exit 1
fi

filePath="user-files/simulations/*/$1*"

function exit_on_error {
  echo $1
  exit 1
}

ls $filePath 1>/dev/null 2>/dev/null || exit_on_error "The simulation pattern provided did not match any simulations"

# list files that match simulation pattern
# remove file path prefixes
# remove file extensions
# replace / with . to give class names
# add examplePackageName. to give complete class names
tests=`ls $filePath | \
  sed "s/user-files\/simulations\///" | \
  sed "s/\.scala//" | \
  sed "s/\//\./g" | \
  sed "s/^/examplePackageName\./"`

echo "Tests to be run:"
echo "$tests" | xargs -L1 echo

# append each class name to the gatling script command
# this produces a string with one or more gatling commands to run
commands=`echo $tests | \
  tr ' ' '\n' | \
  sed "s/^/\.\/bin\/gatling\.sh -m -s /g" | \
  sed "s/$/;/g"`

eval $commands
