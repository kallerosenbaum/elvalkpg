#!/bin/bash

bindaddr=192.168.111.206

#sudo ifconfig eth0:$1 $bindaddr

java -cp target/classes:target/dependency/* -Djgroups.bind_addr=$bindaddr se.elva.lkpg.twitterdemo.SearchClient
