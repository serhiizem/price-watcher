import {Construct} from "constructs";
import {InstanceConstruct} from "./instance-construct";
import {
    Instance,
    InstanceClass,
    InstanceSize,
    InstanceType,
    IVpc,
    MachineImage,
    OperatingSystemType,
    SubnetType
} from "aws-cdk-lib/aws-ec2";
import {readScript} from "../utils/fileUtils";
import {Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal} from "aws-cdk-lib/aws-iam";
import {IManagedPolicy} from "aws-cdk-lib/aws-iam/lib/managed-policy";

interface JenkinsConstructProps {
    vpc: IVpc;
}

export class JenkinsConstruct extends Construct {

    public readonly instance: Instance;

    constructor(scope: Construct, id: string, props: JenkinsConstructProps) {
        super(scope, id);

        const ssmPolicy: IManagedPolicy = this.createSSMPolicy();
        const eksReadOnlyPolicy: IManagedPolicy = this.createEksReadOnlyAccessPolicy();

        const instanceRole = new Role(this, "JenkinsInstanceRole", {
            assumedBy: new ServicePrincipal("ec2.amazonaws.com"),
            managedPolicies: [ssmPolicy, eksReadOnlyPolicy]
        });

        const {instance} = new InstanceConstruct(
            this,
            "JenkinsInstanceConstruct",
            {
                instanceName: "JenkinsEc2Instance",
                instanceType: InstanceType.of(InstanceClass.T2, InstanceSize.MEDIUM),
                ami: MachineImage.fromSsmParameter(
                    "/aws/service/canonical/ubuntu/server/focal/stable/current/amd64/hvm/ebs-gp2/ami-id",
                    {os: OperatingSystemType.LINUX}
                ),
                vpc: props.vpc,
                subnetType: SubnetType.PUBLIC,
                role: instanceRole,
                exposedPorts: [22, 8080, 9000],
                setupScripts: [readScript("ubuntu-jenkins-instance-setup.sh")]
            }
        );
        this.instance = instance;
    }

    private createSSMPolicy() {
        return ManagedPolicy.fromAwsManagedPolicyName("AmazonSSMFullAccess");
    }

    private createEksReadOnlyAccessPolicy() {
        const jenkinsHostPolicy = new ManagedPolicy(this, "JenkinsHostManagedPolicy");
        jenkinsHostPolicy.addStatements(new PolicyStatement({
            resources: ["*"],
            actions: [
                "eks:DescribeNodegroup",
                "eks:ListNodegroups",
                "eks:DescribeCluster",
                "eks:ListClusters",
                "eks:AccessKubernetesApi",
                "eks:ListUpdates",
                "eks:ListFargateProfiles"
            ],
            effect: Effect.ALLOW,
            sid: "EKSReadonly"
        }));
        return jenkinsHostPolicy;
    }
}