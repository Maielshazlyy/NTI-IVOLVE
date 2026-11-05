@Library('ivolve-lab22') _

pipeline {
    agent { label 'lab22-agent' }

    options {
        timestamps()
    }

    environment {
        IMAGE_REPO = 'maielshazly/ivolve-app'
    }

    stages {
        stage('Build App') {
            steps {
                BuildApp()
            }
        }

        stage('Build, Scan, Push Image') {
            steps {
                BuildImage(
                    imageRepo: env.IMAGE_REPO,
                    credentialsId: 'dockerhub-creds'
                )
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                DeployOnK8s(
                    imageRepo: env.IMAGE_REPO,
                    namespace: 'ivolve',
                    deploymentName: 'ivolveapp',
                    kubeconfigCredentialsId: 'k8s-kubeconfig'
                )
            }
        }
    }
}
