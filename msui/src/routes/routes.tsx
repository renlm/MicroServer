import { isTruthy } from "@grafana/data";
import config from "../core/config";
import { RouteDescriptor } from "../core/navigation/types";
import { SafeDynamicImport } from "../components/DynamicImports/SafeDynamicImport";
import { PageNotFound } from "../components/PageNotFound/PageNotFound";

const isDevEnv = config.buildInfo.env === "development";
export const extraRoutes: RouteDescriptor[] = [];

export function getAppRoutes(): RouteDescriptor[] {
  return [
    // LOGIN / SIGNUP
    {
      path: "/login",
      component: SafeDynamicImport(
        () => import("../components/Login/LoginPage")
      ),
      pageClass: "login-page",
      chromeless: true,
    },
    {
      path: "/*",
      component: PageNotFound,
    },
  ].filter(isTruthy);
}
