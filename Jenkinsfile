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
        REPO_NAME = 'YOUR_USERNAME/YOUR_REPO_NAME'
    }

    stages {
        // ========== 1. Checkout ==========
        stage('Checkout') {
            steps {
                checkout scm
                // Для работы с git командами нужно настроить git config
                script {
                    sh 'git config --global user.email "jenkins@example.com"'
                    sh 'git config --global user.name "Jenkins CI"'
                }
            }
        }

        // ========== 2. Clean & Compile ==========
        stage('Clean & Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        // ========== 3. Static Code Analysis ==========
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

        // ========== 4. Build JAR ==========
        stage('Build JAR') {
            steps {
                sh 'mvn package -DskipTests'
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

                    // Проверяем, установлен ли GitHub CLI
                    sh 'gh --version'

                    // Создаём релиз и загружаем JAR
                    // --notes "Auto-generated release from Jenkins build ${BUILD_NUMBER}"
                    // --title "Release ${version}"
                    sh """
                        gh release create ${tagName} \
                            ${jarPath} \
                            --repo ${REPO_NAME} \
                            --title "Release ${version}" \
                            --notes "Автоматический релиз из Jenkins Build #${BUILD_NUMBER} для ветки ${env.BRANCH_NAME}" \
                            --latest=false
                    """

                    echo "✅ GitHub Release created successfully: https://github.com/${REPO_NAME}/releases/tag/${tagName}"
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