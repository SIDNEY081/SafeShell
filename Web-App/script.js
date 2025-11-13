// SafeShell Web App JavaScript - Mobile Enhanced
document.addEventListener('DOMContentLoaded', function() {
    console.log('SafeShell Web App Loaded - Mobile Optimized');

    // Initialize the app
    initializeApp();
});

function initializeApp() {
    // Set up viewport for mobile devices
    setViewport();

    // Detect device type
    const deviceType = detectDeviceType();
    console.log(`Device detected: ${deviceType}`);

    // Initialize interactive elements
    initializeFeatureCards();

    // Initialize status indicators
    initializeStatusIndicators();

    // Update dynamic content
    updateWelcomeMessage();
    updateCurrentTime();

    // Set up event listeners
    setupEventListeners();

    // Initialize service worker for PWA
    initializeServiceWorker();
}

function setViewport() {
    // Ensure proper viewport for mobile devices
    const viewport = document.querySelector('meta[name="viewport"]');
    if (viewport) {
        // Allow zoom but with reasonable limits
        viewport.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes');
    }
}

function detectDeviceType() {
    const userAgent = navigator.userAgent.toLowerCase();
    const isMobile = /mobile|android|iphone|ipad|ipod/.test(userAgent);
    const isTablet = /ipad|tablet/.test(userAgent) ||
                    (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1);

    if (isMobile) return 'mobile';
    if (isTablet) return 'tablet';
    return 'desktop';
}

function initializeFeatureCards() {
    const featureCards = document.querySelectorAll('.feature-card');
    const isTouchDevice = 'ontouchstart' in window || navigator.maxTouchPoints > 0;

    featureCards.forEach(card => {
        // Remove any existing event listeners
        card.replaceWith(card.cloneNode(true));
    });

    // Re-select after clone
    const freshCards = document.querySelectorAll('.feature-card');

    freshCards.forEach(card => {
        if (isTouchDevice) {
            // Touch device interactions
            card.addEventListener('touchstart', handleTouchStart, { passive: true });
            card.addEventListener('touchend', handleTouchEnd, { passive: true });
            card.addEventListener('touchcancel', handleTouchCancel, { passive: true });
        } else {
            // Mouse device interactions
            card.addEventListener('mouseenter', handleMouseEnter);
            card.addEventListener('mouseleave', handleMouseLeave);
            card.addEventListener('click', handleCardClick);
        }

        // Add accessibility
        card.setAttribute('role', 'button');
        card.setAttribute('tabindex', '0');
        card.addEventListener('keypress', handleKeyPress);
    });
}

function handleTouchStart(event) {
    const card = event.currentTarget;
    card.style.transform = 'scale(0.98)';
    card.style.transition = 'transform 0.1s ease';
}

function handleTouchEnd(event) {
    const card = event.currentTarget;
    card.style.transform = '';

    // Simulate click action after touch
    setTimeout(() => {
        showFeatureDetails(card);
    }, 150);
}

function handleTouchCancel(event) {
    const card = event.currentTarget;
    card.style.transform = '';
}

function handleMouseEnter(event) {
    const card = event.currentTarget;
    if (window.innerWidth >= 1024) { // Only on desktop
        card.style.transform = 'translateY(-5px)';
        card.style.boxShadow = '0 15px 35px rgba(0,0,0,0.15)';
        card.style.transition = 'all 0.3s ease';
    }
}

function handleMouseLeave(event) {
    const card = event.currentTarget;
    card.style.transform = '';
    card.style.boxShadow = '';
}

function handleCardClick(event) {
    const card = event.currentTarget;
    showFeatureDetails(card);
}

function handleKeyPress(event) {
    if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        const card = event.currentTarget;
        showFeatureDetails(card);
    }
}

function showFeatureDetails(card) {
    const featureTitle = card.querySelector('h3').textContent;
    const featureDescription = card.querySelector('p').textContent;

    // Create a simple notification
    showNotification(`🔍 ${featureTitle}`, featureDescription);

    // Add visual feedback
    card.style.backgroundColor = '#e6fffa';
    setTimeout(() => {
        card.style.backgroundColor = '';
    }, 1000);
}

function initializeStatusIndicators() {
    // Simulate loading user data with animation
    simulateUserDataLoad();

    // Update status periodically
    setInterval(updateStatusIndicators, 30000); // Every 30 seconds
}

function simulateUserDataLoad() {
    const statusElements = document.querySelectorAll('.status');
    let delay = 0;

    statusElements.forEach((status, index) => {
        setTimeout(() => {
            status.style.opacity = '0';
            status.style.transform = 'translateX(-20px)';

            setTimeout(() => {
                status.style.transition = 'all 0.5s ease';
                status.style.opacity = '1';
                status.style.transform = 'translateX(0)';

                // Add checkmark after load
                if (index === statusElements.length - 1) {
                    showNotification('✅', 'All systems loaded successfully!');
                }
            }, 200);
        }, delay);

        delay += 300;
    });
}

