import {Construct} from 'constructs';
import {InstancesConstruct} from "./instances-construct";
import {NetworkingConstruct} from "./networking-construct";
import {Stack, StackProps, Tags} from 'aws-cdk-lib';

export class DeploymentStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const networkingStack = new NetworkingConstruct(
            this,
            "NetworkingConstruct",
            {
                maxAzs: 1,
            }
        );

        const instanceStack = new InstancesConstruct(
            this,
            "DeploymentInstanceConstruct",
            {
                vpc: networkingStack.vpc,
            }
        );

        Tags.of(instanceStack).add("Module", "Deployment");
    }
}
