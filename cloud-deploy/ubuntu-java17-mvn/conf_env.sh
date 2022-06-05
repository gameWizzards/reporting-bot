#!/usr/bin/env bash

# update and install important apps to forward installations
apt-get update
apt-get install -y openssh-client openssh-server
# required dir to prevent Error: Missing privilege separation directory: /run/sshd
mkdir -p -m 0755 /var/run/sshd
chowm root:root /var/run/sshd

cd ~

# install Java 17
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.deb
apt-get install -y ~/jdk-17_linux-x64_bin.deb
update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk-17/bin/java" 1
update-alternatives --set java /usr/lib/jvm/jdk-17/bin/java
rm ~/jdk-17_linux-x64_bin.deb

# install maven 3.8.5
wget https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz
tar -xvzf apache-maven-3.8.5-bin.tar.gz
cp -r apache-maven-3.8.5 /usr/share/maven-3.8.5
rm ~/apache-maven-3.8.5-bin.tar.gz
rm -r ~/apache-maven-3.8.5

apt-get install -y git
apt-get update

# rewrite and add evn variable to general environment
echo "PATH=\"/usr/share/maven-3.8.5/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\"" > /etc/environment
# add evn variables to general environment
echo "JAVA_HOME=\"/usr/lib/jvm/jdk-17\"" >> /etc/environment
echo "MAVEN_HOME=\"/usr/share/maven-3.8.5\"" >> /etc/environment

# add param to be able to connect from jenkins container
if [ "$(grep 'PermitRootLogin yes' -c  /etc/ssh/sshd_config)" = 0 ];
then
  echo 'PermitRootLogin yes' >> /etc/ssh/sshd_config;
  echo 'sshd_conf was update';
fi

# clean unused date after install all utilities
apt-get clean autoclean && apt-get autoremove --yes && rm -rf /var/lib/{apt,dpkg,cache,log}/