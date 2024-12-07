import {Construct} from 'constructs';
import {InstanceConstruct} from "./instance-construct";
import {NetworkingConstruct} from "./networking-construct";
import {Stack, StackProps, Tags} from 'aws-cdk-lib';
import {
    InstanceClass,
    InstanceSize,
    InstanceType,
    MachineImage,
    OperatingSystemType,
    SubnetType
} from "aws-cdk-lib/aws-ec2";
import {readScript} from "./utils/fileUtils";

type DeploymentStackProps = StackProps & {
    type: "app" | "cicd"
}

export class DeploymentStack extends Stack {
    constructor(scope: Construct, id: string, props: DeploymentStackProps) {
        super(scope, id, props);

        const instanceConstruct = this.createInstance(props);
        Tags.of(instanceConstruct).add("Module", "Deployment");
    }

    private createInstance(props: DeploymentStackProps) {
        const networkingConstruct = new NetworkingConstruct(this, "NetworkingConstruct");

        const deploymentType = props.type;
        if ("app" === deploymentType) {
            return new InstanceConstruct(
                this,
                "AppInstanceConstruct",
                {
                    instanceName: "AppEc2Instance",
                    instanceType: InstanceType.of(InstanceClass.T2, InstanceSize.MICRO),
                    ami: MachineImage.latestAmazonLinux2(),
                    vpc: networkingConstruct.vpc,
                    subnetType: SubnetType.PUBLIC,
                    exposedPorts: [22, 3500, 9100],
                    setupScripts: [readScript("aws-linux-instance-setup.sh")]
                }
            );
        }
        if ("cicd" === deploymentType) {
            return new InstanceConstruct(
                this,
                "JenkinsInstanceConstruct",
                {
                    instanceName: "JenkinsEc2Instance",
                    instanceType: InstanceType.of(InstanceClass.T2, InstanceSize.MICRO),
                    ami: MachineImage.fromSsmParameter(
                        '/aws/service/canonical/ubuntu/server/focal/stable/current/amd64/hvm/ebs-gp2/ami-id',
                        {os: OperatingSystemType.LINUX},
                    ),
                    vpc: networkingConstruct.vpc,
                    subnetType: SubnetType.PUBLIC,
                    exposedPorts: [22, 8080, 9000],
                    setupScripts: [readScript("ubuntu-jenkins-instance-setup.sh")]
                }
            );
        }
        return new Construct(this, "None");
    }
}
