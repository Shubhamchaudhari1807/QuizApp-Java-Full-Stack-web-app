document.addEventListener('DOMContentLoaded', () => {
    const errorMessageEl = document.getElementById('error-message');
    const params = new URLSearchParams(window.location.search);

    if (params.has('error')) {
        errorMessageEl.textContent = 'Invalid username or password.';
    }

    if (params.has('logout')) {
        errorMessageEl.textContent = 'You have been logged out successfully.';
        errorMessageEl.classList.remove('error'); // Remove error class if present
        errorMessageEl.classList.add('success'); // Add success class
    }
});

// --- Hamburger Menu Logic ---
// This should be inside the DOMContentLoaded listener with your other functions
document.addEventListener('DOMContentLoaded', () => {
    // Make sure your loadTests() and loadTopPerformers() calls are also inside here

    const hamburger = document.getElementById('hamburger-menu');
    const mobileNav = document.getElementById('mobile-nav');

    if (hamburger && mobileNav) { // Check if the elements exist
        hamburger.addEventListener('click', () => {
            mobileNav.classList.toggle('open');
        });

        // Close menu when a link is clicked
        mobileNav.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                mobileNav.classList.remove('open');
            });
        });
    }
});