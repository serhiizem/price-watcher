import {Construct} from 'constructs';
import {InstanceConstruct} from "./instance-construct";
import {NetworkingConstruct} from "./networking-construct";
import {Stack, StackProps, Tags} from 'aws-cdk-lib';
import {InstanceClass, InstanceSize, InstanceType, MachineImage} from "aws-cdk-lib/aws-ec2";
import {readScript} from "./utils/fileUtils";

export class DeploymentStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const networkingConstruct = new NetworkingConstruct(this, "NetworkingConstruct");

        const appInstanceConstruct = new InstanceConstruct(
            this,
            "AppInstanceConstruct",
            {
                instanceName: "AppEc2Instance",
                instanceType: InstanceType.of(InstanceClass.T2, InstanceSize.MICRO),
                ami: MachineImage.latestAmazonLinux2(),
                vpc: networkingConstruct.vpc,
                exposedPorts: [22, 3500, 9100],
                setupScript: readScript("aws-linux-instance-setup.sh")
            }
        );

        Tags.of(appInstanceConstruct).add("Module", "Deployment");
    }
}
