#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { DeploymentStack } from '../lib/deployment-stack';

const app = new cdk.App();

new DeploymentStack(app, "AppStack", {
    env: "app"
});

new DeploymentStack(app, "CicdStack", {
    env: "cicd"
});