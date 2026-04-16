pipeline {
    agent any

    tools {
        maven 'maven-3.9.14'  // замените на имя вашего Maven в Jenkins
        jdk 'jdk-21'          // замените на имя вашего JDK в Jenkins
    }

    environment {
        // Загружаем GitHub токен из Jenkins Credentials
        GH_TOKEN = credentials('github-token')
        // Определяем имя репозитория (замените на свой!)
        REPO_NAME = 'https://github.com/Zer24/jenkinsL4.git'
    }

    stages {
        // ========== 1. Checkout ==========
        stage('Checkout') {
            steps {
                checkout scm
                // Для работы с git командами нужно настроить git config
                script {
                    bat 'git config --global user.email "jenkins@example.com"'
                    bat 'git config --global user.name "Jenkins CI"'
                }
            }
        }

        // ========== 2. Clean & Compile ==========
        stage('Clean & Compile') {
            steps {
                bat 'mvn clean compile'
            }
        }

        // ========== 3. Static Code Analysis ==========
        stage('Static Code Analysis') {
            steps {
                bat 'mvn spotbugs:check || exit 0'
                bat 'mvn checkstyle:check || exit 0'
                bat 'mvn pmd:check || exit 0'

                recordIssues tools: [
                    spotBugs(pattern: '**/spotbugsXml.xml'),
                    checkStyle(pattern: '**/checkstyle-result.xml'),
                    pmdParser(pattern: '**/pmd.xml')
                ]
            }
        }

        // ========== 4. Build JAR ==========
        stage('Build JAR') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        // ========== 5. Archive Artifact (Jenkins storage) ==========
        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // ========== 6. GitHub Release (ТОЛЬКО для main ветки!) ==========
        stage('GitHub Release') {
            when {
                branch 'main'  // <-- ТОЛЬКО main ветка
            }
            steps {
                script {
                    // Находим JAR файл
                    def jarFile = findFiles(glob: 'target/*.jar')[0]
                    def jarPath = jarFile.path

                    // Формируем тег и название релиза
                    def version = "v${BUILD_NUMBER}-${env.BRANCH_NAME}"
                    def tagName = "release-${version}"

                    echo "Creating GitHub release for branch: ${env.BRANCH_NAME}"
                    echo "JAR file: ${jarPath}"
                    echo "Tag name: ${tagName}"

                    bat """
                        curl -X POST \
                          -H "Authorization: token ${GH_TOKEN}" \
                          -H "Content-Type: application/json" \
                          https://api.github.com/repos/${REPO_NAME}/releases \
                          -d "{\"tag_name\":\"${tagName}\",\"name\":\"Release ${BUILD_NUMBER}\",\"body\":\"Auto release from Jenkins\",\"draft\":false,\"prerelease\":false}"
                    """

                    echo "GitHub Release created successfully: https://github.com/${REPO_NAME}/releases/tag/${tagName}"
                }
            }
        }

        // ========== 7. Информация для develop ветки ==========
        stage('Info for Develop') {
            when {
                branch 'develop'
            }
            steps {
                echo "📌 DEVELOP BRANCH: Артефакт НЕ загружен в GitHub Releases (только для main)"
                echo "📌 JAR файл сохранён в Jenkins ArchiveArtifact"
            }
        }

        // ========== 8. Информация для feature ветки ==========
        stage('Info for Feature') {
            when {
                branch pattern: "feature/.*", comparator: "REGEXP"
            }
            steps {
                echo "📌 FEATURE BRANCH: Только компиляция и статический анализ"
                echo "📌 Артефакт НЕ создаётся для feature веток"
            }
        }
    }

    post {
        always {
            echo "========================================="
            echo "Pipeline execution finished for branch: ${env.BRANCH_NAME}"
            echo "Build number: ${BUILD_NUMBER}"
            echo "========================================="
        }
        failure {
            echo "❌ Build FAILED for branch ${env.BRANCH_NAME}. Check console output."
        }
        success {
            echo "✅ Build SUCCESSFUL for branch ${env.BRANCH_NAME}"
        }
    }
}