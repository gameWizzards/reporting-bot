# Preparing and deploying Reporting-bot
For installation, use Linux based on Debian (Ubuntu) distributive. (Tested on AWS AC2 service with Ubuntu 20:04)

 Copy files from cloud-deploy dir to ~/reporting-bot-deploy/
   * scp -i reporting-ssh-key.pem -r /mnt/c/local_storage/java_projects/reporting-bot/cloud-deploy/* ubuntu@18.130.244.125:~/reporting-bot-deploy/

1. Check for Docker at least v20.10.10 and Docker-compose at least v1.29.2
   * if services don't install then they will be installed and configured in third step.
   * if some of these services have unappropriated versions then remove it and after that removed service will be installed and configured in second step.
   * for removing the Docker run the following command - sudo apt-get purge docker-ce docker-ce-cli containerd.io docker-compose-plugin
   * for removing the Docker-compose use instructions written on this link - https://docs.docker.com/compose/install/uninstall/
2. Copy to host files from reporting-bot dir, include .env file.
   * copying command - scp -i {path_to_ssh_key_file} -r {path_to_project}/cloud-deploy/reporting-bot/.
     {user_name}@{host_ip}:~/reporting-bot-deploy/
3. Run on host reporting-bot-setup.sh to prepare host for further installation.
   * **IMPORTANT** - necessarily to change password in variable "repoBotPasswd" for reporting-bot user in reporting-bot-setup.sh
4. Check for necessary Docker image 'smarkov05/ubuntu-java17-mvn' on the Docker HUB
   * Command to verify - docker search smarkov05/ubuntu-java17-mvn
   * if image doesn't exist then make all steps wrote in README-ubuntu-java17.md file which path is cloud-deploy/ubuntu-java17-mvn/ 
5. Run 'reporting-bot' and 'reporting-db' containers
   * docker-compose -f /home/reporting-bot/deploy-files/docker-compose-bot.yml up --build -d

### For create CI/CD use instruction README-Jenkins.md which path is /cloud-deploy/jenkins/
