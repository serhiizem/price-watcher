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

interface JenkinsConstructProps {
    vpc: IVpc;
}

export class JenkinsConstruct extends Construct {

    public readonly instance: Instance;

    constructor(scope: Construct, id: string, props: JenkinsConstructProps) {
        super(scope, id);

        const {instance} = new InstanceConstruct(
            this,
            "JenkinsInstanceConstruct",
            {
                instanceName: "JenkinsEc2Instance",
                instanceType: InstanceType.of(InstanceClass.T2, InstanceSize.MICRO),
                ami: MachineImage.fromSsmParameter(
                    '/aws/service/canonical/ubuntu/server/focal/stable/current/amd64/hvm/ebs-gp2/ami-id',
                    {os: OperatingSystemType.LINUX},
                ),
                vpc: props.vpc,
                subnetType: SubnetType.PUBLIC,
                exposedPorts: [22, 80, 9000],
                setupScripts: [readScript("ubuntu-jenkins-instance-setup.sh")]
            }
        );
        this.instance = instance;
    }
}