#!/bin/bash

DIR=`dirname $0`

JBOSS_HOME=/home/kalle/tmp/jboss/jboss-eap-6.0
DOMAINCONFIG="$JBOSS_HOME/domain/configuration"

cp "$DIR/domain.xml" "$DOMAINCONFIG"
cp "$DIR/host.xml" "$DOMAINCONFIG"
