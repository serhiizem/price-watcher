import {Duration, Stack, StackProps} from "aws-cdk-lib";
import {InstanceClass, InstanceSize, InstanceType, IpAddresses, SubnetType, Vpc} from "aws-cdk-lib/aws-ec2";
import {Cluster, KubernetesVersion, NodegroupAmiType} from "aws-cdk-lib/aws-eks";
import {KubectlV29Layer} from "@aws-cdk/lambda-layer-kubectl-v29";
import {ManagedPolicy, Role, ServicePrincipal, User} from "aws-cdk-lib/aws-iam";
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

        // const jenkinsInstance = new InstanceConstruct(
        //     this,
        //     "JenkinsInstanceConstruct",
        //     {
        //         instanceName: "JenkinsEc2Instance",
        //         instanceType: InstanceType.of(InstanceClass.T2, InstanceSize.MICRO),
        //         ami: MachineImage.fromSsmParameter(
        //             '/aws/service/canonical/ubuntu/server/focal/stable/current/amd64/hvm/ebs-gp2/ami-id',
        //             {os: OperatingSystemType.LINUX},
        //         ),
        //         vpc: vpc,
        //         subnetType: SubnetType.PUBLIC,
        //         exposedPorts: [22, 8080, 9000],
        //         setupScripts: [readScript("ubuntu-jenkins-instance-setup.sh")]
        //     }
        // );
        //
        // Tags.of(jenkinsInstance).add("Module", "Deployment");

        const eksCluster = new Cluster(this, "EKSCluster", {
            vpc,
            defaultCapacity: 0,
            version: KubernetesVersion.V1_30,
            kubectlLayer: new KubectlV29Layer(this, "kubectl")
        });

        eksCluster.addNodegroupCapacity("EksNodeGroup", {
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

        const user = User.fromUserName(this, "CliUser", "serhiizem.cli");
        eksCluster.awsAuth.addUserMapping(user, {
            groups: ["system:masters"],
            username: "serhiizem.cli",
        });

        const argocdNamespace = eksCluster.addManifest('argocd-namespace', {
            apiVersion: 'v1',
            kind: 'Namespace',
            metadata: {
                name: 'argocd',
            },
        });

        const argocdIngress = eksCluster.addHelmChart('argocd-ingress', {
            chart: 'argo-cd',
            repository: 'https://argoproj.github.io/argo-helm',
            namespace: 'argocd',
            wait: true,
            release: 'argo-cd',
            version: '7.7.7',
            createNamespace: false,
            timeout: Duration.minutes(15),
            values: {
                server: {
                    service: {
                        type: 'LoadBalancer'
                    }
                }
            }
        });

        // make sure namespace is deployed before the chart
        argocdIngress.node.addDependency(argocdNamespace);
    }
}