pipeline {
    agent any

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'qa', 'prod'], description: 'Deployment Environment Profile')
        string(name: 'PORT', defaultValue: '8085', description: 'Server Port for Deployment')
        booleanParam(name: 'DEPLOY_SERVER', defaultValue: true, description: 'Deploy application server after build')
    }

    environment {
        APP_NAME = 'farmer-registration-platform'
        APP_VERSION = '1.0.0'
        JAR_NAME = "${APP_NAME}-${APP_VERSION}.jar"
        JAR_PATH = "target/${JAR_NAME}"
        DEPLOY_DIR = "${WORKSPACE}/deploy"
        LOGS_DIR = "${WORKSPACE}/logs"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo "======================================================================"
                echo ">>> [STAGE: CHECKOUT] Checking out Git Repository from SCM..."
                echo "======================================================================"
                checkout scm
                script {
                    if (isUnix()) {
                        sh '''
                            echo "Git Commit  : $(git rev-parse --short HEAD)"
                            echo "Git Branch  : $(git rev-parse --abbrev-ref HEAD)"
                            echo "Committed By: $(git log -1 --pretty=format:'%an <%ae>')"
                            echo "Commit Msg  : $(git log -1 --pretty=format:'%s')"
                        '''
                    } else {
                        bat '''
                            @echo off
                            echo Git Commit :
                            git rev-parse --short HEAD
                            echo Git Branch :
                            git rev-parse --abbrev-ref HEAD
                            echo Commit Msg :
                            git log -1 --pretty=format:"%%s"
                        '''
                    }
                }
            }
        }

        stage('Environment & Tools Setup') {
            steps {
                echo "======================================================================"
                echo ">>> [STAGE: SETUP] Validating Build Environment & Tools..."
                echo "======================================================================"
                echo "Target Environment : ${params.ENVIRONMENT}"
                echo "Target Port        : ${params.PORT}"
                echo "Deploy Server Flag : ${params.DEPLOY_SERVER}"
                script {
                    if (isUnix()) {
                        sh '''
                            java -version
                            mvn -version || C:\\DevTools\\apache-maven-3.9.16\\bin\\mvn.cmd -version
                        '''
                    } else {
                        bat '''
                            @echo off
                            java -version
                            call mvn -version 2>nul || "C:\\DevTools\\apache-maven-3.9.16\\bin\\mvn.cmd" -version
                        '''
                    }
                }
            }
        }

        stage('Compile & Test') {
            steps {
                echo "======================================================================"
                echo ">>> [STAGE: TEST] Compiling Code & Executing Automated Unit Tests..."
                echo "======================================================================"
                script {
                    if (isUnix()) {
                        sh 'mvn clean test'
                    } else {
                        bat '''
                            @echo off
                            call mvn clean test || "C:\\DevTools\\apache-maven-3.9.16\\bin\\mvn.cmd" clean test
                        '''
                    }
                }
            }
        }

        stage('Package Executable JAR') {
            steps {
                echo "======================================================================"
                echo ">>> [STAGE: PACKAGE] Packaging Spring Boot Fat JAR..."
                echo "======================================================================"
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests=true'
                        sh "ls -lh target/*.jar"
                    } else {
                        bat '''
                            @echo off
                            call mvn package -DskipTests=true || "C:\\DevTools\\apache-maven-3.9.16\\bin\\mvn.cmd" package -DskipTests=true
                            dir target\\*.jar
                        '''
                    }
                }
            }
        }

        stage('Deploy to Embedded Server') {
            when {
                expression { return params.DEPLOY_SERVER == true }
            }
            steps {
                echo "======================================================================"
                echo ">>> [STAGE: DEPLOY] Deploying to Embedded Tomcat (${params.ENVIRONMENT.toUpperCase()} Profile)..."
                echo "======================================================================"
                script {
                    if (isUnix()) {
                        sh "chmod +x ./scripts/deploy.sh ./scripts/stop-app.sh"
                        sh "./scripts/deploy.sh ${params.ENVIRONMENT} ${params.PORT} target/${JAR_NAME} deploy"
                    } else {
                        powershell """
                            Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
                            .\\scripts\\deploy.ps1 -Environment "${params.ENVIRONMENT}" -Port ${params.PORT} -JarPath "target/${JAR_NAME}" -DeployDir "deploy"
                        """
                    }
                }
            }
        }

        stage('Smoke Test & Health Probe') {
            when {
                expression { return params.DEPLOY_SERVER == true }
            }
            steps {
                echo "======================================================================"
                echo ">>> [STAGE: SMOKE TEST] Validating Deployed Services and Health Probes..."
                echo "======================================================================"
                script {
                    if (isUnix()) {
                        sh """
                            echo "Verifying Health Endpoint:"
                            curl -s -i "http://localhost:${params.PORT}/actuator/health"
                            echo "\\nVerifying Training Catalog API:"
                            curl -s -i "http://localhost:${params.PORT}/api/trainings"
                            echo "\\nVerifying Portal Homepage:"
                            curl -s -o /dev/null -w "%{http_code}" "http://localhost:${params.PORT}/"
                        """
                    } else {
                        powershell """
                            \$health = Invoke-RestMethod -Uri "http://localhost:${params.PORT}/actuator/health" -Method Get
                            Write-Host "Health Status: \$(\$health.status)"
                            if (\$health.status -ne 'UP') { throw 'Health check failed!' }

                            \$trainings = Invoke-RestMethod -Uri "http://localhost:${params.PORT}/api/trainings" -Method Get
                            Write-Host "Loaded Trainings: \$(\$trainings.Count) modules available"

                            \$page = Invoke-WebRequest -Uri "http://localhost:${params.PORT}/" -Method Get
                            Write-Host "Portal Status Code: \$(\$page.StatusCode)"
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            echo "======================================================================"
            echo ">>> [POST-BUILD] Recording Test Results..."
            echo "======================================================================"
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
        success {
            echo "======================================================================"
            echo ">>> [PIPELINE SUCCESS] Pipeline as Code Execution Finished Successfully!"
            echo ">>> Deployed Portal URL   : http://localhost:${params.PORT}/"
            echo ">>> Coordinator Admin URL : http://localhost:${params.PORT}/admin"
            echo ">>> Healthcheck Endpoint  : http://localhost:${params.PORT}/actuator/health"
            echo ">>> Active Profile        : ${params.ENVIRONMENT}"
            echo "======================================================================"
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
        }
        failure {
            echo "======================================================================"
            echo ">>> [PIPELINE FAILURE] Pipeline failed! Review logs for error diagnosis."
            echo "======================================================================"
        }
    }
}
