#!/usr/bin/env bash

##
## Configuration
##

# Requires: Defined GRAAL_HOME to point at the root of your GRAALVM folder.
# Requires: Leiningen


# Set GRAAL VM to JAVA_HOME
export JAVA_HOME=$GRAAL_HOME

# Add GRAAL VM to the PATH, should include native-image
export PATH=$GRAAL_HOME/bin:$PATH

# Retrieve the current eden version
echo "Getting Project Version..."
EDEN_VERSION=`lein project-version`
echo "Project Version: " $EDEN_VERSION
echo ""

echo "Generating Uberjar..."
lein uberjar
echo ""

echo "Building Native Image..."
native-image -jar target/eden-$EDEN_VERSION-standalone.jar \
             -H:Name="eden-${EDEN_VERSION}" \
             --initialize-at-build-time \
	     --no-server \
             --no-fallback \
             --report-unsupported-elements-at-runtime \
	     --enable-all-security-services \
	     -H:+ReportExceptionStackTraces \
             -H:ReflectionConfigurationFiles=ReflectionConfig.json
echo ""

echo "Post Configuration..."
mkdir -p bin
chmod 744 eden-${EDEN_VERSION}
mv eden-$EDEN_VERSION ./bin/
rm -f ./bin/eden
ln -s ./bin/eden-$EDEN_VERSION ./bin/eden
echo ""

echo "Built executable can be found at ./bin/eden-${EDEN_VERSION}"
