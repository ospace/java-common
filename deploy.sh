# !/bin/bash

LOCAL_MAVEN_REPO='../repository'

PROJECT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
PROJECT_NAME=$(mvn -q --non-recursive exec:exec -Dexec.executable=echo -Dexec.args='${project.artifactId}')

mvn -Dmaven.test.skip=true -DaltDeploymentRepository=snapshot::default::file:${LOCAL_MAVEN_REPO}/maven/snapshots clean deploy

cd ${LOCAL_MAVEN_REPO}

exit 0
git status
git add .
git status
git commit -m "version ${PROJECT_VERSION} of ${PROJECT_NAME}"
git push origin master
