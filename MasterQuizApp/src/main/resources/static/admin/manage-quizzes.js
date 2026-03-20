const API_BASE_URL = '/api';

async function loadQuizzes() {
    const quizListContainer = document.getElementById('quiz-list-container');
    quizListContainer.innerHTML = '<p>Loading quizzes...</p>';
    try {
        const response = await fetch(`${API_BASE_URL}/tests`);
        if (!response.ok) throw new Error('Failed to fetch quizzes.');
        const quizzes = await response.json();
        if (quizzes.length === 0) {
            quizListContainer.innerHTML = '<p>No quizzes found. Create one above!</p>';
            return;
        }
        const list = document.createElement('ul');
        list.className = 'item-list';
        quizzes.forEach(quiz => {
            const listItem = document.createElement('li');
            listItem.innerHTML = `<span>${quiz.name}</span><button class="btn-danger" onclick="handleDeleteQuiz(${quiz.id}, '${quiz.name}')">Delete</button>`;
            list.appendChild(listItem);
        });
        quizListContainer.innerHTML = '';
        quizListContainer.appendChild(list);
    } catch (error) {
        quizListContainer.innerHTML = '<p style="color: red;">Could not load quizzes.</p>';
    }
}

async function handleCreateQuiz(event) {
    event.preventDefault();
    const createButton = event.target.querySelector('button[type="submit"]');
    const quizNameInput = document.getElementById('quiz-name');
    const quizImageInput = document.getElementById('quiz-image');
    const quizName = quizNameInput.value.trim();
    if (!quizName) { alert('Please enter a quiz name.'); return; }

    createButton.disabled = true;
    createButton.classList.add('loading');
    createButton.textContent = 'Creating...';

    const formData = new FormData();
    formData.append('name', quizName);
    if (quizImageInput.files[0]) {
        formData.append('imageFile', quizImageInput.files[0]);
    }

    try {
        const response = await fetch(`${API_BASE_URL}/admin/tests`, { method: 'POST', body: formData });
        if (!response.ok) throw new Error('Failed to create quiz.');
        quizNameInput.value = '';
        quizImageInput.value = '';
        alert(`Quiz '${quizName}' created successfully!`);
        loadQuizzes();
    } catch (error) {
        alert(error.message);
    } finally {
        createButton.disabled = false;
        createButton.classList.remove('loading');
        createButton.textContent = 'Create Quiz';
    }
}

async function handleDeleteQuiz(testId, testName) {
    if (!confirm(`Are you sure you want to delete the "${testName}" quiz? This will delete all its questions and results.`)) return;
    try {
        const response = await fetch(`${API_BASE_URL}/admin/tests/${testId}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete quiz.');
        alert(`Quiz '${testName}' deleted successfully!`);
        loadQuizzes();
    } catch (error) {
        alert(error.message);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadQuizzes();
    document.getElementById('create-quiz-form').addEventListener('submit', handleCreateQuiz);
});