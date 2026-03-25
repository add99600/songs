import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { useAuthStore } from '@/stores/useAuthStore'
import './index.css'
import App from './App.tsx'

useAuthStore.getState().initAuth()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
