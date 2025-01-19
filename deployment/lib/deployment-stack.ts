import {Stack, StackProps} from "aws-cdk-lib";
import {Construct} from "constructs";
import {NetworkingConstruct} from "./constructs/networking-construct";
import {K8sConstruct} from "./constructs/k8s-construct";
import {ArgoConstruct} from "./constructs/argo-construct";
import {JenkinsConstruct} from "./constructs/jenkins-construct";
import {ClusterAuth} from "./access/cluster-auth";
import {User} from "aws-cdk-lib/aws-iam";
import {AppConfig} from "./config/app-config";
import {MonitoringConstruct} from "./constructs/monitoring-construct";
import {DynamodbConstruct} from "./constructs/dynamodb-construct";

export class DeploymentStack extends Stack {

    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const {vpc} = new NetworkingConstruct(this, "NetworkingConstruct");
        const {cluster} = new K8sConstruct(this, "K8sConstruct", {vpc});
        const {instance: jenkins} = new JenkinsConstruct(this, "JenkinsConstruct", {vpc});
        jenkins.node.addDependency(cluster);

        const deployer = User.fromUserName(this, "CdkUser", AppConfig.cdkUsername);
        ClusterAuth.of(cluster)
            .allowAccessToInstance(jenkins)
            .allowAccessToUser(deployer);

        new ArgoConstruct(this, "ArgoHelmConstruct", {cluster});
        new MonitoringConstruct(this, "MonitoringConstruct", {cluster});
        new DynamodbConstruct(this, "DynamoDbConstruct");
    }
}