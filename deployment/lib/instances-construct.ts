import {Construct} from "constructs";
import {
    IMachineImage,
    Instance,
    InstanceClass,
    InstanceSize,
    InstanceType,
    IVpc,
    MachineImage,
    Peer,
    Port,
    SecurityGroup,
} from "aws-cdk-lib/aws-ec2";
import * as fs from "fs";
import {CfnOutput} from "aws-cdk-lib";
import {ManagedPolicy, Role, ServicePrincipal} from "aws-cdk-lib/aws-iam";
import {SubnetSelection} from "aws-cdk-lib/aws-ec2/lib/vpc";

type DeploymentInstanceProps = {
    vpc: IVpc;
};

export class InstancesConstruct extends Construct {
    constructor(scope: Construct, id: string, props: DeploymentInstanceProps) {
        super(scope, id);

        const {vpc} = props;
        const subnets = props.vpc.selectSubnets({subnetGroupName: "Public"});

        const role = this.createRole();
        const securityGroup = this.createSecurityGroup(props.vpc);
        const instanceType = InstanceType.of(InstanceClass.T2, InstanceSize.MICRO);

        const linuxMachineImage = MachineImage.latestAmazonLinux2();
        const awsLinuxInstance = this.createInstance(
            "LinuxEc2Instance",
            vpc,
            subnets,
            role,
            securityGroup,
            instanceType,
            linuxMachineImage
        );

        const linuxSetupScript = fs.readFileSync(
            "./scripts/aws-linux-instance-setup.sh",
            "utf8"
        );
        awsLinuxInstance.addUserData(linuxSetupScript);

        new CfnOutput(this, "AwsLinuxInstanceIp", {
            value: `http://${awsLinuxInstance.instancePublicIp}/`,
            exportName: "AwsLinuxInstanceIp",
        });
    }

    private createRole(): Role {
        return new Role(this, "InstanceRole", {
            assumedBy: new ServicePrincipal("ec2.amazonaws.com"),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("AmazonSSMFullAccess"),
            ],
        });
    }

    private createSecurityGroup(vpc: IVpc): SecurityGroup {
        const securityGroup = new SecurityGroup(this, "InstanceSecurityGroup", {
            vpc,
            allowAllOutbound: true,
        });

        const exposedPorts: number[] = [];
        exposedPorts.push(22, 3500); // ports for SSH access, REST resources
        exposedPorts.push(9100); // Linux OS monitoring port

        exposedPorts.forEach((port) =>
            securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(port))
        );

        return securityGroup;
    }

    private createInstance(
        instanceName: string,
        vpc: IVpc,
        vpcSubnets: SubnetSelection,
        role: Role,
        securityGroup: SecurityGroup,
        instanceType: InstanceType,
        machineImage: IMachineImage
    ): Instance {
        const keyName = "cdk-key-pair";
        return new Instance(this, instanceName, {
            vpc,
            vpcSubnets,
            role,
            securityGroup,
            instanceType,
            keyName,
            machineImage,
        });
    }
}
