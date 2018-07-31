# Makefile for generating dpkgs for the native-image compiled 'fif' executable
# Requires: leiningen
# Optional: fif

EMAIL := benzaporzan@gmail.com

GIT_ROOT_DIR := $(shell git rev-parse --show-toplevel)
PROJECT_ROOT_DIR := $(GIT_ROOT_DIR)
DIST_DIR := $(PROJECT_ROOT_DIR)/dist

OS := $(shell uname)
ARCH := $(shell uname -i)

FIF_EXISTS := $(shell which fif >/dev/null && echo "True" || echo "False")

# Use fif to retrieve the project version, since it's faster
ifeq ($(FIF_EXISTS), True)
FIF_VERSION := $(shell fif -e \"$(PROJECT_ROOT_DIR)/project.clj\" read-file first 2 nth println)
else
FIF_VERSION := $(shell lein project-version)
endif

FIF_EXE_NAME := fif-$(FIF_VERSION)
TAR_NAME := $(FIF_EXE_NAME)-$(ARCH).tar.gz

TMAKE_DIR := $(DIST_DIR)/tar/$(FIF_EXE_NAME)
PROJ_FIF_EXE := $(PROJECT_ROOT_DIR)/bin/$(FIF_EXE_NAME)
TAR_FIF_EXE := $(TMAKE_DIR)/usr/bin/$(FIF_EXE_NAME)


# default
all: tar


init:
	mkdir -p $(TMAKE_DIR)
	cp $(PROJECT_ROOT_DIR)/LICENSE $(TMAKE_DIR)/LICENSE
	cp $(PROJECT_ROOT_DIR)/readme.org $(TMAKE_DIR)/readme.org
	cp $(PROJECT_ROOT_DIR)/changelog.org $(TMAKE_DIR)/changelog.org

	cp tar_makefile.template $(TMAKE_DIR)/Makefile
	sed -i "s/FIF_EXE_NAME/$(FIF_EXE_NAME)/g" $(TMAKE_DIR)/Makefile


$(PROJ_FIF_EXE):
	echo "Building Native Image..."
	cd $(PROJECT_ROOT_DIR) && ./build-native.sh


$(TAR_FIF_EXE): $(PROJ_FIF_EXE)
	mkdir -p $(TMAKE_DIR)/usr/bin
	cp $(PROJ_FIF_EXE) $(TAR_FIF_EXE)
	chmod 755 $(TAR_FIF_EXE)


prepare: $(TAR_FIF_EXE)


build:
	cd $(DIST_DIR)/tar && \
           tar -czvf $(TAR_NAME) $(FIF_EXE_NAME) && \
           mv $(TAR_NAME) ..


tar: clean init prepare build


clean:
	rm -rf $(TMAKE_DIR)
	rm -f $(DIST_DIR)/$(TAR_NAME)
