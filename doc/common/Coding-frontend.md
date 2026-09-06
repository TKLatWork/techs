# Coding frontend
> Frontend coding rules for app-web module (React + Vite + TypeScript)

## Rule: Feature structure
> Single SPA entry; shared infra under `src/app`, feature code under `src/features`:

```
app-web/
  index.html                    # single SPA entry, mounted at /web/
  src/
    main.tsx                    # composition root: theme + router + layout, wires feature providers
    app/                        # shared infra: theme, http (axios), Layout, components/NavBar, navItems
    features/<feature>/         # page components + services (interface, impl, Context provider + hook)
```

> Each feature is a client route under `/web/<feature>`, listed in `navItems` and `main.tsx` routes.

Why: shared infra (nav, theme, http) has one obvious home; features stay isolated in their own folder; adding a feature = new folder + one route + one nav item, existing feature code untouched.

## Rule: DI/IoC via Context + Provider
> Components never import a concrete service/API module directly.
> Services are created once at the composition root, injected via React Context, consumed via hooks.

Why:
- Components depend on an interface, so implementations can be swapped (mock in tests, fake in storybook) without touching component code
- One composition root = one place to wire config and instances; no hidden module-singleton state
- Data flow stays visible in the component tree

One context per service:

```tsx
// src/features/timezone/timezoneService.ts - abstraction + implementation
// http = shared axios instance from src/app/http.ts
export interface TimezoneService {
  zones(): Promise<string[]>
  convert(from: string, to: string, dateTime: string): Promise<ConvertResult>
}

export function createHttpTimezoneService(): TimezoneService {
  return {
    zones: async () => (await http.get<{ zones: string[] }>('/api/timezone/zones')).data.zones,
    convert: async (from, to, dateTime) =>
      (await http.get<ConvertResult>('/api/timezone/convert', { params: { from, to, dateTime } })).data,
  }
}
```

```tsx
// src/features/timezone/TimezoneServiceProvider.tsx - context + provider + hook
const TimezoneServiceContext = createContext<TimezoneService | null>(null)

export function TimezoneServiceProvider({ children }: { children: ReactNode }) {
  const [service] = useState(() => createHttpTimezoneService())
  return (
    <TimezoneServiceContext.Provider value={service}>
      {children}
    </TimezoneServiceContext.Provider>
  )
}

export function useTimezoneService(): TimezoneService {
  const service = useContext(TimezoneServiceContext)
  if (!service) throw new Error('TimezoneServiceProvider is missing')
  return service
}
```

```tsx
// src/main.tsx - composition root: infra wraps features, providers wrap each feature page
<ThemeProvider theme={theme}>
  <CssBaseline />
  <BrowserRouter basename="/web">
    <Routes>
      <Route element={<Layout />}>
        <Route path="/timezone" element={
          <TimezoneServiceProvider>
            <TimezonePage />
          </TimezoneServiceProvider>
        } />
      </Route>
    </Routes>
  </BrowserRouter>
</ThemeProvider>
```

```tsx
// component - consume via hook only
const timezone = useTimezoneService()
const zones = await timezone.zones()
```

Test: render the component inside a provider whose value is a mock `TimezoneService` - no network, no module mocking.
