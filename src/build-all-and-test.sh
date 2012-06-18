#!/usr/bin/env bash

# build one standalone and one toolbox WAR and run tests
sh -c "./build.sh --release" && \
    cd PolicyModellingTool && \
    mv policymodellingtool-1.0.0-SNAPSHOT-standalone.war policymodellingtool-1.0.0-SNAPSHOT-toolbox.war && \
    sh -c "./scripts/make-realese-without-toolbox.sh" && \
    lein ring uberwar && \
    mv policymodellingtool-1.0.0-SNAPSHOT-standalone.war policymodellingtool-1.0.0-SNAPSHOT-css.war
    cd ../CarneadesEngine && lein test && \
    cd ../CarneadesWebService && \
    lein test

