import { PluginState } from "@grafana/data";
import { config, GrafanaBootConfig } from "@grafana/runtime";
export { config, GrafanaBootConfig as Settings };

let grafanaConfig: GrafanaBootConfig = config;

export default grafanaConfig;

export const getConfig = () => {
  return grafanaConfig;
};

export const updateConfig = (update: Partial<GrafanaBootConfig>) => {
  grafanaConfig = {
    ...grafanaConfig,
    ...update,
  };
};

export const hasAlphaPanels = Boolean(
  config.panels?.debug?.state === PluginState.alpha
);