function updateStatusIndicators() {
    const statusDots = document.querySelectorAll('.dot');

    statusDots.forEach(dot => {
        // Simulate status check
        dot.style.animation = 'none';
        setTimeout(() => {
            dot.style.animation = 'pulse 2s infinite';
        }, 10);
    });
}

function updateWelcomeMessage() {
    const hour = new Date().getHours();
    let greeting;

    if (hour < 5) {
        greeting = "Late night";
    } else if (hour < 12) {
        greeting = "Good morning";
    } else if (hour < 17) {
        greeting = "Good afternoon";
    } else if (hour < 21) {
        greeting = "Good evening";
    } else {
        greeting = "Good night";
    }

    const welcomeElement = document.querySelector('.hero h2');
    if (welcomeElement) {
        welcomeElement.textContent = `${greeting}! Welcome to SafeShell Web`;
    }
}

function updateCurrentTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });

    // Create or update time display
    let timeElement = document.querySelector('.current-time');
    if (!timeElement) {
        timeElement = document.createElement('div');
        timeElement.className = 'current-time';
        timeElement.style.cssText = `
            text-align: center;
            margin-top: 10px;
            font-size: 0.9rem;
            color: #718096;
            opacity: 0.8;
        `;
        const heroSection = document.querySelector('.hero');
        if (heroSection) {
            heroSection.appendChild(timeElement);
        }
    }

    timeElement.textContent = `Current time: ${timeString}`;

    // Update every minute
    setTimeout(updateCurrentTime, 60000);
}

function setupEventListeners() {
    // Handle orientation changes
    window.addEventListener('orientationchange', function() {
        setTimeout(handleOrientationChange, 100);
    });

    // Handle resize events
    let resizeTimeout;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(handleResize, 250);
    });

    // Prevent zoom on double-tap (iOS)
    let lastTouchEnd = 0;
    document.addEventListener('touchend', function(event) {
        const now = Date.now();
        if (now - lastTouchEnd <= 300) {
            event.preventDefault();
        }
        lastTouchEnd = now;
    }, { passive: false });

    // Handle online/offline status
    window.addEventListener('online', handleOnlineStatus);
    window.addEventListener('offline', handleOfflineStatus);
}

function handleOrientationChange() {
    const isPortrait = window.innerHeight > window.innerWidth;
    console.log(`Orientation changed: ${isPortrait ? 'Portrait' : 'Landscape'}`);

    // Adjust layout if needed
    const container = document.querySelector('.container');
    if (container && !isPortrait) {
        // Landscape optimizations
        container.style.padding = '10px';
    } else if (container) {
        // Portrait reset
        container.style.padding = '';
    }
}

function handleResize() {
    console.log(`Window resized: ${window.innerWidth} x ${window.innerHeight}`);

    // Re-initialize feature cards on significant resize
    const currentWidth = window.innerWidth;
    if (Math.abs(currentWidth - (window.lastWidth || currentWidth)) > 100) {
        initializeFeatureCards();
        window.lastWidth = currentWidth;
    }
}

function handleOnlineStatus() {
    showNotification('🌐', 'Connection restored!', 'success');
    updateStatusIndicators();
}

function handleOfflineStatus() {
    showNotification('⚠️', 'You are currently offline', 'warning');
}

function showNotification(emoji, message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        padding: 15px 20px;
        border-radius: 10px;
        box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        z-index: 1000;
        border-left: 4px solid #667eea;
        transform: translateX(150%);
        transition: transform 0.3s ease;
        max-width: 300px;
        font-size: 0.9rem;
    `;

    if (type === 'success') {
        notification.style.borderLeftColor = '#48bb78';
    } else if (type === 'warning') {
        notification.style.borderLeftColor = '#ed8936';
    }

    notification.innerHTML = `
        <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 1.2rem;">${emoji}</span>
            <span>${message}</span>
        </div>
    `;

    document.body.appendChild(notification);

    // Animate in
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);

    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.style.transform = 'translateX(150%)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);

    // Allow manual close on click
    notification.addEventListener('click', () => {
        notification.style.transform = 'translateX(150%)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    });
}

function initializeServiceWorker() {
    // Add service worker for PWA capabilities (future enhancement)
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', function() {
            navigator.serviceWorker.register('/sw.js')
                .then(function(registration) {
                    console.log('ServiceWorker registration successful with scope: ', registration.scope);
                })
                .catch(function(error) {
                    console.log('ServiceWorker registration failed: ', error);
                });
        });
    }
}

// Utility function to check if element is in viewport
function isElementInViewport(el) {
    const rect = el.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}

// Add some basic error handling
window.addEventListener('error', function(e) {
    console.error('JavaScript Error:', e.error);
});

// Export functions for potential module use (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeApp,
        detectDeviceType,
        showNotification,
        updateWelcomeMessage
    };
}