# Image contains next apps and utilities:
# Java 17, Maven 3.8.5, Git, SSH server.
FROM ubuntu:20.04
ENV PATH="/usr/share/maven-3.8.5/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
ENV JAVA_HOME="/usr/lib/jvm/jdk-17"
ENV MAVEN_HOME="/usr/share/maven-3.8.5"
COPY conf_env.sh /tmp
RUN sh /tmp/conf_env.sh
# required sh command to auto-run sshd
CMD ["/usr/sbin/sshd", "-D"]
