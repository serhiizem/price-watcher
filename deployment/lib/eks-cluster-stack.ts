import {Stack, StackProps} from "aws-cdk-lib";
import {InstanceClass, InstanceSize, InstanceType, IpAddresses, SubnetType, Vpc} from "aws-cdk-lib/aws-ec2";
import {Cluster, IpFamily, KubernetesVersion, NodegroupAmiType} from "aws-cdk-lib/aws-eks";
import {KubectlV29Layer} from "@aws-cdk/lambda-layer-kubectl-v29";
import {ManagedPolicy, Role, ServicePrincipal} from "aws-cdk-lib/aws-iam";
import {Construct} from "constructs";

export class EKSClusterStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const vpc = new Vpc(this, "EksVPC", {
            ipAddresses: IpAddresses.cidr("10.0.0.0/16"),
            maxAzs: 2,
            subnetConfiguration: [
                {
                    subnetType: SubnetType.PUBLIC,
                    name: "PublicSubnet",
                    cidrMask: 24,
                },
                {
                    subnetType: SubnetType.PRIVATE_WITH_EGRESS,
                    name: "PrivateSubnet",
                    cidrMask: 24,
                },
            ],
        });

        const eksCluster = new Cluster(this, "EKSCluster", {
            vpc,
            defaultCapacity: 0,
            version: KubernetesVersion.V1_29,
            kubectlLayer: new KubectlV29Layer(this, "kubectl"),
            ipFamily: IpFamily.IP_V4,
            outputClusterName: true,
            outputConfigCommand: true
        });

        eksCluster.addNodegroupCapacity("EksNodeGroup", {
            amiType: NodegroupAmiType.AL2_X86_64,
            instanceTypes: [InstanceType.of(InstanceClass.T2, InstanceSize.MICRO)],
            desiredSize: 2,
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