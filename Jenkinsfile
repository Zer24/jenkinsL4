pipeline {
    agent any

    tools {
        maven 'maven-3.9.14'
        jdk 'jdk-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean & Compile') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Static Code Analysis') {
            steps {
                bat 'mvn spotbugs:check & mvn checkstyle:check & mvn pmd:check'

                // Публикуем отчёты
                recordIssues tools: [
                    spotBugs(pattern: '**/spotbugsXml.xml'),
                    checkStyle(pattern: '**/checkstyle-result.xml'),
                    pmdParser(pattern: '**/pmd.xml')
                ]
            }
        }

        stage('Build JAR') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution finished'
        }
        failure {
            echo 'Build failed. Check console output.'
        }
    }
}