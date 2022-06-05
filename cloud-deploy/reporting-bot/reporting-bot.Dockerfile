FROM smarkov05/ubuntu-java17-mvn:1.0
ARG BOT_DB_NAME
ARG BOT_DB_USERNAME
ARG BOT_DB_PASSWORD
ARG BOT_DB_HOST
ARG BOT_DB_PORT
ARG BOT_USERNAME
ARG BOT_TOKEN
ARG JENKINS_SSH_PSWD

# add password for connect by ssh from jenkins container
RUN echo "JENKINS_SSH_PSWD=$JENKINS_SSH_PSWD"
RUN echo "root:$JENKINS_SSH_PSWD" | chpasswd

RUN echo "BOT_DB_NAME=$BOT_DB_NAME" >> /etc/environment \
    && echo "BOT_DB_USERNAME=$BOT_DB_USERNAME" >> /etc/environment \
    && echo "BOT_DB_PASSWORD=$BOT_DB_PASSWORD" >> /etc/environment \
    && echo "BOT_DB_HOST=$BOT_DB_HOST" >> /etc/environment \
    && echo "BOT_DB_PORT=$BOT_DB_PORT" >> /etc/environment \
    && echo "BOT_USERNAME=$BOT_USERNAME" >> /etc/environment \
    && echo "BOT_TOKEN=$BOT_TOKEN" >> /etc/environment \
