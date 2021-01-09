# !/bin/bash

LOCAL_MAVEN_REPO='../repository'

if [ ! -d "${LOCAL_MAVEN_REPO}" ]; then
    echo "Maven repository directory ${LOCAL_MAVEN_REPO} DOES NOT exists." 
    exit 1
fi

PROJECT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
PROJECT_NAME=$(mvn -q --non-recursive exec:exec -Dexec.executable=echo -Dexec.args='${project.artifactId}')

mvn -Dmaven.test.skip=true -DaltDeploymentRepository=snapshot::default::file:${LOCAL_MAVEN_REPO}/maven/snapshots clean deploy

if [ $? -ne 0 ]; then
    echo "ERROR: Maven deploy failed"
    exit 1
fi

cd ${LOCAL_MAVEN_REPO}

git status
git add .
git status
git commit -m "version ${PROJECT_VERSION} of ${PROJECT_NAME}"
if [ $? -ne 0 ]; then
    echo "ERROR: git commit failed"
    exit 1
fi

git pull
if [ $? -ne 0 ]; then
    echo "ERROR: git pull failed"
    exit 1
fi

git push origin master
if [ $? -ne 0 ]; then
    echo "ERROR: git push failed"
    exit 1
fi
