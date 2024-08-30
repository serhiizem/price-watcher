import {Construct} from 'constructs';
import {InstanceConstruct} from "./instance-construct";
import {NetworkingConstruct} from "./networking-construct";
import {Stack, StackProps, Tags} from 'aws-cdk-lib';
import {InstanceClass, InstanceSize, InstanceType, IVpc, MachineImage} from "aws-cdk-lib/aws-ec2";
import {readScript} from "./utils/fileUtils";

type DeploymentStackProps = StackProps & {
    env: "app" | "cicd"
}

export class DeploymentStack extends Stack {
    constructor(scope: Construct, id: string, props: DeploymentStackProps) {
        super(scope, id, props);

        const instanceConstruct = this.createInstance(props);
        Tags.of(instanceConstruct).add("Module", "Deployment");
    }

    private createInstance(props: DeploymentStackProps) {
        const networkingConstruct = new NetworkingConstruct(this, "NetworkingConstruct");

        if ("app" === props.env) {
            return new InstanceConstruct(
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
        }
        return new Construct(this, "None");
    }
}
