import * as dotenv from "dotenv";

dotenv.config();

export const AppConfig = {
    cdkUsername: process.env.CDK_USERNAME as string
}