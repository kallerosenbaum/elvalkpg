#!/bin/bash

if [ "$#" != "1" ] ; then
  echo "expected one argument"
  exit 1
fi

mvn exec:java -Dexec.mainClass="se.elva.lkpg.twitterdemo.TwitterDataSource" -Dexec.args="$@"
