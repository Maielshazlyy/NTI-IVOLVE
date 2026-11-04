def call(Map config = [:]) {
    def imageRepo = config.imageRepo ?: "maielshazly/ivolve-app"
    def namespace = config.namespace ?: "ivolve"
    def deploymentName = config.deploymentName ?: "ivolveapp"
    def kubeconfigCredentialsId = config.kubeconfigCredentialsId ?: "k8s-kubeconfig"
    def imageTag = "${imageRepo}:${env.BUILD_NUMBER}"

    withCredentials([
        file(
            credentialsId: kubeconfigCredentialsId,
            variable: "KUBECONFIG_FILE"
        )
    ]) {
        sh """
            set -eu
            sed -i 's|image: ${imageRepo}:.*|image: ${imageTag}|' k8s/deployment.yaml
            export KUBECONFIG="\$KUBECONFIG_FILE"
            kubectl apply -f k8s/deployment.yaml
            kubectl apply -f k8s/service.yaml
            kubectl rollout status deployment/${deploymentName} -n ${namespace} --timeout=180s
            kubectl get deployment,pods,service -n ${namespace}
        """
    }
}
