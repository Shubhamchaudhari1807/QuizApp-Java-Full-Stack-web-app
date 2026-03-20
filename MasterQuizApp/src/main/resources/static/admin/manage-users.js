const API_BASE_URL = '/api';

/**
 * Fetches all registered users from the backend and displays them in a table.
 */
async function loadUsers() {
    const userListContainer = document.getElementById('user-list-container');
    userListContainer.innerHTML = '<p>Loading users...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/admin/users`);
        if (!response.ok) {
            throw new Error('Failed to fetch users. You must be an admin.');
        }
        const users = await response.json();

        if (users.length === 0) {
            userListContainer.innerHTML = '<p>No users found.</p>';
            return;
        }

        // Create a table to display the users
        const table = document.createElement('table');
        table.className = 'user-table';
        table.innerHTML = `
            <thead>
                <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        `;
        const tbody = table.querySelector('tbody');

        users.forEach(user => {
            const row = tbody.insertRow();
            let actionButton = '';

            // Logic to decide which button to show (Promote or Demote)
            if (user.role === 'USER') {
                actionButton = `<button class="btn-promote" onclick="handlePromoteUser(${user.id}, '${user.username}')">Promote to Admin</button>`;
            } else if (user.role === 'ADMIN') {
                // We add a disabled check for the default 'admin' and the current user later
                actionButton = `<button class="btn-demote" onclick="handleDemoteUser(${user.id}, '${user.username}')">Demote to User</button>`;
            }

            row.innerHTML = `
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td><span class="role-badge role-${user.role.toLowerCase()}">${user.role}</span></td>
                <td class="action-cell">${actionButton}</td>
            `;
        });

        userListContainer.innerHTML = '';
        userListContainer.appendChild(table);

    } catch (error) {
        console.error('Error loading users:', error);
        userListContainer.innerHTML = `<p style="color: red;">${error.message}</p>`;
    }
}

/**
 * Handles the click of the "Promote to Admin" button.
 */
async function handlePromoteUser(userId, username) {
    if (!confirm(`Are you sure you want to promote "${username}" to an Admin?`)) {
        return;
    }
    try {
        const response = await fetch(`${API_BASE_URL}/admin/users/${userId}/promote`, {
            method: 'PUT',
        });
        if (!response.ok) throw new Error('Failed to promote user.');
        alert(`User "${username}" has been successfully promoted to Admin.`);
        loadUsers(); // Refresh the user list
    } catch (error) {
        console.error('Error promoting user:', error);
        alert(error.message);
    }
}

/**
 * Handles the click of the "Demote to User" button.
 */
async function handleDemoteUser(userId, username) {
    if (!confirm(`Are you sure you want to demote "${username}" to a User?`)) {
        return;
    }
    try {
        const response = await fetch(`${API_BASE_URL}/admin/users/${userId}/demote`, {
            method: 'PUT',
        });

        if (response.ok) {
            alert(`User "${username}" has been successfully demoted to User.`);
            loadUsers(); // Refresh the list
        } else {
            // Display the specific error message from the backend (e.g., "You cannot demote yourself.")
            const errorMessage = await response.text();
            throw new Error(errorMessage);
        }
    } catch (error) {
        console.error('Error demoting user:', error);
        alert(`${error.message}`);
    }
}


// This is the main entry point that runs when the page is loaded.
document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});