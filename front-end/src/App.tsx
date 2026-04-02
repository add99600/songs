import { createBrowserRouter, Navigate, RouterProvider } from 'react-router-dom'
import { MainLayout } from '@/layouts/MainLayout'
import { ProtectedRoute } from '@/features/auth/components/ProtectedRoute'
import SearchPage from '@/pages/SearchPage'
import MyFoldersPage from '@/pages/MyFoldersPage'
import PlaylistDetailPage from '@/pages/PlaylistDetailPage'
import SharedPage from '@/pages/SharedPage'
import LoginPage from '@/pages/LoginPage'
import OAuth2CallbackPage from '@/pages/OAuth2CallbackPage'

const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <Navigate to="/search" replace /> },
      { path: 'search', element: <SearchPage /> },
      {
        element: <ProtectedRoute />,
        children: [
          { path: 'my-folders', element: <MyFoldersPage /> },
          { path: 'my-folders/:playlistId', element: <PlaylistDetailPage /> },
          { path: 'shared', element: <SharedPage /> },
        ],
      },
    ],
  },
  { path: '/login', element: <LoginPage /> },
  { path: '/oauth2/callback', element: <OAuth2CallbackPage /> },
  { path: '*', element: <Navigate to="/search" replace /> },
])

function App() {
  return <RouterProvider router={router} />
}

export default App
