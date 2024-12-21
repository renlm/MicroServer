import { readFileSync } from "fs";
import { parse as _parse } from "yaml";
import { merge } from "webpack-merge";

export default (env) => {
  const defaultSettings = readFileSync("./conf/defaults.yaml", "utf8");
  const customSettings = readFileSync(`./conf/${env}.yaml`, "utf8");

  const defaults = _parse(defaultSettings);
  const custom = _parse(customSettings);

  return merge(defaults, custom);
};
