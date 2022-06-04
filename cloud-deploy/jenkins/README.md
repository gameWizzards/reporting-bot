# Preparing Jenkins container
1. Copy Dockerfile and docker-compose-jenkins.yml files to host to appropriate dir 
2. Build image 
   * docker build -t jenkins-bo /home/jenkins/deploy-files/
3. Make sure that the dir for docker volumes has appropriate privileges  
4. Run container by command or docker-compose
    * docker-compose -f /home/jenkins/deploy-files/docker-compose-jenkins.yml up -d
    * docker run --name jenkins-bo \
     --restart=unless-stopped --detach \
     --network reporting-bot-network  \
     --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 \
     --publish 8008:8080 --publish 50000:50000 --publish 127.0.0.1:8223:22 \
     --volume /home/jenkins/volume/jenkins_home:/var/jenkins_home \
     --volume /home/jenkins/volume/certs:/certs/client:ro \
     jenkins-bo
