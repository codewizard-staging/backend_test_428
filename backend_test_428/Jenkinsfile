pipeline {
   agent any
    tools { 
        maven 'maven-3.8.6' 
    }
 triggers {
    pollSCM('') 
  }
   stages {
       stage('SCM clone') {
           steps {
		      checkout scm
               println("code has been cloned from git hub")
           }
       }
       stage('Maven build') {
                   steps {
                      configFileProvider([configFile(fileId: '4359eccd-1cb1-4e66-999e-40d5729f4d92', variable: 'MAVEN_SETTINGS_XML')]) {
                         sh 'mvn -U --batch-mode -s $MAVEN_SETTINGS_XML clean deploy '
        }
                       println("executing maven build and deploy goal")
                   }
       }
   }
    post{
        failure{
		    emailext body: '$DEFAULT_CONTENT', subject: '$DEFAULT_SUBJECT', to: '$DEFAULT_RECIPIENTS'
        }
    }
}