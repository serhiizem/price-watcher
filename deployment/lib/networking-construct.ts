import {Construct} from "constructs";
import {IpAddresses, IVpc, SubnetType, Vpc} from "aws-cdk-lib/aws-ec2";

export class NetworkingConstruct extends Construct {
    public readonly vpc: IVpc;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.vpc = new Vpc(this, "DeploymentVPC", {
            ipAddresses: IpAddresses.cidr("10.0.0.0/16"),
            maxAzs: 1,
            subnetConfiguration: [
                {
                    subnetType: SubnetType.PUBLIC,
                    name: "Public",
                    cidrMask: 24,
                },
                {
                    cidrMask: 24,
                    name: "Private",
                    subnetType: SubnetType.PRIVATE_ISOLATED,
                },
            ],
        });
    }
}
