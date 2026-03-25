import { create } from 'zustand'
import apiClient from '@/api/client'

interface User {
  id: number
  email: string
  nickname: string
  profileImageUrl: string | null
}

interface AuthState {
  user: User | null
  isLoggedIn: boolean
  isLoading: boolean
  initAuth: () => Promise<void>
  login: () => Promise<void>
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isLoggedIn: false,
  isLoading: true,

  initAuth: async () => {
    if (localStorage.getItem('isLoggedIn') !== 'true') {
      set({ isLoading: false })
      return
    }

    try {
      const { data } = await apiClient.get<User>('/api/auth/me')
      set({ user: data, isLoggedIn: true, isLoading: false })
    } catch {
      localStorage.removeItem('isLoggedIn')
      set({ user: null, isLoggedIn: false, isLoading: false })
    }
  },

  login: async () => {
    set({ isLoading: true })
    try {
      const { data } = await apiClient.get<User>('/api/auth/me')
      localStorage.setItem('isLoggedIn', 'true')
      set({ user: data, isLoggedIn: true, isLoading: false })
    } catch {
      localStorage.removeItem('isLoggedIn')
      set({ user: null, isLoggedIn: false, isLoading: false })
    }
  },

  logout: () => {
    localStorage.removeItem('isLoggedIn')
    set({ user: null, isLoggedIn: false })
  },
}))
