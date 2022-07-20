def remote = [name: env.BOT_NAME, host: env.BOT_HOST, allowAnyHosts: true]

pipeline {
    agent any
    parameters{
       booleanParam(defaultValue: true, description: 'Deploy reporting-bot', name: 'enable_step_DEPLOY')
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
                script{
                    withCredentials([sshUserPrivateKey(credentialsId: 'RepoBotHostDO-pk-Creds', keyFileVariable: 'USER_SECRET', usernameVariable: 'USER_LOGIN')]) {
                        remote.user = USER_LOGIN
                        remote.identityFile = USER_SECRET
                        sshCommand remote: remote, command: String.format('docker exec reporting-bot ps -ef | grep java | grep -v grep | awk \'{print \$2}\' | docker exec -i reporting-bot xargs -r kill; docker exec reporting-bot mkdir -p %s; docker exec reporting-bot rm -rf %s; docker exec reporting-bot mkdir %s; docker exec reporting-bot git clone -b master https://github.com/gameWizzards/reporting-bot.git %s;',
                                                                            "$TESTDIR",
                                                                            "$TESTDIR/$APPDIR/",
                                                                            "$TESTDIR/$APPDIR/",
                                                                            "$TESTDIR/$APPDIR/")
                    }
                }
            }
        }
        stage('Unit_tests') {
            steps {
                script{
                    withCredentials([sshUserPrivateKey(credentialsId: 'RepoBotHostDO-pk-Creds', keyFileVariable: 'USER_SECRET', usernameVariable: 'USER_LOGIN')]) {
                         remote.user = USER_LOGIN
                         remote.identityFile = USER_SECRET
                         sshCommand remote: remote, command: String.format('docker exec reporting-bot mvn verify -f %s;',
                                                                            "$TESTDIR/$APPDIR")
                    }
                }
            }
        }
        stage('Integration_tests') {
            steps{
                echo 'Here will be integration tests'
                //sshCommand remote: remote, command: String.format('docker exec reporting-bot mvn verify -f %s',
                                                                     //"$TESTDIR/$APPDIR")
            }
        }
        stage('Deploy') {
            when {
                expression {
                    return params.enable_step_DEPLOY
                }
            }
            steps{
                script{
                    withCredentials([sshUserPrivateKey(credentialsId: 'RepoBotHostDO-pk-Creds', keyFileVariable: 'USER_SECRET', usernameVariable: 'USER_LOGIN')]) {
                        remote.user = USER_LOGIN
                        remote.identityFile = USER_SECRET
                        sshCommand remote: remote, command: String.format('docker exec reporting-bot mkdir -p %s; docker exec reporting-bot cp %s/target/%s %s; docker exec reporting-bot java -Dspring.profiles.active=%s -jar %s >/dev/null 2>&1 &',
                                                                            "$WORKDIR",
                                                                            "$TESTDIR/$APPDIR",
                                                                            "$APPNAME",
                                                                            "$WORKDIR/",
                                                                            "$SPRING_PROFILE",
                                                                            "$WORKDIR/$APPNAME")
                    }
                }
            }
        }
    }
    post {
        always {
            telegramSend(message: "Job = ${JOB_NAME}. Status = ${currentBuild.getCurrentResult()}")
        }
   }
}