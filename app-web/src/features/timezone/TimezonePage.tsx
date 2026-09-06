import { useEffect, useState } from 'react'
import Alert from '@mui/material/Alert'
import Autocomplete from '@mui/material/Autocomplete'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Paper from '@mui/material/Paper'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useTimezoneService } from './TimezoneServiceProvider'
import type { ConvertResult } from './timezoneService'
import { toErrorMessage } from '../../app/http'

export default function TimezonePage() {
  const timezone = useTimezoneService()
  const [zones, setZones] = useState<string[]>([])
  const [fromZone, setFromZone] = useState('Asia/Shanghai')
  const [toZone, setToZone] = useState('Europe/Paris')
  const [dateTime, setDateTime] = useState('2026-09-04T16:00')
  const [result, setResult] = useState<ConvertResult | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    timezone
      .zones()
      .then(setZones)
      .catch((err) => setError(toErrorMessage(err)))
  }, [timezone])

  const convert = () => {
    setError('')
    setResult(null)
    setLoading(true)
    timezone
      .convert(fromZone, toZone, dateTime)
      .then(setResult)
      .catch((err) => setError(toErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  return (
    <Paper sx={{ p: 4 }}>
      <Typography variant="h4" gutterBottom>
        Timezone Converter
      </Typography>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
        <TextField
          label="Time"
          type="datetime-local"
          value={dateTime}
          onChange={(e) => setDateTime(e.target.value)}
          slotProps={{ inputLabel: { shrink: true } }}
        />
        <Autocomplete
          options={zones}
          value={fromZone}
          onChange={(_, value) => value && setFromZone(value)}
          renderInput={(params) => <TextField {...params} label="From" />}
        />
        <Autocomplete
          options={zones}
          value={toZone}
          onChange={(_, value) => value && setToZone(value)}
          renderInput={(params) => <TextField {...params} label="To" />}
        />
        <Button variant="contained" onClick={convert} disabled={loading || zones.length === 0}>
          Convert
        </Button>
      </Box>
      {error && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
        </Alert>
      )}
      {result && (
        <Paper variant="outlined" sx={{ mt: 3, p: 2 }}>
          <Typography color="text.secondary">
            {result.input} ({result.fromZone}, UTC{result.fromOffset})
          </Typography>
          <Typography variant="h6" sx={{ mt: 1 }}>
            {result.converted} ({result.toZone}, UTC{result.toOffset})
          </Typography>
        </Paper>
      )}
    </Paper>
  )
}
