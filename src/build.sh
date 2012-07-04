#!/usr/bin/env bash

PROJECTS="CarneadesEngine CarneadesExamples CarneadesWebService PolicyModellingTool"
RELEASE_CMD=""

function show_usage {
    echo "Usage: build.sh [--release|--dev|--release-without-toolbox]";
}

function clean {
    for project in $PROJECTS; do
        echo "Cleaning $project"
        cd $project
        lein clean
        cd -
    done
}

function exec_confscripts {
    $RELEASE_CMD
}

function build_install_jar_war {
    echo -e "\n======================================== Building CarneadesEngine\n"
    cd CarneadesEngine
    lein deps
    lein install
    cd -

    echo -e "\n======================================== Building CarneadesWebService\n"
    cd CarneadesWebService
    lein deps
    lein install
    lein ring uberwar
    cd -

    echo -e "\n======================================== Building PolicyModellingTool\n"
    cd PolicyModellingTool
    exec_confscripts;
    lein deps
    lein ring uberwar
    cd -
}

function show_instructions {
    echo -e "\nBuild is finished. \n"
    echo -e "To run the Aston example:\n$ cd CarneadesExamples && lein run -m carneades.examples.aston\n"
    echo -e "To run the Policy Modelling tool:\n$ cd PolicyModellingTool && lein ring server 8080"
    echo -e "Then point your browser at http://localhost:8080/policymodellingtool/#/introduction"
}
    
function show_notification {
    if [ -x /usr/bin/notify-send ]; then
        notify-send "Build.sh" "Build is finished!"
    fi
}

case $1 in
    "--release-without-toolbox") RELEASE_CMD="./scripts/make-release-without-toolbox.sh";;
    "--release") RELEASE_CMD="./scripts/make-release.sh";;
    "--dev") RELEASE_CMD="./scripts/make-dev.sh";;
    *) show_usage; exit;;
esac


clean;
build_install_jar_war;
show_instructions;
show_notification;
