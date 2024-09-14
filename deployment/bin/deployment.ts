#!/usr/bin/env node
import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import {EKSClusterStack} from "../lib/eks-cluster-stack";

const app = new cdk.App();

new EKSClusterStack(app, "EksStack");
//
// new DeploymentStack(app, "AppStack", {
//     type: "app"
// });
//
// new DeploymentStack(app, "CicdStack", {
//     type: "cicd"
// });