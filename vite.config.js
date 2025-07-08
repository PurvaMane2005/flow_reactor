import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: path.resolve(__dirname, 'android/app/src/main/assets'),
    emptyOutDir: true,
  },
  publicDir: false, // ✅ prevents copying the public/ folder
})
