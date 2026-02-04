// API Configuration
const API_URL = 'http://localhost:8080/api';

// ==================== Tab Management ====================
function switchTab(tabName) {
    // Hide all content
    document.querySelectorAll('.content').forEach(el => {
        el.classList.remove('active');
    });

    // Remove active class from all buttons
    document.querySelectorAll('.tab-button').forEach(el => {
        el.classList.remove('active');
    });

    // Show selected content
    document.getElementById(tabName).classList.add('active');

    // Add active class to clicked button
    event.target.classList.add('active');

    // Load data when switching tabs
    if (tabName === 'users') {
        loadUsers();
    } else if (tabName === 'portfolios') {
        loadPortfolios();
    } else if (tabName === 'status') {
        checkApiStatus();
    }
}

// ==================== Message Display ====================
function showMessage(messageId, text, type = 'info') {
    const messageEl = document.getElementById(messageId);
    messageEl.textContent = text;
    messageEl.className = `message show ${type}`;
    setTimeout(() => {
        messageEl.classList.remove('show');
    }, 5000);
}

function showLoading(loadingId, show = true) {
    const loadingEl = document.getElementById(loadingId);
    if (show) {
        loadingEl.classList.add('show');
    } else {
        loadingEl.classList.remove('show');
    }
}

// ==================== USER CRUD OPERATIONS ====================

