# Makefile for generating dpkgs for the native-image compiled 'eden' executable
# Requires: leiningen
# Optional: eden

EMAIL := benzaporzan@gmail.com

GIT_ROOT_DIR := $(shell git rev-parse --show-toplevel)
PROJECT_ROOT_DIR := $(GIT_ROOT_DIR)
DIST_DIR := $(PROJECT_ROOT_DIR)/dist

OS := $(shell uname)
ARCH := $(shell uname -i)

EDEN_VERSION := $(shell lein project-version)
EDEN_EXE_NAME := eden-$(EDEN_VERSION)
TAR_NAME := $(EDEN_EXE_NAME)-$(ARCH).tar.gz

TMAKE_DIR := $(DIST_DIR)/tar/$(EDEN_EXE_NAME)
PROJ_EDEN_EXE := $(PROJECT_ROOT_DIR)/bin/$(EDEN_EXE_NAME)
TAR_EDEN_EXE := $(TMAKE_DIR)/usr/bin/$(EDEN_EXE_NAME)


# default
all: tar


init:
	mkdir -p $(TMAKE_DIR)
	cp $(PROJECT_ROOT_DIR)/LICENSE $(TMAKE_DIR)/LICENSE
	cp $(PROJECT_ROOT_DIR)/readme.org $(TMAKE_DIR)/readme.org
	cp $(PROJECT_ROOT_DIR)/changelog.org $(TMAKE_DIR)/changelog.org

	cp tar_makefile.template $(TMAKE_DIR)/Makefile
	sed -i "s/EDEN_EXE_NAME/$(EDEN_EXE_NAME)/g" $(TMAKE_DIR)/Makefile


$(PROJ_EDEN_EXE):
	echo "Building Native Image..."
	cd $(PROJECT_ROOT_DIR) && ./build-native.sh


$(TAR_EDEN_EXE): $(PROJ_EDEN_EXE)
	mkdir -p $(TMAKE_DIR)/usr/bin
	cp $(PROJ_EDEN_EXE) $(TAR_EDEN_EXE)
	chmod 755 $(TAR_EDEN_EXE)


prepare: $(TAR_EDEN_EXE)


build:
	cd $(DIST_DIR)/tar && \
           tar -czvf $(TAR_NAME) $(EDEN_EXE_NAME) && \
           mv $(TAR_NAME) ..


tar: clean init prepare build


clean:
	rm -rf $(TMAKE_DIR)
	rm -f $(DIST_DIR)/$(TAR_NAME)
