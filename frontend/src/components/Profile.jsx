import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Profile = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="container">
      <h2>Profile</h2>
      <div className="success">
        <p><strong>Username:</strong> {user?.username}</p>
        <p><strong>Roles:</strong> {user?.roles?.join(', ')}</p>
      </div>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default Profile;
