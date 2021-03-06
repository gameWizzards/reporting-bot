#!/bin/bash
jenkinsPasswd="jenkins"

exist=1
notExist=0

sudo apt-get update
if [ $(docker --version 2> /dev/null | grep -c "version") -eq $notExist ]
  then
    echo "__ Installing the Docker"
    sudo apt-get install -y ca-certificates curl gnupg lsb-release
    sudo mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null;

    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    echo "__ Docker's installed successfully"


fi
if [ $(docker-compose -v 2> /dev/null | grep -c "version") -eq $notExist ]
  then
    echo "__ Installing the Docker-compose"
    sudo apt-get update
    # sudo apt upgrade
    sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "__ Docker compose has installed successfully"
fi

if [ $(docker --version 2> /dev/null | grep -c "version") -eq $exist ]
  then
    echo "__ Enable to auto-run Docker on host boot"
    sudo systemctl enable docker.service
    sudo systemctl enable containerd.service

    echo "__ Manage Docker as a non-root user for current user"
    sudo groupadd -f docker
    sudo usermod -aG docker $USER

    echo "__ Adding jenkins user"
    sudo useradd jenkins -m -G sudo,docker -s /bin/bash ; echo "jenkins:$jenkinsPasswd" | sudo chpasswd \
    && sudo groupmod -g 7000 jenkins \
    && echo "__ Adding jenkins necessary dirs"; sudo install -m 0775 -o jenkins -g jenkins -d /home/jenkins/volume/jenkins_home /home/jenkins/volume/certs /home/jenkins/deploy-files \
    && echo "__ Copping jenkins deploy files"; sudo cp -r ~/jenkins-deploy/. /home/jenkins/deploy-files/
    echo "__ Congratulations!!! All configurations have done successfully!"

    # Required to be the last operation, after that no one command will not be executed
    echo "__ Activating changes to Docker group"
    newgrp docker
fi
