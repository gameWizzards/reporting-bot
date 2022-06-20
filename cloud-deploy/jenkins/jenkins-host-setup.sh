jenkinsPasswd="jenkins"

echo "__ Adding jenkins user"
sudo useradd jenkins -m -G sudo,docker -s /bin/bash ; echo "jenkins:$jenkinsPasswd" | sudo chpasswd \
&& sudo groupmod -g 7000 jenkins \
&& echo "__ Adding jenkins necessary dirs"; sudo install -m 0775 -o jenkins -g jenkins -d /home/jenkins/volume/jenkins_home /home/jenkins/volume/certs /home/jenkins/deploy-files \
&& echo "__ Copping jenkins deploy files"; sudo cp -r ~/jenkins-deploy/. /home/jenkins/deploy-files/
