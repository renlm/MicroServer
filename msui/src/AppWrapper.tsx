import { Component, ComponentType, Fragment } from 'react';
import { Provider } from 'react-redux';
import { Route, Routes } from 'react-router-dom-v5-compat';

import {
  config,
  navigationLogger,
  reportInteraction,
  SidecarContext_EXPERIMENTAL,
  sidecarServiceSingleton_EXPERIMENTAL,
} from '@grafana/runtime';
import { ErrorBoundaryAlert, GlobalStyles, PortalContainer, TimeRangeProvider } from '@grafana/ui';
import { getAppRoutes } from 'app/routes/routes';
import { store } from 'app/store/store';

import { loadAndInitAngularIfEnabled } from './angular/loadAndInitAngularIfEnabled';
import { MsuiApp } from './app';
import { GrafanaContext } from './core/context/GrafanaContext';
import { GrafanaRouteWrapper } from './core/navigation/GrafanaRoute';
import { RouteDescriptor } from './core/navigation/types';
import { ThemeProvider } from './core/utils/ConfigProvider';
import { LiveConnectionWarning } from './features/live/LiveConnectionWarning';
import { ExtensionRegistriesProvider } from './features/plugins/extensions/ExtensionRegistriesContext';
import { ExperimentalSplitPaneRouterWrapper, RouterWrapper } from './routes/RoutesWrapper';

interface AppWrapperProps {
  app: MsuiApp;
}

interface AppWrapperState {
  ready?: boolean;
}

let bodyRenderHooks: ComponentType[] = [];
let pageBanners: ComponentType[] = [];

export function addBodyRenderHook(fn: ComponentType) {
  bodyRenderHooks.push(fn);
}

export function addPageBanner(fn: ComponentType) {
  pageBanners.push(fn);
}

export class AppWrapper extends Component<AppWrapperProps, AppWrapperState> {
  constructor(props: AppWrapperProps) {
    super(props);
    this.state = {};
  }

  async componentDidMount() {
    await loadAndInitAngularIfEnabled();
    this.setState({ ready: true });
    $('.preloader').remove();
  }

  renderRoute = (route: RouteDescriptor) => {
    return (
      <Route
        caseSensitive={route.sensitive === undefined ? false : route.sensitive}
        path={route.path}
        key={route.path}
        element={<GrafanaRouteWrapper route={route} />}
      />
    );
  };

  renderRoutes() {
    return <Routes>{getAppRoutes().map((r) => this.renderRoute(r))}</Routes>;
  }

  render() {
    const { app } = this.props;
    const { ready } = this.state;

    navigationLogger('AppWrapper', false, 'rendering');

    const commandPaletteActionSelected = (action: Action) => {
      reportInteraction('command_palette_action_selected', {
        actionId: action.id,
        actionName: action.name,
      });
    };

    const routerWrapperProps = {
      routes: ready && this.renderRoutes(),
      pageBanners,
      bodyRenderHooks,
    };

    const MaybeTimeRangeProvider = config.featureToggles.timeRangeProvider ? TimeRangeProvider : Fragment;

    return (
      <Provider store={store}>
        <ErrorBoundaryAlert style="page">
          <GrafanaContext.Provider value={app.context}>
            <ThemeProvider value={config.theme2}>
              <KBarProvider
                actions={[]}
                options={{ enableHistory: true, callbacks: { onSelectAction: commandPaletteActionSelected } }}
              >
                <GlobalStyles />
                <MaybeTimeRangeProvider>
                  <SidecarContext_EXPERIMENTAL.Provider value={sidecarServiceSingleton_EXPERIMENTAL}>
                    <ExtensionRegistriesProvider registries={app.pluginExtensionsRegistries}>
                      <div className="grafana-app">
                        {config.featureToggles.appSidecar ? (
                          <ExperimentalSplitPaneRouterWrapper {...routerWrapperProps} />
                        ) : (
                          <RouterWrapper {...routerWrapperProps} />
                        )}
                        <LiveConnectionWarning />
                        <PortalContainer />
                      </div>
                    </ExtensionRegistriesProvider>
                  </SidecarContext_EXPERIMENTAL.Provider>
                </MaybeTimeRangeProvider>
              </KBarProvider>
            </ThemeProvider>
          </GrafanaContext.Provider>
        </ErrorBoundaryAlert>
      </Provider>
    );
  }
}