#!/bin/bash

bindaddr=192.168.111.206

java -cp target/classes:target/dependency/* -Djgroups.bind_addr=$bindaddr se.elva.lkpg.twitterdemo.InfinispanTest
