#!/bin/bash

JBOSS_HOME=/home/kalle/tmp/jboss/jboss-eap-6.0
DOMAINCONFIG=$JBOSS_HOME/domain/configuration

cp domain.xml $DOMAINCONFIG
cp host.xml $DOMAINCONFIG
