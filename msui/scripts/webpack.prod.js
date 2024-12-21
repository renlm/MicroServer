import { merge } from "webpack-merge";
import common from "./webpack.common.js";
import getEnvConfig from "./env-util.js";
const envConfig = getEnvConfig(process.env.NODE_ENV);

export default (env = {}) =>
  merge(common(envConfig), {
    mode: "production",
    devtool: "source-map",
  });
