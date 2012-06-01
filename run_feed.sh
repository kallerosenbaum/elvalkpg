#!/bin/bash

if [ "$#" != "1" ] ; then
  echo "expected one argument"
  exit 1
fi

bindaddr=192.168.111.206

java -cp target/classes:target/dependency/* -Djgroups.bind_addr=$bindaddr se.elva.lkpg.twitterdemo.TwitterDataSource $@
