import { merge } from "webpack-merge";
import { BundleAnalyzerPlugin } from "webpack-bundle-analyzer";
import common from "./webpack.common.js";
import getEnvConfig from "./env-util.js";
const envConfig = getEnvConfig(process.env.NODE_ENV);
const withReport = process.env.npm_config_withReport;

export default (env = {}) => {
  console.log(JSON.stringify(envConfig, (k, v) => v, 2));
  return merge(common(envConfig), {
    mode: "development",
    devtool: "inline-source-map",
    devServer: {
      port: "auto",
      open: [envConfig.server.path],
      devMiddleware: {
        index: true,
        mimeTypes: { phtml: "text/html" },
        publicPath: envConfig.server.path,
        serverSideRender: true,
        writeToDisk: true,
      },
    },
    plugins: [
      new BundleAnalyzerPlugin({
        analyzerMode: withReport ? "server" : "disabled",
        analyzerPort: "auto",
      }),
    ],
  });
};
