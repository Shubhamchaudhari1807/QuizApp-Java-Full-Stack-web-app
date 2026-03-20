const API_BASE_URL = '/api';
let generatedQuestions = [];

async function loadQuizzesIntoSelect() {
    const quizSelect = document.getElementById('quiz-select-ai');
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

async function handleGenerate(event) {
    event.preventDefault();
    const generateBtn = event.target.querySelector('button[type="submit"]');
    const quizSelect = document.getElementById('quiz-select-ai');
    const selectedTestId = quizSelect.value;
    const selectedTestName = quizSelect.options[quizSelect.selectedIndex].text;
    const questionCount = document.getElementById('question-count').value;

    if (!selectedTestId) {
        alert('Please select a quiz.');
        return;
    }

    generateBtn.disabled = true;
    generateBtn.textContent = 'Generating...';
    document.getElementById('ai-results-container').innerHTML = '<p>Generating questions with AI, please wait...</p>';

    generatedQuestions = [];

    try {
        const response = await fetch(`${API_BASE_URL}/admin/questions/generate-ai`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ topic: selectedTestName, count: parseInt(questionCount) })
        });
        if (!response.ok) throw new Error('AI generation failed.');
        generatedQuestions = await response.json();
        displayGeneratedQuestions(selectedTestId);
    } catch (error) {
        console.error('AI Generation Error:', error);
        document.getElementById('ai-results-container').innerHTML = `<p style="color: red;">${error.message}</p>`;
    } finally {
        generateBtn.disabled = false;
        generateBtn.textContent = 'Generate Questions';
    }
}

function displayGeneratedQuestions(testId) {
    const resultsContainer = document.getElementById('ai-results-container');
    resultsContainer.innerHTML = '';

    const reviewForm = document.createElement('form');
    reviewForm.id = 'review-questions-form';

    let questionHtml = '';
    generatedQuestions.forEach((q, index) => {
        let optionsHtml = q.options.map(opt => `<li>${opt.optionText} ${opt.correct ? '<strong>(Correct)</strong>' : ''}</li>`).join('');
        questionHtml += `
            <div class="card" style="margin-top: 20px;">
                <h4>Generated Question ${index + 1}</h4>
                <p><strong>${q.questionText}</strong></p>
                <ul>${optionsHtml}</ul>
            </div>
        `;
    });

    reviewForm.innerHTML = `
        <h3>Review and Save Generated Questions</h3>
        ${questionHtml}
        <button type="submit" class="btn-primary">Save All Questions</button>
    `;

    reviewForm.addEventListener('submit', (e) => handleSaveAllQuestions(e, testId));
    resultsContainer.appendChild(reviewForm);
}

async function handleSaveAllQuestions(event, testId) {
    event.preventDefault();
    const saveBtn = event.target.querySelector('button[type="submit"]');
    saveBtn.disabled = true;
    saveBtn.textContent = 'Saving...';

    try {
        for (const question of generatedQuestions) {
            const response = await fetch(`${API_BASE_URL}/admin/tests/${testId}/questions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(question)
            });
            if (!response.ok) {
                throw new Error('Failed to save one or more questions.');
            }
        }
        alert('All generated questions saved successfully!');
        document.getElementById('ai-results-container').innerHTML = '';
    } catch (error) {
        alert(error.message);
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = 'Save All Questions';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadQuizzesIntoSelect();
    document.getElementById('ai-generation-form').addEventListener('submit', handleGenerate);
});