// Create or Update User
async function handleUserSubmit(event) {
    event.preventDefault();

    const userId = document.getElementById('userId').value;
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const currency = document.getElementById('currency').value;

    const userData = {
        username: username,
        email: email,
        defaultCurrency: currency
    };

    try {
        showLoading('userLoading', true);

        let response;
        if (userId) {
            // Update existing user
            response = await fetch(`${API_URL}/users/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
            if (response.ok) {
                showMessage('userMessage', `✅ User updated successfully!`, 'success');
            }
        } else {
            // Create new user
            response = await fetch(`${API_URL}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
            if (response.ok) {
                showMessage('userMessage', `✅ User created successfully!`, 'success');
            }
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        resetUserForm();
        await loadUsers();
    } catch (error) {
        showMessage('userMessage', `❌ Error: ${error.message}`, 'error');
        console.error('Error:', error);
    } finally {
        showLoading('userLoading', false);
    }
}

// Read Users
async function loadUsers() {
    try {
        showLoading('userLoading', true);

        const response = await fetch(`${API_URL}/users`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const users = await response.json();
        const tbody = document.getElementById('usersTableBody');

        if (users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No users found. Create one to get started!</td></tr>';
        } else {
            tbody.innerHTML = users.map(user => `
                <tr>
                    <td>${user.userId}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.defaultCurrency}</td>
                    <td>${new Date(user.createdAt).toLocaleDateString()}</td>
                    <td>
                        <div class="actions">
                            <button class="btn-edit" onclick="editUser(${user.userId}, '${user.username}', '${user.email}', '${user.defaultCurrency}')">Edit</button>
                            <button class="btn-delete" onclick="deleteUser(${user.userId})">Delete</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        showMessage('userMessage', `✅ Loaded ${users.length} users`, 'info');
    } catch (error) {
        showMessage('userMessage', `❌ Error loading users: ${error.message}`, 'error');
        console.error('Error:', error);
    } finally {
        showLoading('userLoading', false);
    }
}

// Edit User
function editUser(userId, username, email, currency) {
    document.getElementById('userId').value = userId;
    document.getElementById('username').value = username;
    document.getElementById('email').value = email;
    document.getElementById('currency').value = currency;
    window.scrollTo(0, 0);
}

// Delete User
async function deleteUser(userId) {
    if (!confirm(`Are you sure you want to delete user ${userId}?`)) {
        return;
    }

    try {
        showLoading('userLoading', true);

        const response = await fetch(`${API_URL}/users/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        showMessage('userMessage', `✅ User deleted successfully!`, 'success');
        await loadUsers();
    } catch (error) {
        showMessage('userMessage', `❌ Error deleting user: ${error.message}`, 'error');
        console.error('Error:', error);
    } finally {
        showLoading('userLoading', false);
    }
}

// Reset User Form
function resetUserForm() {
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
}

// ==================== PORTFOLIO CRUD OPERATIONS ====================

// Create or Update Portfolio
async function handlePortfolioSubmit(event) {
    event.preventDefault();

    const portfolioId = document.getElementById('portfolioId').value;
    const userId = document.getElementById('userId2').value;
    const portfolioName = document.getElementById('portfolioName').value;
    const baseCurrency = document.getElementById('baseCurrency2').value;

    const portfolioData = {
        userId: parseInt(userId),
        portfolioName: portfolioName,
        baseCurrency: baseCurrency
    };

    try {
        showLoading('portfolioLoading', true);

        let response;
        if (portfolioId) {
            // Update existing portfolio
            response = await fetch(`${API_URL}/portfolios/${portfolioId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(portfolioData)
            });
            if (response.ok) {
                showMessage('portfolioMessage', `✅ Portfolio updated successfully!`, 'success');
            }
        } else {
            // Create new portfolio
            response = await fetch(`${API_URL}/portfolios`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(portfolioData)
            });
            if (response.ok) {
                showMessage('portfolioMessage', `✅ Portfolio created successfully!`, 'success');
            }
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        resetPortfolioForm();
        await loadPortfolios();
    } catch (error) {
        showMessage('portfolioMessage', `❌ Error: ${error.message}`, 'error');
        console.error('Error:', error);
    } finally {
        showLoading('portfolioLoading', false);
    }
}

// Read Portfolios
async function loadPortfolios() {
    try {
        showLoading('portfolioLoading', true);

        const response = await fetch(`${API_URL}/portfolios`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const portfolios = await response.json();
        const tbody = document.getElementById('portfoliosTableBody');

        if (portfolios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No portfolios found. Create one to get started!</td></tr>';
        } else {
            tbody.innerHTML = portfolios.map(portfolio => `
                <tr>
                    <td>${portfolio.portfolioId}</td>
                    <td>${portfolio.userId}</td>
                    <td>${portfolio.portfolioName}</td>
                    <td>${portfolio.baseCurrency}</td>
                    <td>${new Date(portfolio.createdAt).toLocaleDateString()}</td>
                    <td>
                        <div class="actions">
                            <button class="btn-edit" onclick="editPortfolio(${portfolio.portfolioId}, ${portfolio.userId}, '${portfolio.portfolioName}', '${portfolio.baseCurrency}')">Edit</button>
                            <button class="btn-delete" onclick="deletePortfolio(${portfolio.portfolioId})">Delete</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        showMessage('portfolioMessage', `✅ Loaded ${portfolios.length} portfolios`, 'info');
    } catch (error) {
        showMessage('portfolioMessage', `❌ Error loading portfolios: ${error.message}`, 'error');
        console.error('Error:', error);
    } finally {
        showLoading('portfolioLoading', false);
    }
}

// Edit Portfolio
function editPortfolio(portfolioId, userId, portfolioName, baseCurrency) {
    document.getElementById('portfolioId').value = portfolioId;
    document.getElementById('userId2').value = userId;
    document.getElementById('portfolioName').value = portfolioName;
    document.getElementById('baseCurrency2').value = baseCurrency;
    window.scrollTo(0, 0);
}

// Delete Portfolio
async function deletePortfolio(portfolioId) {
    if (!confirm(`Are you sure you want to delete portfolio ${portfolioId}?`)) {
        return;
    }

    try {
        showLoading('portfolioLoading', true);

        const response = await fetch(`${API_URL}/portfolios/${portfolioId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        showMessage('portfolioMessage', `✅ Portfolio deleted successfully!`, 'success');
        await loadPortfolios();
    } catch (error) {
        showMessage('portfolioMessage', `❌ Error deleting portfolio: ${error.message}`, 'error');
        console.error('Error:', error);
    } finally {
        showLoading('portfolioLoading', false);
    }
}

// Reset Portfolio Form
function resetPortfolioForm() {
    document.getElementById('portfolioForm').reset();
    document.getElementById('portfolioId').value = '';
}

// ==================== API STATUS CHECK ====================
async function checkApiStatus() {
    try {
        const statusContent = document.getElementById('statusContent');
        statusContent.innerHTML = '<div class="spinner"></div> Checking API status...';

        const response = await fetch(`${API_URL}/users`);

        if (response.ok) {
            statusContent.innerHTML = `
                <div style="background: #d4edda; padding: 20px; border-radius: 5px; border-left: 4px solid #28a745;">
                    <h3 style="color: #155724; margin-bottom: 10px;">✅ API is Connected!</h3>
                    <p style="color: #155724; margin: 5px 0;">✓ Backend: <strong>Running on port 8080</strong></p>
                    <p style="color: #155724; margin: 5px 0;">✓ Database: <strong>Connected</strong></p>
                    <p style="color: #155724; margin: 5px 0;">✓ API: <strong>Responding correctly</strong></p>
                </div>
            `;
        } else {
            throw new Error('API not responding with 200 OK');
        }
    } catch (error) {
        document.getElementById('statusContent').innerHTML = `
            <div style="background: #f8d7da; padding: 20px; border-radius: 5px; border-left: 4px solid #f5c6cb;">
                <h3 style="color: #721c24; margin-bottom: 10px;">❌ API Connection Failed</h3>
                <p style="color: #721c24; margin: 5px 0;"><strong>Error:</strong> ${error.message}</p>
                <p style="color: #721c24; margin: 5px 0;">Make sure:</p>
                <ul style="color: #721c24; margin-left: 20px; margin-top: 10px;">
                    <li>MySQL is running: <code>net start MySQL80</code></li>
                    <li>Backend is running: <code>.\\mvnw.cmd spring-boot:run</code></li>
                    <li>API URL is correct: <code>${API_URL}</code></li>
                </ul>
            </div>
        `;
    }
}

// ==================== SAMPLE DATA ====================
async function createSampleData() {
    try {
        showLoading('userLoading', true);

        // Create sample user
        const userResponse = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: 'demo_user_' + Date.now(),
                email: 'demo' + Date.now() + '@example.com',
                defaultCurrency: 'USD'
            })
        });

        if (!userResponse.ok) {
            throw new Error(`Failed to create user: ${userResponse.status}`);
        }

        const user = await userResponse.json();

        // Create sample portfolio
        const portfolioResponse = await fetch(`${API_URL}/portfolios`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: user.userId,
                portfolioName: 'Demo Portfolio',
                baseCurrency: 'USD'
            })
        });

        if (!portfolioResponse.ok) {
            throw new Error(`Failed to create portfolio: ${portfolioResponse.status}`);
        }

        const statusContent = document.getElementById('statusContent');
        statusContent.innerHTML = `
            <div style="background: #d4edda; padding: 20px; border-radius: 5px; border-left: 4px solid #28a745;">
                <h3 style="color: #155724; margin-bottom: 10px;">✅ Sample Data Created!</h3>
                <p style="color: #155724; margin: 5px 0;">✓ User ID: <strong>${user.userId}</strong></p>
                <p style="color: #155724; margin: 5px 0;">✓ Username: <strong>${user.username}</strong></p>
                <p style="color: #155724; margin: 5px 0;">✓ Portfolio Created: <strong>Demo Portfolio</strong></p>
                <p style="color: #155724; margin-top: 15px;">Go to Users and Portfolios tabs to see the created data!</p>
            </div>
        `;

        showMessage('userMessage', '✅ Sample data created! Check the Users tab.', 'success');
        await loadUsers();
        showLoading('userLoading', false);
    } catch (error) {
        document.getElementById('statusContent').innerHTML = `
            <div style="background: #f8d7da; padding: 20px; border-radius: 5px; border-left: 4px solid #f5c6cb;">
                <h3 style="color: #721c24; margin-bottom: 10px;">❌ Error Creating Sample Data</h3>
                <p style="color: #721c24;">${error.message}</p>
            </div>
        `;
        showLoading('userLoading', false);
    }
}

// ==================== Initialize ====================
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Application loaded. API URL:', API_URL);
    loadUsers();
});
