# Preparing docker image for reporting-bot container
### Image contains next apps and utilities:
### Java 17, Maven 3.8.5, Git, SSH server.

1. Copy Dockerfile and conf_env.sh from 'ubuntu-java17-mvn' dir to appropriate dir on the host. Files must be in one dir.
2. Run docker command to create ubuntu-java17-mvn image with necessary utils and env
   * docker build -t smarkov05/ubuntu-java17-mvn:1.0 {path_to_dockerfile}

### Add image to docker HUB
1. Optional -Run the container and check the env
   * docker run --name test-jdk --hostname test-jdk --tty --detach ubuntu-jdk17-mvn
2. If you did some changes to container then update image in local repository
   * docker commit <cont_name> <new_image_name>
3. Push image to docker HUB
   * docker push smarkov05/ubuntu-java17-mvn:1.0