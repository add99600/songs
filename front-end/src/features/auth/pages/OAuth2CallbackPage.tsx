import { useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/stores/useAuthStore'

const OAuth2CallbackPage = () => {
  const navigate = useNavigate()
  const login = useAuthStore((s) => s.login)
  const calledRef = useRef(false)

  useEffect(() => {
    if (calledRef.current) return
    calledRef.current = true

    const verify = async () => {
      try {
        await login()
        navigate('/my-folders', { replace: true })
      } catch {
        navigate('/login', { replace: true })
      }
    }

    verify()
  }, [login, navigate])

  return (
    <div className="min-h-screen flex items-center justify-center bg-muted/30">
      <div className="flex flex-col items-center gap-4 text-muted-foreground">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
        <p className="text-sm">로그인 처리 중...</p>
      </div>
    </div>
  )
}

export default OAuth2CallbackPage
