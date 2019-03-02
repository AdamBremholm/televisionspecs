pipeline {
  agent any
  stages {
    stage('Build with unit testing') {
      steps {
        script {
          echo 'Pulling...' + env.BRANCH_NAME
          def mvnHome = tool 'Maven 3.5.2'
          if (isUnix()) {
            def targetVersion = getDevVersion()
            print 'target build version...'
            print targetVersion
            sh "'${mvnHome}/bin/mvn' -Dintegration-tests.skip=true -Dbuild.number=${targetVersion} clean package"
            def pom = readMavenPom file: 'pom.xml'
            // get the current development version
            developmentArtifactVersion = "${pom.version}-${targetVersion}"
            print pom.version
            // execute the unit testing and collect the reports
            junit '**//*target/surefire-reports/TEST-*.xml'
            archive 'target*//*.jar'
          } else {
            bat(/"${mvnHome}\bin\mvn" -Dintegration-tests.skip=true clean package/)
            def pom = readMavenPom file: 'pom.xml'
            print pom.version
            junit '**//*target/surefire-reports/TEST-*.xml'
            archive 'target*//*.jar'
          }
        }

      }
    }
    stage('Integration tests') {
      steps {
        script {
          def mvnHome = tool 'Maven 3.5.2'
          if (isUnix()) {
            // just to trigger the integration test without unit testing
            sh "'${mvnHome}/bin/mvn'  verify -Dunit-tests.skip=true"
          } else {
            bat(/"${mvnHome}\bin\mvn" verify -Dunit-tests.skip=true/)
          }
        }

        cucumber(fileIncludePattern: '**/cucumber.json', jsonReportDirectory: 'target', sortingMethod: 'ALPHABETICAL')
      }
    }
    stage('Sonar scan execution') {
      steps {
        script {
          def mvnHome = tool 'Maven 3.5.2'
          withSonarQubeEnv {

            sh "'${mvnHome}/bin/mvn'  verify sonar:sonar -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
          }
        }

      }
    }
    stage('Sonar scan result check') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          retry(count: 3) {
            script {
              def qg = waitForQualityGate()
              if (qg.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
              }
            }

          }

        }

      }
    }
    stage('Development deploy approval and deployment') {
      steps {
        script {
          if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
            timeout(time: 3, unit: 'MINUTES') {
              // you can use the commented line if u have specific user group who CAN ONLY approve
              //input message:'Approve deployment?', submitter: 'it-ops'
              input message: 'Approve deployment?'
            }
            timeout(time: 2, unit: 'MINUTES') {
              //
              if (developmentArtifactVersion != null && !developmentArtifactVersion.isEmpty()) {
                // replace it with your application name or make it easily loaded from pom.xml
                def jarName = "application-${developmentArtifactVersion}.jar"
                echo "the application is deploying ${jarName}"
                // NOTE : CREATE your deployemnt JOB, where it can take parameters whoch is the jar name to fetch from jenkins workspace
                build job: 'ApplicationToDev', parameters: [[$class: 'StringParameterValue', name: 'jarName', value: jarName]]
                echo 'the application is deployed !'
              } else {
                error 'the application is not  deployed as development version is null!'
              }

            }
          }
        }

      }
    }
    stage('DEV sanity check') {
      steps {
        sleep 45
        script {
          if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
            timeout(time: 1, unit: 'MINUTES') {
              script {
                def mvnHome = tool 'Maven 3.5.2'
                //NOTE : if u change the sanity test class name , change it here as well
                sh "'${mvnHome}/bin/mvn' -Dtest=ApplicationSanityCheck_ITT surefire:test"
              }

            }
          }
        }

      }
    }
    stage('Release and publish artifact') {
      when {
        branch 'master'
      }
      steps {
        script {
          def mvnHome = tool 'Maven 3.5.2' //
          if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
            def v = getReleaseVersion()
            releasedVersion = v;
            if (v) {
              echo "Building version ${v} - so released version is ${releasedVersion}"
            }
            // jenkins user credentials ID which is transparent to the user and password change
            sshagent(['0000000-3b5a-454e-a8e6-c6b6114d36000']) {
              sh "git tag -f v${v}"
              sh "git push -f --tags"
            }
            sh "'${mvnHome}/bin/mvn' -Dmaven.test.skip=true  versions:set  -DgenerateBackupPoms=false -DnewVersion=${v}"
            sh "'${mvnHome}/bin/mvn' -Dmaven.test.skip=true clean deploy"

          } else {
            error "Release is not possible. as build is not successful"
          }
        }

      }
    }
    stage('Deploy to Acceptance') {
      when {
        branch 'master'
      }
      steps {
        script {
          if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
            timeout(time: 3, unit: 'MINUTES') {
              //input message:'Approve deployment?', submitter: 'it-ops'
              input message: 'Approve deployment to UAT?'
            }
            timeout(time: 3, unit: 'MINUTES') {
              //  deployment job which will take the relasesed version
              if (releasedVersion != null && !releasedVersion.isEmpty()) {
                // make the applciation name for the jar configurable
                def jarName = "application-${releasedVersion}.jar"
                echo "the application is deploying ${jarName}"
                // NOTE : DO NOT FORGET to create your UAT deployment jar , check Job AlertManagerToUAT in Jenkins for reference
                // the deployemnt should be based into Nexus repo
                build job: 'AApplicationToACC', parameters: [[$class: 'StringParameterValue', name: 'jarName', value: jarName], [$class: 'StringParameterValue', name: 'appVersion', value: releasedVersion]]
                echo 'the application is deployed !'
              } else {
                error 'the application is not  deployed as released version is null!'
              }

            }
          }
        }

      }
    }
    stage('ACC E2E tests') {
      when {
        branch 'master'
      }
      steps {
        sleep 45
        script {
          if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
            timeout(time: 1, unit: 'MINUTES') {

              script {
                def mvnHome = tool 'Maven 3.5.2'
                // NOTE : if you change the test class name change it here as well
                sh "'${mvnHome}/bin/mvn' -Dtest=ApplicationE2E surefire:test"
              }

            }
          }
        }

      }
    }
  }
  environment {
    EMAIL_RECIPIENTS = 'adambremholm@gmail.com'
  }
  post {
    always {
      deleteDir()

    }

    success {
      sendEmail 'Successful'

    }

    unstable {
      sendEmail 'Unstable'

    }

    failure {
      sendEmail 'Failed'

    }

  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
    timeout(time: 25, unit: 'MINUTES')
  }
}