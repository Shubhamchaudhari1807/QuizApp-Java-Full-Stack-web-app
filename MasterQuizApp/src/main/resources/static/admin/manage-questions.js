const API_BASE_URL = '/api';

async function loadQuizzesIntoSelect() {
    const quizSelect = document.getElementById('quiz-select');
    quizSelect.innerHTML = '<option value="">Loading quizzes...</option>';
    try {
        const response = await fetch(`${API_BASE_URL}/tests`);
        if (!response.ok) throw new Error('Failed to fetch quizzes.');
        const quizzes = await response.json();
        if (quizzes.length === 0) {
            quizSelect.innerHTML = '<option value="">No quizzes found. Create one first!</option>';
            return;
        }
        quizSelect.innerHTML = '<option value="">-- Please select a quiz --</option>';
        quizzes.forEach(quiz => {
            const option = document.createElement('option');
            option.value = quiz.id;
            option.textContent = quiz.name;
            quizSelect.appendChild(option);
        });
    } catch (error) {
        quizSelect.innerHTML = '<option value="">Error loading quizzes.</option>';
    }
}

async function handleCreateQuestion(event) {
    event.preventDefault();
    const createButton = event.target.querySelector('button[type="submit"]');
    const selectedTestId = document.getElementById('quiz-select').value;
    if (!selectedTestId) {
        alert('Please select a quiz from the dropdown menu first.');
        return;
    }
    const questionText = document.getElementById('question-text').value;
    const optionInputs = document.querySelectorAll('.option-text');
    const correctOptionRadio = document.querySelector('input[name="correct-option"]:checked');
    if (!correctOptionRadio) {
        alert('Please select the correct answer for one of the options.');
        return;
    }

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
        document.getElementById('quiz-select').value = "";
    } catch (error) {
        alert(error.message);
    } finally {
        createButton.disabled = false;
        createButton.classList.remove('loading');
        createButton.textContent = 'Add Question';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadQuizzesIntoSelect();
    document.getElementById('create-question-form').addEventListener('submit', handleCreateQuestion);
});