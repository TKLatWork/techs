import type { RouteObject } from "react-router-dom";

interface FeatureRouteModule {
  routes: RouteObject[];
}

const featureModules = import.meta.glob<FeatureRouteModule>(
  "../features/*/routes.tsx",
  { eager: true }
);

export function collectFeatureRoutes(): RouteObject[] {
  const routes: RouteObject[] = [];
  for (const mod of Object.values(featureModules)) {
    const m = mod as FeatureRouteModule;
    if (m && m.routes) {
      routes.push(...m.routes);
    }
  }
  return routes;
}
