#
# Yet Another UserAgent Analyzer
# Copyright (C) 2013-2025 Niels Basjes
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
FROM ubuntu:24.04

WORKDIR /root

ENV INSIDE_DOCKER=Yes

ARG DEBIAN_FRONTEND=noninteractive

# Suppress an apt-key warning about standard out not being a terminal. Use in this script is safe.
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=DontWarn

WORKDIR /root

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

#####
# Disable suggests/recommends
#####
RUN echo APT::Install-Recommends "0"\; > /etc/apt/apt.conf.d/10disableextras
RUN echo APT::Install-Suggests "0"\; >>  /etc/apt/apt.conf.d/10disableextras

ENV DEBIAN_FRONTEND="noninteractive"
ENV DEBCONF_TERSE="true"

###
# Update and install common packages
###
RUN apt clean cache \
   && apt -q update \
   && apt install -y software-properties-common apt-utils apt-transport-https ca-certificates \
   && apt clean cache

RUN apt-get -q install -y --no-install-recommends \
    bash-completion \
    build-essential \
    bzip2 \
    wget \
    curl \
    docker.io \
    git \
    gnupg-agent \
    rsync \
    sudo \
    vim \
    locales \
    wget \
    time \
    tzdata \
    openjdk-8-jdk \
    openjdk-11-jdk \
    openjdk-17-jdk \
    openjdk-21-jdk \
    diffoscope \
    python3-tlsh \
    tlsh-tools \
     && apt clean cache

###
# Install the latest JDK if not an LTS version (needed for Trino)
###
ENV NON_LTS_JDK=24
RUN wget "https://download.oracle.com/java/${NON_LTS_JDK}/latest/jdk-${NON_LTS_JDK}_linux-x64_bin.deb"
RUN apt install "./jdk-${NON_LTS_JDK}_linux-x64_bin.deb"

###
# The update-java-alternatives uses the config files like /usr/lib/jvm/.java-1.8.0-openjdk-amd64.jinfo
# Some of these files contain a reference to mozilla-javaplugin.so which we do not need AND which does not exist
# in this docker image. So we remove this reference as it will give a needless warning every time we switch JDK.
###
RUN sed -i '/^plugin mozilla-javaplugin.so.*$/d' /usr/lib/jvm/.*.jinfo

###
# Set the locale ( see https://stackoverflow.com/a/28406007/114196 )
###
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US:en"
ENV LC_ALL="en_US.UTF-8"

# --------------------------------
# Install Maven ( https://maven.apache.org/download.cgi )
ENV MAVEN_VERSION=3.9.9
RUN mkdir -p /usr/local/apache-maven
WORKDIR /usr/local/
RUN wget "https://www.apache.org/dyn/closer.lua?action=download&filename=/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -O "apache-maven-${MAVEN_VERSION}-bin.tar.gz"
RUN tar xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz --strip-components 1 -C /usr/local/apache-maven
ENV M2_HOME=/usr/local/apache-maven
ENV PATH=${M2_HOME}/bin:${PATH}

# Avoid out of memory errors in builds
ENV MAVEN_OPTS="-Xms256m -Xmx512m"

# Install command line completion for maven
RUN wget https://raw.githubusercontent.com/juven/maven-bash-completion/master/bash_completion.bash -O /etc/bash_completion.d/maven

# --------------------------------
# Install shellcheck ( https://github.com/koalaman/shellcheck/releases )
ENV SHELLCHECK_VERSION=0.9.0
WORKDIR /usr/local/bin
RUN wget https://github.com/koalaman/shellcheck/releases/download/v${SHELLCHECK_VERSION}/shellcheck-v${SHELLCHECK_VERSION}.linux.x86_64.tar.xz && \
    tar xJf shellcheck-v${SHELLCHECK_VERSION}.linux.x86_64.tar.xz && \
    mv shellcheck-v${SHELLCHECK_VERSION}/shellcheck . && \
    rm -rf shellcheck-v${SHELLCHECK_VERSION} shellcheck-v${SHELLCHECK_VERSION}.linux.x86_64.tar.xz

# --------------------------------
# Install Hugo ( https://github.com/gohugoio/hugo/releases )
ENV HUGO_VERSION=0.136.3
WORKDIR /usr/local/bin
RUN wget https://github.com/gohugoio/hugo/releases/download/v${HUGO_VERSION}/hugo_${HUGO_VERSION}_Linux-64bit.tar.gz && \
    tar xzf hugo_${HUGO_VERSION}_Linux-64bit.tar.gz && \
    rm -f hugo_${HUGO_VERSION}_Linux-64bit.tar.gz

###
# Better reproducibility: deliberately set default timezone VERY uncommon (only ~200 people in this timezone)
###
RUN ln -fs /usr/share/zoneinfo/Australia/Eucla /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

# --------------------------------
# Add a welcome message and environment checks.
RUN mkdir /scripts
ADD *.sh /scripts/
RUN chmod 755 /scripts/*.sh

# --------------------------------
# For serving the documentation site
EXPOSE 1313
