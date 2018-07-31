# Makefile for generating dpkgs for the native-image compiled 'eden' executable
# Requires: leiningen
# Optional: eden

EMAIL := benzaporzan@gmail.com

GIT_ROOT_DIR := $(shell git rev-parse --show-toplevel)
PROJECT_ROOT_DIR := $(GIT_ROOT_DIR)
DIST_DIR := $(PROJECT_ROOT_DIR)/dist

OS := $(shell uname)
#ARCH := $(shell uname -i)
ARCH := $(shell dpkg --print-architecture)

EDEN_VERSION := $(shell lein project-version)
EDEN_EXE_NAME := eden-$(EDEN_VERSION)

DEB_NAME := $(EDEN_EXE_NAME)-$(ARCH)
DEB_VERSION := 1

DMAKE_DIR := $(DIST_DIR)/dpkg/$(DEB_NAME)
PROJ_EDEN_EXE := $(PROJECT_ROOT_DIR)/bin/$(EDEN_EXE_NAME)
DPKG_EDEN_EXE := $(DMAKE_DIR)/usr/bin/$(EDEN_EXE_NAME)


# default
all: dpkg


init:
	mkdir -p $(DMAKE_DIR)/DEBIAN
	touch $(DMAKE_DIR)/DEBIAN/control
	echo "Package: eden" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Version: $(EDEN_VERSION)-$(DEB_VERSION)" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Section: interpreters" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Priority: optional" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Homepage: http://github.com/benzap/eden" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Architecture: $(ARCH)" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Maintainer: Benjamin Zaporzan <benzaporzan@gmail.com>" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Description: Interpreter for the eden language" >> $(DMAKE_DIR)/DEBIAN/control

	cp prerm.template $(DMAKE_DIR)/DEBIAN/prerm
	chmod 775 $(DMAKE_DIR)/DEBIAN/prerm
	sed -i "s/EDEN_EXE_NAME/$(EDEN_EXE_NAME)/g" $(DMAKE_DIR)/DEBIAN/prerm

	cp postinst.template $(DMAKE_DIR)/DEBIAN/postinst
	chmod 775 $(DMAKE_DIR)/DEBIAN/postinst
	sed -i "s/EDEN_EXE_NAME/$(EDEN_EXE_NAME)/g" $(DMAKE_DIR)/DEBIAN/postinst

	cp $(PROJECT_ROOT_DIR)/LICENSE $(DMAKE_DIR)/DEBIAN/license

$(PROJ_EDEN_EXE):
	echo "Building Native Image..."
	cd $(PROJECT_ROOT_DIR) && ./build-native.sh


$(DPKG_EDEN_EXE): $(PROJ_EDEN_EXE)
	mkdir -p $(DMAKE_DIR)/usr/bin
	cp $(PROJ_EDEN_EXE) $(DPKG_EDEN_EXE)
	chmod 755 $(DPKG_EDEN_EXE)


prepare: $(DPKG_EDEN_EXE)
	find $(DMAKE_DIR) -type d | xargs chmod 755


build:
	dpkg-deb --build $(DMAKE_DIR)


clean:
	rm -rf $(DMAKE_DIR)


dpkg: clean init prepare build

