import { resolve as _resolve } from "path";
import webpack from "webpack";
import HtmlWebpackPlugin from "html-webpack-plugin";

export default (envConfig) => {
  return {
    entry: {
      app: "./src/index.ts",
    },
    watchOptions: {
      ignored: /node_modules/,
    },
    plugins: [
      new webpack.EnvironmentPlugin(envConfig),
      new HtmlWebpackPlugin({
        title: envConfig.title,
      }),
    ],
    resolve: {
      extensions: [".ts", ".tsx", ".es6", ".js", ".json", ".svg"],
    },
    output: {
      clean: true,
      filename: "[name].[contenthash].js",
      path: _resolve("./dist"),
      publicPath: envConfig.server.path,
    },
    module: {
      rules: [
        {
          test: /\.css$/i,
          use: ["style-loader", "css-loader"],
        },
        {
          test: /\.tsx?$/,
          use: "ts-loader",
          exclude: /node_modules/,
        },
      ],
    },
  };
};
