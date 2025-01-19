import {Construct} from "constructs";
import {AttributeType, BillingMode, Table} from "aws-cdk-lib/aws-dynamodb";
import {RemovalPolicy} from "aws-cdk-lib";

export class DynamodbConstruct extends Construct {

    constructor(scope: Construct, id: string) {
        super(scope, id);

        new Table(this, "SubscriptionsDynamoDbTable", {
            partitionKey: {name: "symbol", type: AttributeType.STRING},
            tableName: "Subscriptions",
            billingMode: BillingMode.PAY_PER_REQUEST,
            removalPolicy: RemovalPolicy.DESTROY
        });
    }
}