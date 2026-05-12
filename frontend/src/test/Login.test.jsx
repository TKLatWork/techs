import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Login from '../components/Login';
import { AuthProvider } from '../context/AuthContext';
import { BrowserRouter } from 'react-router-dom';

const mockLogin = vi.fn();
const mockRegister = vi.fn();

vi.mock('../context/AuthContext', async () => {
  const actual = await vi.importActual('../context/AuthContext');
  return {
    ...actual,
    useAuth: () => ({
      login: mockLogin,
      register: mockRegister,
      isAuthenticated: false,
      user: null,
      loading: false,
    }),
    AuthProvider: ({ children }) => <div>{children}</div>,
  };
});

const renderWithRouter = (component) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  );
};

describe('Login Component', () => {
  it('should render login form by default', () => {
    renderWithRouter(<Login />);
    
    expect(screen.getByText('Login')).toBeInTheDocument();
    expect(screen.getByLabelText('Username')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
  });

  it('should switch to register mode when register link is clicked', () => {
    renderWithRouter(<Login />);
    
    fireEvent.click(screen.getByText('Register'));
    
    expect(screen.getByText('Register')).toBeInTheDocument();
  });

  it('should show loading state during submission', async () => {
    mockLogin.mockResolvedValueOnce({ token: 'test-token', username: 'testuser' });
    
    renderWithRouter(<Login />);
    
    fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password' } });
    fireEvent.click(screen.getByText('Login'));
    
    expect(screen.getByText('Processing...')).toBeInTheDocument();
  });

  it('should show error message when login fails', async () => {
    mockLogin.mockRejectedValueOnce({ response: { data: { error: 'Invalid credentials' } } });
    
    renderWithRouter(<Login />);
    
    fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'wrongpassword' } });
    fireEvent.click(screen.getByText('Login'));
    
    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument();
    });
  });

  it('should call login with correct credentials', async () => {
    mockLogin.mockResolvedValueOnce({ token: 'test-token', username: 'testuser' });
    
    renderWithRouter(<Login />);
    
    fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password123' } });
    fireEvent.click(screen.getByText('Login'));
    
    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('testuser', 'password123');
    });
  });
});
