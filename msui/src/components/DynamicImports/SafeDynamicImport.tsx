import { lazy } from "react";

import { MsuiRouteComponent } from "../../core/navigation/types";

export const SafeDynamicImport = (
  loader: () => Promise<any>
): MsuiRouteComponent => lazy(loader);
