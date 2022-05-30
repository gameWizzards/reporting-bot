def remote = [name: 'reporting-bot', host: 'reporting-bot', user: 'root', password: 'ssh-passwd', allowAnyHosts: true]
pipeline {
    agent any
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
                sshCommand remote: remote, command: "mkdir -p $TESTDIR"
                sshCommand remote: remote, command: "rm -rf $TESTDIR/*"
                sshCommand remote: remote, command: "cd $TESTDIR/;  git clone -b master https://github.com/gameWizzards/reporting-bot.git"
            }
        }
        stage('Build') {
            steps{
                sshCommand remote: remote, command: "mvn package -DskipTests -f $TESTDIR/$APPDIR"
            }
        }
        stage('Unit_tests') {
            steps {
                sshCommand remote: remote, command: "mvn test -f $TESTDIR/$APPDIR"
            }

        }
        stage('Integrational_tests') {
            steps{
                echo "Doesn't have integration tests"
            }
        }
        stage('Deploy') {
            steps{
                sshCommand remote: remote, command: "mkdir -p $WORKDIR"
                sshCommand remote: remote, command: "cp $TESTDIR/$APPDIR/target/$APPNAME $WORKDIR/"
                sshCommand remote: remote, command: "ps -ef | grep java | grep -v grep | awk '{print \$2}' | xargs -r kill"
                sshCommand remote: remote, command: "java -Dspring.profiles.active=$SPRING_PROFILE -jar $WORKDIR/$APPNAME >/dev/null 2>&1 &"
            }
        }
    }
}