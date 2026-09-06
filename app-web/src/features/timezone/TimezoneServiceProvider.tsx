import { createContext, useContext, useState } from 'react'
import type { ReactNode } from 'react'
import { createHttpTimezoneService } from './timezoneService'
import type { TimezoneService } from './timezoneService'

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
