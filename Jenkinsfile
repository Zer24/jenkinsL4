pipeline {
    agent any

    tools {
        maven 'maven-3.9.14'
        jdk 'jdk-21'
    }

    environment {
        GH_TOKEN = credentials('github-token')
        REPO_NAME = 'https://github.com/Zer24/jenkinsL4.git'
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

        // ТОЛЬКО ДЛЯ MAIN: загрузка в GitHub Releases
        stage('GitHub Release') {
            steps {
                script {
                    def jarFile = findFiles(glob: 'target/*.jar')[0]
                    def tagName = "release-v${BUILD_NUMBER}"

                    sh """
                        gh release create ${tagName} \
                            ${jarFile.path} \
                            --repo ${REPO_NAME} \
                            --title "Production Release ${BUILD_NUMBER}" \
                            --notes "Релиз из main ветки" \
                            --latest=true
                    """
                    echo "✅ Создан релиз на GitHub"
                }
            }
        }
    }

    post {
        success {
            echo "✅ MAIN ветка: сборка и релиз успешны"
        }
    }
}