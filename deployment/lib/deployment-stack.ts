import {Stack, StackProps} from "aws-cdk-lib";
import {Construct} from "constructs";
import {NetworkingConstruct} from "./constructs/networking-construct";
import {K8sConstruct} from "./constructs/k8s-construct";
import {ArgoHelmConstruct} from "./constructs/argo-helm-construct";
import {JenkinsConstruct} from "./constructs/jenkins-construct";

export class DeploymentStack extends Stack {

    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const {vpc} = new NetworkingConstruct(this, "NetworkingConstruct");
        const k8s = new K8sConstruct(this, "K8sConstruct", {vpc});
        new JenkinsConstruct(this, "JenkinsConstruct", {vpc});

        new ArgoHelmConstruct(this, "ArgoHelmConstruct", {cluster: k8s.cluster});
    }
}