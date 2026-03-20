// The base URL for our main site's API
const API_BASE_URL = '/api';

// This variable will store the ID of the currently selected quiz
let selectedTestId = null;

/**
 * Fetches all existing quizzes and populates both the list and the dropdown.
 */
async function loadQuizzes() {
    const quizListContainer = document.getElementById('quiz-list-container');
    const quizSelect = document.getElementById('quiz-select');

    quizListContainer.innerHTML = '<p>Loading quizzes...</p>';
    if(quizSelect) quizSelect.innerHTML = '<option value="">Loading quizzes...</option>';

    try {
        const response = await fetch(`${API_BASE_URL}/tests`);
        if (!response.ok) throw new Error('Failed to fetch quizzes.');
        const quizzes = await response.json();

        // Clear loading messages
        quizListContainer.innerHTML = '';
        if(quizSelect) quizSelect.innerHTML = '';

        if (quizzes.length === 0) {
            quizListContainer.innerHTML = '<p>No quizzes found. Create one!</p>';
            if(quizSelect) quizSelect.innerHTML = '<option value="">No quizzes available</option>';
            return;
        }

        // Populate the dropdown menu in the "Add Question" form
        if(quizSelect) quizSelect.innerHTML = '<option value="">-- Please select a quiz --</option>';
        const list = document.createElement('ul');
        list.className = 'item-list';

        quizzes.forEach(quiz => {
            if(quizSelect) {
                const option = document.createElement('option');
                option.value = quiz.id;
                option.textContent = quiz.name;
                quizSelect.appendChild(option);
            }

            // Populate the manageable list with delete buttons
            const listItem = document.createElement('li');
            listItem.setAttribute('data-testid', quiz.id);
            listItem.innerHTML = `
                <span class="quiz-name" onclick="selectQuiz(${quiz.id}, '${quiz.name}')">${quiz.name}</span>
                <button class="btn-danger" onclick="handleDeleteQuiz(event, ${quiz.id}, '${quiz.name}')">Delete</button>
            `;
            list.appendChild(listItem);
        });

        quizListContainer.appendChild(list);

    } catch (error) {
        console.error('Error loading quizzes:', error);
        quizListContainer.innerHTML = '<p style="color: red;">Could not load quizzes.</p>';
        if(quizSelect) quizSelect.innerHTML = '<option value="">Error loading quizzes</option>';
    }
}

/**
 * Called when a user clicks a quiz name in the list to select it.
 */
function selectQuiz(testId, testName) {
    selectedTestId = testId;

    // Highlight the selected quiz in the list
    document.querySelectorAll('#quiz-list-container li').forEach(li => {
        li.classList.toggle('selected', li.getAttribute('data-testid') == testId);
    });

    // Set the selected quiz in the 'Add Question' form's dropdown
    const quizSelect = document.getElementById('quiz-select');
    if(quizSelect) {
        quizSelect.value = testId;
    }
}


/**
 * Handles the submission of the "Create New Quiz" form.
 */
async function handleCreateQuiz(event) {
    event.preventDefault();
    const createButton = event.target.querySelector('button[type="submit"]');
    const quizNameInput = document.getElementById('quiz-name');
    const quizImageInput = document.getElementById('quiz-image');

    const quizName = quizNameInput.value.trim();
    const imageFile = quizImageInput.files[0];

    if (!quizName) { alert('Please enter a quiz name.'); return; }

    createButton.disabled = true;
    createButton.classList.add('loading');
    createButton.textContent = 'Creating...';

    const formData = new FormData();
    formData.append('name', quizName);
    if (imageFile) formData.append('imageFile', imageFile);

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

/**
 * Handles the click of a "Delete" button for a quiz.
 */
async function handleDeleteQuiz(event, testId, testName) {
    event.stopPropagation(); // Prevents the selectQuiz function from being called
    if (!confirm(`Are you sure you want to delete "${testName}"? This also deletes its questions and results.`)) return;
    try {
        const response = await fetch(`${API_BASE_URL}/admin/tests/${testId}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete quiz.');
        alert(`Quiz '${testName}' deleted successfully!`);
        loadQuizzes();
    } catch (error) {
        alert(error.message);
    }
}

/**
 * Handles the submission of the "Add New Question" form.
 */
async function handleCreateQuestion(event) {
    event.preventDefault();
    const createButton = event.target.querySelector('button[type="submit"]');
    const selectedTestId = document.getElementById('quiz-select').value;

    if (!selectedTestId) { alert('Please select a quiz.'); return; }

    const questionText = document.getElementById('question-text').value;
    const optionInputs = document.querySelectorAll('.option-text');
    const correctOptionRadio = document.querySelector('input[name="correct-option"]:checked');

    if (!correctOptionRadio) { alert('Please select the correct answer.'); return; }

    createButton.disabled = true;
    createButton.classList.add('loading');
    createButton.textContent = 'Adding...';

    const correctOptionIndex = parseInt(correctOptionRadio.value);
    const options = Array.from(optionInputs).map((input, index) => ({
        optionText: input.value,
        correct: index === correctOptionIndex
    }));
    const questionPayload = { questionText, options };

    try {
        const response = await fetch(`${API_BASE_URL}/admin/tests/${selectedTestId}/questions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(questionPayload),
        });
        if (!response.ok) throw new Error('Failed to add question.');

        alert('Question added successfully!');
        document.getElementById('create-question-form').reset();
    } catch (error) {
        alert(error.message);
    } finally {
        createButton.disabled = false;
        createButton.classList.remove('loading');
        createButton.textContent = 'Add Question';
    }
}

// Main entry point that runs when the page is loaded.
document.addEventListener('DOMContentLoaded', () => {
    loadQuizzes();
    document.getElementById('create-quiz-form').addEventListener('submit', handleCreateQuiz);
    document.getElementById('create-question-form').addEventListener('submit', handleCreateQuestion);
});