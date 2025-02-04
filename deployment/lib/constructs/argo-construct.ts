import {Construct} from "constructs";
import {Cluster} from "aws-cdk-lib/aws-eks";
import {Duration} from "aws-cdk-lib";

interface ArgoConstructProps {
    cluster: Cluster
}

export class ArgoConstruct extends Construct {

    constructor(scope: Construct, id: string, {cluster}: ArgoConstructProps) {
        super(scope, id);

        const argoNamespace = cluster.addManifest("argocd-namespace", {
            apiVersion: "v1",
            kind: "Namespace",
            metadata: {
                name: "argocd",
            },
        });

        const argoIngress = cluster.addHelmChart("argocd-ingress", {
            chart: "argo-cd",
            repository: "https://argoproj.github.io/argo-helm",
            namespace: "argocd",
            release: "argo-cd",
            wait: true,
            version: "7.7.7",
            createNamespace: false,
            timeout: Duration.minutes(15),
            values: {
                server: {
                    service: {
                        type: "LoadBalancer"
                    }
                }
            }
        });

        argoIngress.node.addDependency(argoNamespace);
    }
}