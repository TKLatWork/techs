import { describe, it, expect, vi, beforeEach } from 'vitest';
import api, { authApi, userApi } from '../api/auth';
import axios from 'axios';

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: {
          use: vi.fn(),
        },
      },
      post: vi.fn(),
      get: vi.fn(),
    })),
  },
}));

describe('API Module', () => {
  let mockApi;

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it('should add authorization header when token exists', () => {
    localStorage.setItem('token', 'test-token');
    
    const mockInstance = {
      interceptors: { request: { use: vi.fn((cb) => cb({ headers: {} })) } },
      post: vi.fn(),
      get: vi.fn(),
    };
    
    axios.create.mockReturnValue(mockInstance);
    
    const testApi = axios.create();
    
    const config = { headers: {} };
    testApi.interceptors.request.use.mock.calls[0][0](config);
    
    expect(config.headers.Authorization).toBe('Bearer test-token');
  });

  it('should not add authorization header when no token', () => {
    const mockInstance = {
      interceptors: { request: { use: vi.fn((cb) => cb({ headers: {} })) } },
      post: vi.fn(),
      get: vi.fn(),
    };
    
    axios.create.mockReturnValue(mockInstance);
    
    const testApi = axios.create();
    
    const config = { headers: {} };
    testApi.interceptors.request.use.mock.calls[0][0](config);
    
    expect(config.headers.Authorization).toBeUndefined();
  });
});
