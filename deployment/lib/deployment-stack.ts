import {Stack, StackProps} from "aws-cdk-lib";
import {Construct} from "constructs";
import {NetworkingConstruct} from "./constructs/networking-construct";
import {K8sConstruct} from "./constructs/k8s-construct";
import {ArgoHelmConstruct} from "./constructs/argo-helm-construct";

export class DeploymentStack extends Stack {

    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const networking = new NetworkingConstruct(this, "NetworkingConstruct");
        const k8s = new K8sConstruct(this, "K8sConstruct", {vpc: networking.vpc});

        new ArgoHelmConstruct(this, "ArgoHelmConstruct", {cluster: k8s.cluster});
    }
}