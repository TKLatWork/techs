import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Single SPA entry, mounted at /web/; features are client routes under /web/<feature>
export default defineConfig({
  base: '/web/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
  },
})
