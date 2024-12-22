import {Construct} from "constructs";
import {Cluster} from "aws-cdk-lib/aws-eks";
import {Duration} from "aws-cdk-lib";

interface MonitoringConstructProps {
    cluster: Cluster
}

export class MonitoringConstruct extends Construct {

    constructor(scope: Construct, id: string, {cluster}: MonitoringConstructProps) {
        super(scope, id);

        const monitoringNamespace = cluster.addManifest("monitoring-namespace", {
            apiVersion: "v1",
            kind: "Namespace",
            metadata: {name: "monitoring"},
        });

        const promIngress = cluster.addHelmChart("prom-ingress", {
            chart: "kube-prometheus-stack",
            repository: "https://prometheus-community.github.io/helm-charts",
            namespace: "monitoring",
            release: "kube-prometheus-stack",
            wait: true,
            version: "67.4.0",
            createNamespace: false,
            timeout: Duration.minutes(15),
            values: {
                prometheus: {
                    service: {
                        type: "LoadBalancer"
                    },
                    replicaCount: 1
                },
                grafana: {
                    service: {
                        type: "LoadBalancer",
                        port: 3000
                    },
                    adminPassword: "admin",
                    replicaCount: 1
                },
                alertmanager: {
                    enabled: false
                },
                kubeStateMetrics: {
                    enabled: false
                },
                prometheusNodeExporter: {
                    enabled: false
                },
                prometheusPushgateway: {
                    enabled: false
                }
            }
        });

        promIngress.node.addDependency(monitoringNamespace);
    }
}