import { useEffect, useState } from 'react'

interface ConvertResult {
  fromZone: string
  toZone: string
  input: string
  converted: string
  fromOffset: string
  toOffset: string
}

export default function App() {
  const [zones, setZones] = useState<string[]>([])
  const [fromZone, setFromZone] = useState('Asia/Shanghai')
  const [toZone, setToZone] = useState('Europe/Paris')
  const [dateTime, setDateTime] = useState('2026-09-04T16:00')
  const [result, setResult] = useState<ConvertResult | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    fetch('/api/timezone/zones')
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json()
      })
      .then((data: { zones: string[] }) => setZones(data.zones))
      .catch((err) => setError(String(err)))
  }, [])

  const convert = () => {
    setError('')
    setResult(null)
    const params = new URLSearchParams({ from: fromZone, to: toZone, dateTime })
    fetch(`/api/timezone/convert?${params}`)
      .then((res) =>
        res.json().then((body) => {
          if (!res.ok) throw new Error(body.message ?? `HTTP ${res.status}`)
          return body as ConvertResult
        }),
      )
      .then(setResult)
      .catch((err) => setError(String(err.message ?? err)))
  }

  const zoneOptions = zones.map((zone) => (
    <option key={zone} value={zone}>
      {zone}
    </option>
  ))

  return (
    <main>
      <h1>Timezone Converter</h1>
      <div className="form">
        <label>
          Time
          <input
            type="datetime-local"
            value={dateTime}
            onChange={(e) => setDateTime(e.target.value)}
          />
        </label>
        <label>
          From
          <select value={fromZone} onChange={(e) => setFromZone(e.target.value)}>
            {zoneOptions}
          </select>
        </label>
        <label>
          To
          <select value={toZone} onChange={(e) => setToZone(e.target.value)}>
            {zoneOptions}
          </select>
        </label>
        <button onClick={convert} disabled={zones.length === 0}>
          Convert
        </button>
      </div>
      {error && <p className="error">{error}</p>}
      {result && (
        <div className="result">
          <p>
            {result.input} ({result.fromZone}, UTC{result.fromOffset})
          </p>
          <p className="converted">
            {result.converted} ({result.toZone}, UTC{result.toOffset})
          </p>
        </div>
      )}
    </main>
  )
}
