import { Location } from "history";
import { ComponentType } from "react";
import { UrlQueryMap } from "@grafana/data";

export interface RouteDescriptor {
  path: string;
  component: MsuiRouteComponent;
  roles?: () => string[];
  pageClass?: string;
  routeName?: string;
  chromeless?: boolean;
  sensitive?: boolean;
}

export interface MsuiRouteComponentProps<T extends {} = {}, Q = UrlQueryMap> {
  route: RouteDescriptor;
  queryParams: Q;
  location: Location;
}

export type MsuiRouteComponent<T extends {} = any> = ComponentType<
  MsuiRouteComponentProps<T>
>;
