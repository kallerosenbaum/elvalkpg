#!/bin/bash

if [ "$#" != "1" ] ; then
  echo "expected one argument"
  exit 1
fi

java -cp target/classes:target/dependency/* se.elva.lkpg.twitterdemo.TwitterDataSource $@
