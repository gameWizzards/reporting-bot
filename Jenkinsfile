def remote = [name: 'reporting-bot', host: '139.59.174.8', user: 'reporting-bot', password: '123456', allowAnyHosts: true]
pipeline {
    agent any
     options {
        skipDefaultCheckout true
      }
    environment {
        BUILDDIR = "/var/reporting-bot"
        WORKDIR = "${BUILDDIR}/workdir"
        TESTDIR = "${BUILDDIR}/testdir"
        APPDIR = "reporting-bot"
        APPNAME = "reporting-telegrambot-1.0.0.jar"
        SPRING_PROFILE = "dev"
    }
    stages{
        stage('Checkout') {
            steps{
                sshCommand remote: remote, command: "docker exec reporting-bot mkdir -p $TESTDIR"
                sshCommand remote: remote, command: "docker exec reporting-bot rm -rf $TESTDIR/$APPDIR/"
                sshCommand remote: remote, command: "docker exec reporting-bot mkdir $TESTDIR/$APPDIR/"
                sshCommand remote: remote, command: "docker exec reporting-bot git clone -b master https://github.com/gameWizzards/reporting-bot.git $TESTDIR/$APPDIR/"
            }
        }
        stage('Build') {
            steps{
                sshCommand remote: remote, command: "docker exec reporting-bot mvn package -DskipTests -f $TESTDIR/$APPDIR"
            }
        }
        stage('Unit_tests') {
            steps {
                sshCommand remote: remote, command: "docker exec reporting-bot mvn test -f $TESTDIR/$APPDIR"
            }

        }
        stage('Integration_tests') {
            steps{
                echo "Doesn't have integration tests"
            }
        }
        stage('Deploy') {
            steps{
                sshCommand remote: remote, command: "docker exec reporting-bot mkdir -p $WORKDIR"
                sshCommand remote: remote, command: "docker exec reporting-bot cp $TESTDIR/$APPDIR/target/$APPNAME $WORKDIR/"
                sshCommand remote: remote, command: "docker exec reporting-bot ps -ef | grep java | grep -v grep | awk '{print \$2}' | docker exec -i reporting-bot xargs -r kill"
                sshCommand remote: remote, command: "docker exec reporting-bot java -Dspring.profiles.active=$SPRING_PROFILE -jar $WORKDIR/$APPNAME >/dev/null 2>&1 &"
            }
        }
    }
}