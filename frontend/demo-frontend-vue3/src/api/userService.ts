import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

interface LoginRequest {
  username: string
  password: string
}

interface RegisterRequest {
  username: string
  email: string
  password: string
}

interface ApiResponse {
  code: number
  message: string
  data: any
  success: boolean
}

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add token to request headers if it exists
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  // Note: No token warning removed for cleaner console output
  return config
})

// Add response interceptor for better error handling
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // Log detailed error information for debugging
    console.error('API Error:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
    })
    return Promise.reject(error)
  }
)

export const userService = {
  login(data: LoginRequest): Promise<ApiResponse> {
    return api.post('/user/login', data)
  },

  register(data: RegisterRequest): Promise<ApiResponse> {
    return api.post('/user/register', data)
  },

  logout(): Promise<ApiResponse> {
    return api.post('/user/logout')
  },
}

export const playerService = {
  getPlayerInfo(): Promise<ApiResponse> {
    return api.get('/player/show')
  },

  createPlayer(): Promise<ApiResponse> {
    return api.post('/player/create')
  },
}
