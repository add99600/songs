import { useState } from 'react'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { Search, FolderOpen, Users, LogIn, LogOut, Music, Menu } from 'lucide-react'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetClose,
} from '@/components/ui/sheet'
import { useAuthStore } from '@/stores/useAuthStore'
import apiClient from '@/api/client'

const navItems = [
  { path: '/search', label: '노래 검색', icon: Search },
  { path: '/my-folders', label: '내 애창곡', icon: FolderOpen },
  { path: '/shared', label: '함께 듣는 믹스', icon: Users },
]

export const MainLayout = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const { user, isLoggedIn, logout } = useAuthStore()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const handleLogout = async () => {
    try {
      await apiClient.post('/api/auth/logout')
    } finally {
      logout()
      navigate('/login', { replace: true })
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-muted/30">
      {/* Header */}
      <header className="sticky top-0 z-50 w-full bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container flex h-16 items-center justify-between px-4 sm:px-6">
          {/* Logo */}
          <Link to="/search" className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary to-accent flex items-center justify-center">
              <Music className="w-4 h-4 text-primary-foreground" />
            </div>
            <span className="font-bold text-lg bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
              SONGS
            </span>
          </Link>

          {/* Desktop nav */}
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map((item) => {
              const Icon = item.icon
              const isActive = location.pathname === item.path
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={cn(
                    'flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all duration-200',
                    isActive
                      ? 'bg-primary text-primary-foreground shadow-sm shadow-primary/25'
                      : 'text-muted-foreground hover:text-foreground hover:bg-muted'
                  )}
                >
                  <Icon className="size-4" />
                  {item.label}
                </Link>
              )
            })}
          </nav>

          {/* Desktop user section */}
          <div className="hidden md:flex items-center gap-3">
            {isLoggedIn ? (
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-2">
                  <Avatar className="size-8">
                    {user?.profileImageUrl && (
                      <AvatarImage src={user.profileImageUrl} alt={user.nickname} />
                    )}
                    <AvatarFallback className="text-xs bg-primary/10 text-primary">
                      {user?.nickname?.[0] ?? '?'}
                    </AvatarFallback>
                  </Avatar>
                  <span className="text-sm font-medium hidden lg:inline">
                    {user?.nickname}
                  </span>
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleLogout}
                  className="text-muted-foreground"
                >
                  <LogOut className="size-4 mr-1.5" />
                  로그아웃
                </Button>
              </div>
            ) : (
              <Button size="sm" onClick={() => navigate('/login')} className="gap-2">
                <LogIn className="size-4" />
                로그인
              </Button>
            )}
          </div>

          {/* Mobile header: hamburger menu */}
          <div className="md:hidden">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => setMobileMenuOpen(true)}
              aria-label="메뉴 열기"
            >
              <Menu className="size-5" />
            </Button>
          </div>
        </div>

        {/* Gradient bottom border */}
        <div className="h-px bg-gradient-to-r from-transparent via-primary/20 to-transparent" />
      </header>

      {/* Main content - extra bottom padding on mobile for fixed nav + safe area */}
      <main className="flex-1 pb-24 md:pb-0">
        <Outlet />
      </main>

      {/* Footer (desktop only) */}
      <footer className="hidden md:block border-t py-4">
        <div className="container px-4 sm:px-6">
          <p className="text-xs text-muted-foreground text-center">
            SONGS &mdash; 나만의 노래방 플레이리스트
          </p>
        </div>
      </footer>

      {/* Mobile slide-out menu */}
      <Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
        <SheetContent side="right" className="w-[280px] p-0">
          <SheetHeader className="border-b px-4 py-4">
            <SheetTitle className="text-left">메뉴</SheetTitle>
          </SheetHeader>

          {/* User info */}
          {isLoggedIn && (
            <div className="flex items-center gap-3 px-4 py-4 border-b">
              <Avatar className="size-9">
                {user?.profileImageUrl && (
                  <AvatarImage src={user.profileImageUrl} alt={user.nickname} />
                )}
                <AvatarFallback className="text-xs bg-primary/10 text-primary">
                  {user?.nickname?.[0] ?? '?'}
                </AvatarFallback>
              </Avatar>
              <div className="min-w-0">
                <p className="text-sm font-medium truncate">{user?.nickname}</p>
                <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
              </div>
            </div>
          )}

          {/* Nav links */}
          <nav className="flex flex-col gap-1 p-3">
            {navItems.map((item) => {
              const Icon = item.icon
              const isActive = location.pathname === item.path
              return (
                <SheetClose key={item.path} render={<span />}>
                  <Link
                    to={item.path}
                    onClick={() => setMobileMenuOpen(false)}
                    className={cn(
                      'flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors',
                      isActive
                        ? 'bg-primary text-primary-foreground'
                        : 'text-foreground hover:bg-muted'
                    )}
                  >
                    <Icon className="size-5" />
                    {item.label}
                  </Link>
                </SheetClose>
              )
            })}
          </nav>

          {/* Bottom actions */}
          <div className="mt-auto border-t p-3">
            {isLoggedIn ? (
              <button
                onClick={() => {
                  setMobileMenuOpen(false)
                  handleLogout()
                }}
                className="flex items-center gap-3 w-full px-4 py-3 rounded-lg text-sm font-medium text-destructive hover:bg-destructive/10 transition-colors"
              >
                <LogOut className="size-5" />
                로그아웃
              </button>
            ) : (
              <button
                onClick={() => {
                  setMobileMenuOpen(false)
                  navigate('/login')
                }}
                className="flex items-center gap-3 w-full px-4 py-3 rounded-lg text-sm font-medium text-primary hover:bg-primary/10 transition-colors"
              >
                <LogIn className="size-5" />
                로그인
              </button>
            )}
          </div>
        </SheetContent>
      </Sheet>

      {/* Mobile bottom navigation bar with safe-area-inset support */}
      <nav
        className="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-background/95 backdrop-blur border-t"
        style={{ paddingBottom: 'env(safe-area-inset-bottom, 0px)' }}
      >
        <div className="flex items-center justify-around px-2" style={{ height: '56px' }}>
          {navItems.map((item) => {
            const Icon = item.icon
            const isActive = location.pathname === item.path
            return (
              <Link
                key={item.path}
                to={item.path}
                className={cn(
                  'relative flex flex-col items-center justify-center gap-0.5 rounded-xl transition-all duration-200',
                  'min-w-[72px] min-h-[48px] px-3 py-1.5',
                  'active:scale-95',
                  isActive
                    ? 'text-primary'
                    : 'text-muted-foreground hover:text-foreground'
                )}
                aria-current={isActive ? 'page' : undefined}
              >
                {/* Active indicator background */}
                {isActive && (
                  <span className="absolute inset-x-2 inset-y-0.5 rounded-xl bg-primary/10 -z-10" />
                )}
                <Icon
                  className={cn(
                    'size-5 transition-transform duration-200',
                    isActive && 'scale-110'
                  )}
                />
                <span
                  className={cn(
                    'text-[10px] leading-tight',
                    isActive ? 'font-semibold' : 'font-medium'
                  )}
                >
                  {item.label}
                </span>
                {/* Active dot indicator */}
                {isActive && (
                  <span className="absolute -bottom-0.5 w-1 h-1 rounded-full bg-primary" />
                )}
              </Link>
            )
          })}
        </div>
      </nav>
    </div>
  )
}
