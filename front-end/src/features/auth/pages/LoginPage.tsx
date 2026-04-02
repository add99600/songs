import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/useAuthStore'

const GoogleIcon = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 48 48"
    width="20"
    height="20"
    aria-hidden="true"
  >
    <path
      fill="#EA4335"
      d="M24 9.5c3.14 0 5.95 1.08 8.17 2.85l6.09-6.09C34.46 3.09 29.5 1 24 1 14.82 1 7.07 6.48 3.58 14.22l7.08 5.5C12.43 13.61 17.74 9.5 24 9.5z"
    />
    <path
      fill="#4285F4"
      d="M46.52 24.5c0-1.64-.15-3.22-.42-4.74H24v8.98h12.7c-.55 2.93-2.2 5.41-4.67 7.08l7.18 5.57C43.27 37.67 46.52 31.55 46.52 24.5z"
    />
    <path
      fill="#FBBC05"
      d="M10.66 28.28A14.56 14.56 0 0 1 9.5 24c0-1.49.26-2.93.72-4.28l-7.08-5.5A23.93 23.93 0 0 0 0 24c0 3.87.93 7.52 2.56 10.74l8.1-6.46z"
    />
    <path
      fill="#34A853"
      d="M24 47c5.5 0 10.12-1.82 13.49-4.95l-7.18-5.57C28.6 37.9 26.42 38.5 24 38.5c-6.26 0-11.57-4.11-13.34-9.72l-8.1 6.46C6.07 42.52 14.44 47 24 47z"
    />
  </svg>
)

const MusicNote = ({ className }: { className?: string }) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="1.5"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
    aria-hidden="true"
  >
    <path d="M9 18V5l12-2v13" />
    <circle cx="6" cy="18" r="3" />
    <circle cx="18" cy="16" r="3" />
  </svg>
)

const LoginPage = () => {
  const navigate = useNavigate()
  const { isLoggedIn, isLoading } = useAuthStore()

  useEffect(() => {
    if (!isLoading && isLoggedIn) {
      navigate('/my-folders', { replace: true })
    }
  }, [isLoggedIn, isLoading, navigate])

  const handleGoogleLogin = () => {
    const baseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081'
    window.location.href = `${baseUrl}/oauth2/authorization/google`
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center relative overflow-hidden bg-gradient-to-br from-primary/5 via-background to-accent/10">
      {/* Floating music notes */}
      <div className="absolute inset-0 pointer-events-none select-none" aria-hidden="true">
        <MusicNote className="absolute top-[12%] left-[8%] w-8 h-8 text-primary/10 animate-pulse" />
        <MusicNote className="absolute top-[25%] right-[12%] w-6 h-6 text-accent/15 animate-pulse [animation-delay:1s]" />
        <MusicNote className="absolute bottom-[20%] left-[15%] w-10 h-10 text-primary/8 animate-pulse [animation-delay:2s]" />
        <MusicNote className="absolute bottom-[30%] right-[8%] w-7 h-7 text-accent/10 animate-pulse [animation-delay:0.5s]" />
        <MusicNote className="absolute top-[60%] left-[5%] w-5 h-5 text-primary/12 animate-pulse [animation-delay:1.5s]" />
        <MusicNote className="absolute top-[10%] right-[30%] w-9 h-9 text-accent/8 animate-pulse [animation-delay:3s]" />
      </div>

      {/* Main content */}
      <div className="relative z-10 flex flex-col items-center gap-8 px-4 w-full max-w-sm">
        {/* Logo & Title */}
        <div className="flex flex-col items-center gap-3 text-center">
          <div className="w-20 h-20 rounded-2xl bg-gradient-to-br from-primary to-accent flex items-center justify-center shadow-lg shadow-primary/25">
            <MusicNote className="w-10 h-10 text-primary-foreground" />
          </div>
          <div>
            <h1 className="text-4xl font-bold tracking-tight bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
              SONGS
            </h1>
            <p className="text-muted-foreground mt-1.5 text-sm">
              나만의 노래방 플레이리스트를 만들어보세요
            </p>
          </div>
        </div>

        {/* Login Card */}
        <div className="w-full space-y-4">
          <div className="rounded-xl border bg-card/80 backdrop-blur-sm p-6 shadow-sm">
            <Button
              className="w-full flex items-center justify-center gap-3 h-12 text-base font-medium"
              size="lg"
              variant="outline"
              onClick={handleGoogleLogin}
            >
              <GoogleIcon />
              Google로 시작하기
            </Button>
          </div>

          {/* Features */}
          <div className="grid grid-cols-3 gap-3 text-center">
            <div className="rounded-lg border bg-card/60 backdrop-blur-sm p-3">
              <div className="text-lg mb-0.5">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 mx-auto text-primary" aria-hidden="true">
                  <circle cx="11" cy="11" r="8" /><path d="m21 21-4.3-4.3" />
                </svg>
              </div>
              <p className="text-xs text-muted-foreground leading-tight">TJ/KY<br />곡 검색</p>
            </div>
            <div className="rounded-lg border bg-card/60 backdrop-blur-sm p-3">
              <div className="text-lg mb-0.5">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 mx-auto text-primary" aria-hidden="true">
                  <path d="M12 2H2v10l9.29 9.29a1 1 0 0 0 1.42 0l8.58-8.58a1 1 0 0 0 0-1.42z" /><circle cx="7.5" cy="7.5" r=".5" fill="currentColor" />
                </svg>
              </div>
              <p className="text-xs text-muted-foreground leading-tight">나만의<br />플레이리스트</p>
            </div>
            <div className="rounded-lg border bg-card/60 backdrop-blur-sm p-3">
              <div className="text-lg mb-0.5">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5 mx-auto text-primary" aria-hidden="true">
                  <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8" /><polyline points="16 6 12 2 8 6" /><line x1="12" x2="12" y1="2" y2="15" />
                </svg>
              </div>
              <p className="text-xs text-muted-foreground leading-tight">친구와<br />공유</p>
            </div>
          </div>
        </div>

        {/* Footer */}
        <p className="text-xs text-muted-foreground/60">
          로그인 시 서비스 이용약관에 동의합니다
        </p>
      </div>
    </div>
  )
}

export default LoginPage
