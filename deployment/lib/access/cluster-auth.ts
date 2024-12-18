import {Cluster} from "aws-cdk-lib/aws-eks";
import {Instance, Port} from "aws-cdk-lib/aws-ec2";
import {IUser} from "aws-cdk-lib/aws-iam";

export class ClusterAuth {

    private constructor(private cluster: Cluster) {
    }

    public static of(cluster: Cluster): ClusterAuth {
        return new ClusterAuth(cluster);
    }

    public allowAccessToInstance(instance: Instance): ClusterAuth {
        instance.connections.allowTo(this.cluster, Port.tcp(443))
        this.cluster.awsAuth.addMastersRole(instance.role);
        return this;
    }

    public allowAccessToUser(user: IUser): ClusterAuth {
        this.cluster.awsAuth.addUserMapping(user, {
            groups: ["system:masters"],
            username: user.userName,
        });
        return this;
    }
}