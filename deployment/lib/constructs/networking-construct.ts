import {Construct} from "constructs";
import {IpAddresses, IVpc, SubnetType, Vpc} from "aws-cdk-lib/aws-ec2";

export class NetworkingConstruct extends Construct {
    public readonly vpc: IVpc;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.vpc = new Vpc(this, "DeploymentVPC", {
            ipAddresses: IpAddresses.cidr("10.0.0.0/16"),
            maxAzs: 2,
            subnetConfiguration: [
                {
                    subnetType: SubnetType.PUBLIC,
                    name: "PublicSubnet",
                    cidrMask: 24
                },
                {
                    subnetType: SubnetType.PRIVATE_WITH_EGRESS,
                    name: "PrivateSubnet",
                    cidrMask: 24
                }
            ]
        });
    }
}
