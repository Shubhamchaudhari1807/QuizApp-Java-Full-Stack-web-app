// Using a relative URL, which works for both local and deployed environments
const API_BASE_URL = '/api';

/**
 * Fetches the list of available quiz tests and creates cards for them.
 */
function loadTests() {
    const packageWrapper = document.querySelector('.package-wrapper');
    packageWrapper.innerHTML = '<p style="color: #333;">Loading quizzes...</p>';

    fetch(`${API_BASE_URL}/tests`)
        .then(response => response.json())
        .then(quizzes => {
            packageWrapper.innerHTML = '';
            if (quizzes.length === 0) {
                packageWrapper.innerHTML = '<p style="color: #333;">No quizzes found.</p>';
                return;
            }
            quizzes.forEach(quiz => {
                const card = document.createElement('div');
                card.className = 'package-card';
                // Use the Cloudinary URL if it exists, otherwise use a default local image
                const imageUrl = quiz.imageUrl ? quiz.imageUrl : './images/default-quiz.png';
                card.innerHTML = `
                    <img src="${imageUrl}" alt="${quiz.name}">
                    <p class="card-heading">${quiz.name}</p>
                    <p class="card-text">A challenging quiz to test your ${quiz.name} skills.</p>
                    <button class="btn" onclick="startQuiz(${quiz.id})">Take Quiz</button>
                `;
                packageWrapper.appendChild(card);
            });
        })
        .catch(error => {
            console.error('Error fetching tests:', error);
            packageWrapper.innerHTML = '<p style="color: red;">Could not load quizzes.</p>';
        });
}

/**
 * Fetches the top 3 performers and creates cards for them.
 */
function loadTopPerformers() {
    const performerGrid = document.querySelector('.performer-grid');
    performerGrid.innerHTML = '<p style="color: #333;">Loading top performers...</p>';

    fetch(`${API_BASE_URL}/results/top-performers`)
        .then(response => response.json())
        .then(performers => {
            performerGrid.innerHTML = ''; // Clear loading message
            if (performers.length === 0) {
                performerGrid.innerHTML = '<p style="color: #333;">No performers yet. Be the first!</p>';
                return;
            }
            performers.forEach(performer => {
                const card = document.createElement('div');
                card.className = 'performer-card';
                // Use a default image if the user has no profile picture
                const imageUrl = performer.profileImageUrl ? performer.profileImageUrl : './images/default-avatar.png';
                card.innerHTML = `
                    <img src="${imageUrl}" alt="Photo of ${performer.username}">
                    <h3 class="performer-name">${performer.username}</h3>
                    <p class="performer-score">Score: ${performer.score} in ${performer.testName}</p>
                `;
                performerGrid.appendChild(card);
            });
        })
        .catch(error => {
            console.error('Error fetching top performers:', error);
            performerGrid.innerHTML = '<p style="color: red;">Could not load top performers.</p>';
        });
}

/**
 * Redirects to the quiz page with the selected test ID.
 */
function startQuiz(testId) {
    window.location.href = `quiz.html?testId=${testId}`;
}

// Replace the existing DOMContentLoaded listener at the end of index.js

document.addEventListener('DOMContentLoaded', () => {
    loadTests();
    loadTopPerformers();

    // --- CORRECTED Hamburger Menu Logic ---
    const hamburger = document.getElementById('hamburger-menu');
    const mobileNav = document.getElementById('mobile-nav');

    if (hamburger && mobileNav) {
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