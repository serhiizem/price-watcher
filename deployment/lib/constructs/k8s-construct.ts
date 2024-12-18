import {Construct} from "constructs";
import {InstanceClass, InstanceSize, InstanceType, IVpc} from "aws-cdk-lib/aws-ec2";
import {Cluster, KubernetesVersion, NodegroupAmiType} from "aws-cdk-lib/aws-eks";
import {KubectlV29Layer} from "@aws-cdk/lambda-layer-kubectl-v29";
import {ManagedPolicy, Role, ServicePrincipal} from "aws-cdk-lib/aws-iam";

interface ClusterConstructProps {
    vpc: IVpc;
}

export class K8sConstruct extends Construct {

    public readonly cluster: Cluster;

    constructor(scope: Construct, id: string, {vpc}: ClusterConstructProps) {
        super(scope, id);

        this.cluster = new Cluster(this, "EksCluster", {
            vpc,
            defaultCapacity: 0,
            version: KubernetesVersion.V1_30,
            kubectlLayer: new KubectlV29Layer(this, "kubectl")
        });

        this.cluster.addNodegroupCapacity("EksNodeGroup", {
            amiType: NodegroupAmiType.AL2_X86_64,
            instanceTypes: [InstanceType.of(InstanceClass.T2, InstanceSize.MEDIUM)],
            desiredSize: 1,
            diskSize: 20,
            nodeRole: new Role(this, "EksClusterNodeGroupRole", {
                roleName: "EksClusterNodeGroupRole",
                assumedBy: new ServicePrincipal("ec2.amazonaws.com"),
                managedPolicies: [
                    ManagedPolicy.fromAwsManagedPolicyName("AmazonEKSWorkerNodePolicy"),
                    ManagedPolicy.fromAwsManagedPolicyName("AmazonEC2ContainerRegistryReadOnly"),
                    ManagedPolicy.fromAwsManagedPolicyName("AmazonEKS_CNI_Policy")
                ]
            })
        });
    }
}