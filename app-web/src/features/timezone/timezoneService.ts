import { http } from '../../app/http'

export interface ConvertResult {
  fromZone: string
  toZone: string
  input: string
  converted: string
  fromOffset: string
  toOffset: string
}

export interface TimezoneService {
  zones(): Promise<string[]>
  convert(from: string, to: string, dateTime: string): Promise<ConvertResult>
}

export function createHttpTimezoneService(): TimezoneService {
  return {
    zones: async () => (await http.get<{ zones: string[] }>('/api/timezone/zones')).data.zones,
    convert: async (from, to, dateTime) =>
      (
        await http.get<ConvertResult>('/api/timezone/convert', {
          params: { from, to, dateTime },
        })
      ).data,
  }
}
