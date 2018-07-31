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

RPM_NAME := $(EDEN_EXE_NAME)-$(ARCH)
RPM_VERSION := 1

RMAKE_DIR := $(DIST_DIR)/rpmpkg/$(RPM_NAME)

TAR_NAME := $(EDEN_EXE_NAME)-$(ARCH).tar.gz
TAR_PATH := $(DIST_DIR)/$(TAR_NAME)
RPKG_TAR := $(RMAKE_DIR)/SOURCES/$(TAR_NAME)


# default
all: rpm


init:
	mkdir -p $(RMAKE_DIR)/{RPMS,SRPMS,UILD,SOURCES,SPECS,tmp}

	cp eden.spec.template $(RMAKE_DIR)/SPECS/eden.spec
	sed -i "s/EDEN_EXE_NAME/$(EDEN_EXE_NAME)/g" $(RMAKE_DIR)/SPECS/eden.spec
	sed -i 's|RMAKE_DIR|'$(RMAKE_DIR)'|g' $(RMAKE_DIR)/SPECS/eden.spec
	sed -i "s/EDEN_VERSION/$(EDEN_VERSION)/g" $(RMAKE_DIR)/SPECS/eden.spec
	sed -i "s/RPM_VERSION/$(RPM_VERSION)/g" $(RMAKE_DIR)/SPECS/eden.spec
	sed -i "s/ARCH/$(ARCH)/g" $(RMAKE_DIR)/SPECS/eden.spec


$(TAR_PATH):
	make -C $(PROJECT_ROOT_DIR)/dist_config/tar/ tar


$(RPKG_TAR): $(TAR_PATH)
	cp $(TAR_PATH) $(RMAKE_DIR)/SOURCES/


prepare: $(RPKG_TAR)
	find $(RMAKE_DIR) -type d | xargs chmod 755


build:
	rpmbuild -ba $(RMAKE_DIR)/SPECS/eden.spec --define '_topdir $(RMAKE_DIR)'
	cp -rf $(RMAKE_DIR)/RPMS/$(ARCH)/*.rpm $(DIST_DIR)/


clean:
	rm -rf $(RMAKE_DIR)


rpm: clean init prepare build

