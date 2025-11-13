// Registration-specific JavaScript
document.addEventListener('DOMContentLoaded', function() {
    initializeRegistrationForm();
});

function initializeRegistrationForm() {
    const registrationForm = document.getElementById('registrationForm');
    const submitBtn = document.getElementById('submitBtn');
    const mobileNotice = document.getElementById('mobileNotice');

    // Detect if coming from mobile app
    const urlParams = new URLSearchParams(window.location.search);
    const fromMobile = urlParams.get('fromMobile');

    if (fromMobile === 'true') {
        mobileNotice.style.display = 'block';
    }

    // Form submission
    registrationForm.addEventListener('submit', function(e) {
        e.preventDefault();

        if (validateForm()) {
            submitRegistration();
        }
    });

    // Real-time validation
    document.getElementById('fullName').addEventListener('input', validateName);
    document.getElementById('email').addEventListener('input', validateEmail);
    document.getElementById('phone').addEventListener('input', validatePhone);
    document.getElementById('password').addEventListener('input', validatePassword);
    document.getElementById('confirmPassword').addEventListener('input', validatePasswordConfirm);

    // Password strength indicator
    document.getElementById('password').addEventListener('input', updatePasswordStrength);
}

function validateForm() {
    let isValid = true;

    // Reset errors
    document.querySelectorAll('.error-message').forEach(error => {
        error.style.display = 'none';
    });

    // Validate each field
    if (!validateName()) isValid = false;
    if (!validateEmail()) isValid = false;
    if (!validatePhone()) isValid = false;
    if (!validatePassword()) isValid = false;
    if (!validatePasswordConfirm()) isValid = false;

    return isValid;
}

function validateName() {
    const name = document.getElementById('fullName').value.trim();
    const errorElement = document.getElementById('nameError');

    if (name.length < 2) {
        showError('nameError', 'Please enter your full name (min 2 characters)');
        return false;
    } else if (name.length > 50) {
        showError('nameError', 'Name is too long (max 50 characters)');
        return false;
    } else {
        hideError('nameError');
        return true;
    }
}

function validateEmail() {
    const email = document.getElementById('email').value.trim();
    const errorElement = document.getElementById('emailError');
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email) {
        showError('emailError', 'Email is required');
        return false;
    } else if (!emailRegex.test(email)) {
        showError('emailError', 'Please enter a valid email address');
        return false;
    } else {
        hideError('emailError');
        return true;
    }
}

function validatePhone() {
    const phone = document.getElementById('phone').value.trim();
    const errorElement = document.getElementById('phoneError');
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;

    if (!phone) {
        showError('phoneError', 'Phone number is required');
        return false;
    } else if (!phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''))) {
        showError('phoneError', 'Please enter a valid phone number');
        return false;
    } else {
        hideError('phoneError');
        return true;
    }
}

function validatePassword() {
    const password = document.getElementById('password').value;
    const errorElement = document.getElementById('passwordError');

    if (password.length < 6) {
        showError('passwordError', 'Password must be at least 6 characters');
        return false;
    } else {
        hideError('passwordError');
        return true;
    }
}

function validatePasswordConfirm() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const errorElement = document.getElementById('confirmPasswordError');

    if (password !== confirmPassword) {
        showError('confirmPasswordError', 'Passwords do not match');
        return false;
    } else {
        hideError('confirmPasswordError');
        return true;
    }
}

function updatePasswordStrength() {
    const password = document.getElementById('password').value;
    let strengthElement = document.getElementById('passwordStrength');

    if (!strengthElement) {
        strengthElement = document.createElement('div');
        strengthElement.id = 'passwordStrength';
        strengthElement.className = 'password-strength';
        document.getElementById('password').parentNode.appendChild(strengthElement);
    }

    let strength = 'Weak';
    let strengthClass = 'strength-weak';

    if (password.length >= 8) {
        strength = 'Medium';
        strengthClass = 'strength-medium';
    }

    if (password.length >= 12 && /[A-Z]/.test(password) && /[0-9]/.test(password) && /[^A-Za-z0-9]/.test(password)) {
        strength = 'Strong';
        strengthClass = 'strength-strong';
    }

    strengthElement.textContent = `Strength: ${strength}`;
    strengthElement.className = `password-strength ${strengthClass}`;
}

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    errorElement.textContent = message;
    errorElement.style.display = 'block';
}

function hideError(elementId) {
    const errorElement = document.getElementById(elementId);
    errorElement.style.display = 'none';
}

function submitRegistration() {
    const submitBtn = document.getElementById('submitBtn');
    const formData = {
        fullName: document.getElementById('fullName').value.trim(),
        email: document.getElementById('email').value.trim(),
        phone: document.getElementById('phone').value.trim(),
        password: document.getElementById('password').value
    };

    // Show loading state
    submitBtn.innerHTML = '<span class="loading"></span> Creating Account...';
    submitBtn.disabled = true;

    // Simulate API call (replace with actual Firebase/backend integration)
    simulateRegistrationAPI(formData)
        .then(response => {
            // Success
            if (typeof showNotification === 'function') {
                showNotification('✅', 'Registration successful! You can now log in to the mobile app.', 'success');
            } else {
                alert('Registration successful! You can now log in to the mobile app.');
            }

            // Redirect to main page after success
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 2000);
        })
        .catch(error => {
            // Error
            if (typeof showNotification === 'function') {
                showNotification('❌', error.message, 'error');
            } else {
                alert('Registration failed: ' + error.message);
            }

            // Reset button
            submitBtn.textContent = 'Create Account';
            submitBtn.disabled = false;
        });
}

function simulateRegistrationAPI(formData) {
    return new Promise((resolve, reject) => {
        // Simulate API delay
        setTimeout(() => {
            // Simulate random success (90% success rate for demo)
            if (Math.random() > 0.1) {
                resolve({
                    success: true,
                    message: 'User registered successfully',
                    userId: 'user_' + Date.now()
                });
            } else {
                reject({
                    success: false,
                    message: 'Email already exists or server error'
                });
            }
        }, 2000);
    });
}

// Add enter key support for form submission
document.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        const focused = document.activeElement;
        if (focused && focused.form && focused.form.id === 'registrationForm') {
            if (validateForm()) {
                submitRegistration();
            }
        }
    }
});

// Export for potential module use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeRegistrationForm,
        validateForm,
        submitRegistration
    };
}