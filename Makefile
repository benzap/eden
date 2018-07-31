# Main makefile for building distributable packages of native eden
# executable
#
# For Debian-based systems:
#
# $ make deb
#
# For RPM-based systems:
#
# $ make rpm
#
# For a compressed archive:
#
# $ make tar
#
# To Install Locally from the repository:
#
# $ make install
#
# or include a custom PREFIX to install elsewhere:
#
# $ PREFIX=~/.bin make install
#
# Configuration:
#
# Requires: leiningen
#
# Requires: GraalVM with GRAAL_HOME environment variable set to the
# root of the graal folder (might work if you just have the command native-image on the path)


PREFIX := /usr/bin
EDEN_VERSION := $(shell lein project-version)
EDEN_EXE_NAME := eden-$(EDEN_VERSION)
PROJ_EDEN_EXE := bin/$(EDEN_EXE_NAME)


# default
all: clean build_native


# Generate eden native executable
build_native: $(PROJ_EDEN_EXE)


# Generate deb Package for native executable
# Note: Tested on Ubuntu 17.10
dpkg: $(PROJ_EDEN_EXE)
	make -C dist_config/dpkg/


# Generate tar.gz Distribution for native executable
tar: $(PROJ_EDEN_EXE)
	make -C dist_config/tar/


# Generate rpm Package for native executable
# Note: Tested on Fedora 28
rpm: $(PROJ_EDEN_EXE)
	make -C dist_config/rpmpkg/


# Install Native Executable
# Note: Tested in linux
install: $(PROJ_EDEN_EXE)
	cp $(PROJ_EDEN_EXE) $(PREFIX)/$(EDEN_EXE_NAME)
	chmod 755 $(PREFIX)/$(EDEN_EXE_NAME)
	rm -f $(PREFIX)/eden
	ln -s $(PREFIX)/$(EDEN_EXE_NAME) $(PREFIX)/eden


clean:
	rm -f $(PROJ_EDEN_EXE)
	rm -rf dist
	rm -rf bin


distclean:
	rm -f $(PREFIX)/$(EDEN_EXE_NAME)
	rm -f $(PREFIX)/eden


$(PROJ_EDEN_EXE):
	sh ./build-native.sh
