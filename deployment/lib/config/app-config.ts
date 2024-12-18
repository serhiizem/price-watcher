import * as dotenv from "dotenv";
import * as path from "node:path";

dotenv.config({path: path.resolve(__dirname, "../../../.env")});

export const AppConfig = {
    cdkUsername: process.env.CDK_USERNAME as string
}