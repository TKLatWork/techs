import { useEffect, useState } from 'react'

export default function App() {
  const [message, setMessage] = useState<string>('')
  const [error, setError] = useState<string>('')

  useEffect(() => {
    fetch('/api/hello')
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json()
      })
      .then((data: { message: string }) => setMessage(data.message))
      .catch((err) => setError(String(err)))
  }, [])

  return (
    <main>
      <h1>WebApp</h1>
      {error ? (
        <p className="error">Failed to load: {error}</p>
      ) : (
        <p>{message || 'Loading...'}</p>
      )}
    </main>
  )
}
