import {Construct} from "constructs";
import {IMachineImage, Instance, InstanceType, IVpc, Peer, Port, SecurityGroup, SubnetType,} from "aws-cdk-lib/aws-ec2";
import {CfnOutput} from "aws-cdk-lib";
import {IRole} from "aws-cdk-lib/aws-iam";

const keyName = "cdk-key-pair";

type DeploymentInstanceProps = {
    instanceName: string;
    instanceType: InstanceType;
    ami: IMachineImage;
    vpc: IVpc;
    subnetType: SubnetType;
    role: IRole;
    exposedPorts: number[];
    setupScripts: string[];
};

export class InstanceConstruct extends Construct {

    public readonly instance: Instance;

    constructor(scope: Construct, id: string, props: DeploymentInstanceProps) {
        super(scope, id);

        const {instanceName, instanceType, ami, vpc, subnetType, role, exposedPorts, setupScripts} = props;
        const vpcSubnets = vpc.selectSubnets({subnetType});
        const securityGroup = this.createSecurityGroup(instanceName, vpc, exposedPorts);
        const instance = new Instance(this, instanceName, {
            vpc, vpcSubnets, role, securityGroup, instanceType, keyName, machineImage: ami
        });
        setupScripts.forEach(script => instance.addUserData(script));

        new CfnOutput(this, `${instanceName}Ip`, {
            value: `http://${instance.instancePublicDnsName}/`,
            exportName: `${instanceName}Host`
        });

        this.instance = instance;
    }

    private createSecurityGroup(instanceName: string, vpc: IVpc, exposedPorts: number[]): SecurityGroup {
        const securityGroup = new SecurityGroup(this, `${instanceName}Sg`, {
            vpc,
            allowAllOutbound: true
        });

        exposedPorts.forEach((port) =>
            securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(port))
        );

        return securityGroup;
    }
}
