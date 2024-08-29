import * as fs from "fs";

export const readScript = (scriptName: string): string => {
    return fs.readFileSync(`./scripts/${scriptName}`, "utf8");
}