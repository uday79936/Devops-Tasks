pipeline {
    agent { label 'sonar' }

    environment {
        // SonarQube
        SONARQUBE_SERVER = 'sonar'

        // Nexus
        NEXUS_URL       = 'http://54.152.171.119:8081'
        NEXUS_REPO      = 'raw-releases'
        NEXUS_GROUP     = 'com/web/pomodoro'
        NEXUS_ARTIFACT  = 'pomodoro-app'

        // Nginx Deployment
        NGINX_WEB_ROOT  = '/var/www/html'
    }

    stages {
        /* === Stage 1: Checkout Code === */
        stage('Checkout Code') {
            steps {
                echo '📦 Cloning source from GitHub...'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/ashuvee/pomodoro-app-js.git' // Fixed trailing space
                    ]]
                ])
            }
        }

        /* === Stage 2: Install Dependencies === */
        stage('Install Dependencies') {
            steps {
                echo '📥 Installing npm dependencies...'
                sh 'npm ci || npm install'  // Prefer `npm ci` for reproducible builds
                echo '✅ Dependencies installed!'
            }
        }

        /* === Stage 3: Run Tests (Before Sonar) === */
        stage('Run Tests') {
            steps {
                echo '🧪 Running tests and generating coverage...'
                sh 'npm test'
                // Ensure coverage/lcov.info exists for Sonar
            }
        }

        /* === Stage 4: SonarQube Analysis === */
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Check if src/ exists; if not, adjust sonar.sources
                    def srcExists = sh(script: '[ -d "src" ] && echo true || echo false', returnStdout: true).trim()
                    def sonarSources = srcExists == 'true' ? 'src' : '.'
                    
                    echo "🔍 Using SonarQube sources directory: ${sonarSources}"
                    
                    sh 'npm install -g sonarqube-scanner || npm install --save-dev sonarqube-scanner'
                    
                    withSonarQubeEnv("${SONARQUBE_SERVER}") {
                        sh """
                            npx sonar-scanner \\
                              -Dsonar.projectKey=pomodoro-app-js \\
                              -Dsonar.projectName="Pomodoro App JS" \\
                              -Dsonar.projectVersion=0.0.\${BUILD_NUMBER} \\
                              -Dsonar.sources=${sonarSources} \\
                              -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info \\
                              -Dsonar.sourceEncoding=UTF-8
                        """
                    }
                }
            }
        }

        /* === Stage 5: Build Artifact === */
        stage('Build Artifact') {
            steps {
                echo '⚙️ Building application...'
                sh 'npm run build'
                echo '✅ Build completed!'
                sh 'ls -lh dist/ || echo "⚠️ dist/ directory is empty or missing"'
            }
        }

        /* === Stage 6: Package Artifact === */
        stage('Package Artifact') {
            steps {
                sh '''
                    VERSION="0.0.${BUILD_NUMBER}"
                    TARBALL="${NEXUS_ARTIFACT}-${VERSION}.tar.gz"
                    tar -czf "$TARBALL" -C dist .
                    echo "✅ Package created: $TARBALL"
                    ls -lh "$TARBALL"
                '''
            }
        }

        /* === Stage 7: Upload Artifact to Nexus === */
        stage('Upload Artifact to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
                    sh '''#!/bin/bash
                        set -e
                        VERSION="0.0.${BUILD_NUMBER}"
                        TARBALL="${NEXUS_ARTIFACT}-${VERSION}.tar.gz"
                        UPLOAD_URL="${NEXUS_URL}/repository/${NEXUS_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT}/${VERSION}/${TARBALL}"

                        echo "📤 Uploading $TARBALL to Nexus: $UPLOAD_URL"
                        curl -f -u "${NEXUS_USR}:${NEXUS_PSW}" --upload-file "$TARBALL" "$UPLOAD_URL"
                        echo "✅ Artifact uploaded successfully to Nexus!"
                    '''
                }
            }
        }

        /* === Stage 8: Deploy to Nginx === */
        stage('Deploy to Nginx') {
            agent { label 'nginx' }  // Changed from 'tomcat' for clarity (use appropriate agent label)
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
                    sh '''#!/bin/bash
                        set -e
                        VERSION="0.0.${BUILD_NUMBER}"
                        TARBALL="${NEXUS_ARTIFACT}-${VERSION}.tar.gz"
                        DOWNLOAD_URL="${NEXUS_URL}/repository/${NEXUS_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT}/${VERSION}/${TARBALL}"
                        WEB_ROOT="${NGINX_WEB_ROOT}"

                        echo "⬇️ Downloading $TARBALL from Nexus..."
                        curl -f -u "${NEXUS_USR}:${NEXUS_PSW}" -o "/tmp/$TARBALL" "$DOWNLOAD_URL"

                        if [ ! -f "/tmp/$TARBALL" ]; then
                            echo "❌ Tarball not found after download!"
                            exit 1
                        fi

                        echo "🚀 Deploying to Nginx at $WEB_ROOT..."
                        sudo mkdir -p "$WEB_ROOT"
                        sudo rm -rf "$WEB_ROOT"/*
                        sudo tar -xzf "/tmp/$TARBALL" -C "$WEB_ROOT"
                        sudo chown -R www-data:www-data "$WEB_ROOT"
                        sudo chmod -R 755 "$WEB_ROOT"

                        echo "✅ Deployment successful! App is live on Nginx."
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully — Application is live on Nginx!'
        }
        failure {
            echo '❌ Pipeline failed — Please check Jenkins console output for details.'
        }
    }
}