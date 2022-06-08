def remote = [name: env.BOT_NAME, host: env.BOT_HOST, allowAnyHosts: true]

pipeline {
    agent any
    parameters{
       booleanParam(defaultValue: false, description: 'Deploy reporting-bot', name: 'enable_step_DEPLOY')
    }
     options {
        skipDefaultCheckout true
        disableConcurrentBuilds()
        timeout(time:10, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '10', daysToKeepStr: '7', numToKeepStr: '50'))
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
                checkout scm
                script{
                    withCredentials([usernamePassword(credentialsId: 'RepoBotHostDOCreds', passwordVariable: 'USER_SECRET', usernameVariable: 'USER_LOGIN')]) {
                        remote.user = USER_LOGIN
                        remote.password = USER_SECRET
                        sshCommand remote: remote, command: 'docker exec reporting-bot mkdir -p ' + "$TESTDIR"
                        sshCommand remote: remote, command: 'docker exec reporting-bot rm -rf ' + "$TESTDIR/$APPDIR/"
                        sshCommand remote: remote, command: 'docker exec reporting-bot mkdir ' + "$TESTDIR/$APPDIR/"
                        sshCommand remote: remote, command: 'docker exec reporting-bot git clone -b master https://github.com/gameWizzards/reporting-bot.git ' + "$TESTDIR/$APPDIR/"
                    }
                }
            }
        }
        stage('Unit_tests') {
            steps {
                sshCommand remote: remote, command: 'docker exec reporting-bot mvn test -f ' + "$TESTDIR/$APPDIR"
            }

        }
        stage('Integration_tests') {
            steps{
                echo 'Here will be integration tests'
                //sshCommand remote: remote, command: 'docker exec reporting-bot mvn verify -f ' + "$TESTDIR/$APPDIR"
            }
        }
        stage('Deploy') {
            when {
                expression {
                    return params.enable_step_DEPLOY
                }
            }
            steps{
                sshCommand remote: remote, command: 'docker exec reporting-bot mkdir -p ' + "$WORKDIR"
                sshCommand remote: remote, command: 'docker exec reporting-bot cp ' + "$TESTDIR/$APPDIR/target/$APPNAME $WORKDIR/"
                sshCommand remote: remote, command: 'docker exec reporting-bot ps -ef | grep java | grep -v grep | awk \'{print \$2}\' | docker exec -i reporting-bot xargs -r kill'
                sshCommand remote: remote, command: 'docker exec reporting-bot java -Dspring.profiles.active=' + "$SPRING_PROFILE -jar $WORKDIR/$APPNAME >/dev/null 2>&1 &"
            }
        }
    }
}