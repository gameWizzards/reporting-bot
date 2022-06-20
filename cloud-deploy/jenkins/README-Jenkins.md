# Preparing and deploying Jenkins
There are two options to deploy the Jenkins - with Docker and on host with appropriate environment.
For installation, use Linux based on Debian (Ubuntu) distributive.

## Deploying with Docker (Preferred)
1. Check for Docker at least v20.10.10 and Docker-compose at least v1.29.2
   * if services don't install then they will be installed and configured in third step.
   * if some of these services have unappropriated versions then remove it and after that removed service will be installed and configured in second step. 
   * for removing the Docker run the following command - sudo apt-get purge docker-ce docker-ce-cli containerd.io docker-compose-plugin
   * for removing the Docker-compose use instructions written on this link - https://docs.docker.com/compose/install/uninstall/ 
2. Copy to host files from docker-setup dir.
   * copying command - scp -i {path_to_ssh_key_file} -r {path_to_project}/cloud-deploy/jenkins/docker-setup/*
     {user_name}@{host_ip}:~/jenkins-deploy
3. Run on host jenkins-docker-setup.sh to prepare host for further installation.
   * **IMPORTANT** - necessarily to change password in variable "jenkinsPasswd" for jenkins user in jenkins-docker-setup.sh
4. Build image 
   * docker build -t jenkins-bo /home/jenkins/deploy-files/
5. Run container with docker-compose command
   * docker-compose -f /home/jenkins/deploy-files/docker-compose-jenkins.yml up -d
6. Jenkins is available by link - http://{your-ip-address}:8008/

## Deploying on host
1.  Copy to host and run jenkins-host-setup.sh to prepare host for further installation.
   * **IMPORTANT** - necessarily to change password in variable "jenkinsPasswd" for jenkins user in jenkins-host-setup.sh
   * copying command - scp -i {path_to_ssh_key_file} -r {path_to_project}/cloud-deploy/jenkins/jenkins-host-setup.sh
     {user_name}@{host_ip}:~/jenkins-deploy/
2. Use instruction from origin - https://www.jenkins.io/doc/book/installing/linux/

## After install add follow plugins
* GIT plugin
* SSH Pipeline steps
* Credentials
* Telegram plugin for notification

### Some useful info
* for connect to reporting-bot via SSh plugin must use plugin Credentials and withCredentials() method in Jenkinsfile
* for webhook have to create and add API token to user with access to particular job/pipeline after that add token to Github webhook
* Jenkins global env variables can add in Jenkins system config (example: for var 'remote' = [name: env.BOT_NAME, host: env.BOT_HOST])