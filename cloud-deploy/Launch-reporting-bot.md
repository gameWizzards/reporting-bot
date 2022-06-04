# Host (Tested on AWS AC2 service with Ubuntu 20:04)
1. Open https (port:443) input traffic on the host
2. Copy files from cloud-deploy dir to ~/reporting-bot-deploy/
   * scp -i reporting-ssh-key.pem -r /mnt/c/local_storage/java_projects/reporting-bot/cloud-deploy/* ubuntu@18.130.244.125:~/reporting-bot-deploy/

3. Run script host-preparing.sh (Change user passwords into the script if necessary)

4. Optional - if base image 'smarkov05/ubuntu-java17-mvn' doesn't exist on the Docker HUB then add mentioned dir to host and make all steps wrote in README.md
   * Command to verify - docker search smarkov05/ubuntu-java17-mvn

5. Run 'reporting-bot' and 'reporting-db' containers
   * docker-compose -f /home/reporting-bot/deploy-files/docker-compose-bot.yml up --build -d

6. Create and run Jenkins in docker container
   * Below command needs to connect to report-bot container via SSH without manual write password  
   * sshpass -p ssh-passwd ssh -o StrictHostKeyChecking=no root@reporting-bot

7. Add required plugins to Jenkins.
   * GIT plugin
   * SSH Pipeline steps
   
docker-compose's version required to 1.29.... like in host script
 
