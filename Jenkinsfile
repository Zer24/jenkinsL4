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
                sh 'mvn clean compile'
            }
        }

        stage('Static Code Analysis') {
            steps {
                sh 'mvn spotbugs:check || true'
                sh 'mvn checkstyle:check || true'
                sh 'mvn pmd:check || true'

                recordIssues tools: [
                    spotBugs(pattern: '**/spotbugsXml.xml'),
                    checkStyle(pattern: '**/checkstyle-result.xml'),
                    pmdParser(pattern: '**/pmd.xml')
                ]
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // НЕТ GitHub Release
        stage('Info') {
            steps {
                echo "📌 DEVELOP ветка: JAR собран, но НЕ загружен в релизы"
                echo "📌 Только для тестирования интеграции"
            }
        }
    }

    post {
        success {
            echo "✅ DEVELOP ветка: сборка успешна (релиз не создаётся)"
        }
    }
}