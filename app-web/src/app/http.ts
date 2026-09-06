import axios from 'axios'

export const http = axios.create({
  headers: { Accept: 'application/json' },
})

export function toErrorMessage(err: unknown): string {
  if (axios.isAxiosError(err)) {
    const message = (err.response?.data as { message?: string } | undefined)?.message
    return message || `HTTP ${err.response?.status ?? 'network error'}`
  }
  return String(err)
}
