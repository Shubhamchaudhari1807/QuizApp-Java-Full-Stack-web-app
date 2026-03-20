const API_BASE_URL = '/api';

let questions = [];
let currentQuestionIndex = 0;
let score = 0;
let timerInterval;

async function initializeQuiz() {
    const params = new URLSearchParams(window.location.search);
    const testId = params.get('testId');

    if (!testId) {
        document.getElementById('question-text').textContent = "No test ID provided. Please select a quiz from the home page.";
        return;
    }

    try {
        const [testResponse, questionsResponse] = await Promise.all([
            fetch(`${API_BASE_URL}/tests/${testId}`),
            fetch(`${API_BASE_URL}/tests/${testId}/questions`)
        ]);

        if (!testResponse.ok || !questionsResponse.ok) {
            throw new Error('Failed to load quiz data.');
        }

        const testDetails = await testResponse.json();
        questions = await questionsResponse.json();

        document.title = `${testDetails.name} Quiz`;
        document.getElementById('quiz-title').textContent = `${testDetails.name} Quiz`;
        document.getElementById('total-questions').textContent = questions.length;

        displayQuestion();

    } catch (error) {
        console.error('Initialization error:', error);
        document.getElementById('question-text').textContent = "Could not load the quiz. Please try again later.";
    }
}

function displayQuestion() {
    if (currentQuestionIndex >= questions.length) {
        showResults();
        return;
    }

    const currentQuestion = questions[currentQuestionIndex];
    document.getElementById('question-text').textContent = currentQuestion.questionText;
    document.getElementById('current-question').textContent = currentQuestionIndex + 1;

    const optionsContainer = document.getElementById('options-container');
    optionsContainer.innerHTML = '';

    currentQuestion.options.forEach(option => {
        const button = document.createElement('button');
        button.textContent = option.optionText;
        button.className = 'option';
        button.onclick = () => checkAnswer(option.correct, button);
        optionsContainer.appendChild(button);
    });

    document.getElementById('next-button').style.display = 'none';
    startTimer();
}

function checkAnswer(isCorrect, selectedButton) {
    clearInterval(timerInterval);

    const optionButtons = document.querySelectorAll('.option');
    optionButtons.forEach(button => button.disabled = true);

    if (isCorrect) {
        score++;
        if (selectedButton) selectedButton.classList.add('correct');
    } else {
        if (selectedButton) selectedButton.classList.add('incorrect');

        const correctOption = questions[currentQuestionIndex].options.find(opt => opt.correct);
        if (correctOption) {
            optionButtons.forEach(btn => {
                if (btn.textContent === correctOption.optionText) {
                    btn.classList.add('correct');
                }
            });
        }
    }

    document.getElementById('next-button').style.display = 'block';
}

function startTimer() {
    let timeLeft = 30;
    const timerEl = document.getElementById('timer');
    timerEl.textContent = timeLeft;

    timerInterval = setInterval(() => {
        timeLeft--;
        timerEl.textContent = timeLeft;
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            checkAnswer(false, null);
        }
    }, 1000);
}

function nextQuestion() {
    currentQuestionIndex++;
    displayQuestion();
}

async function showResults() {
    try {
        const testId = new URLSearchParams(window.location.search).get('testId');
        const resultData = {
            testId: parseInt(testId),
            score: score,
            totalQuestions: questions.length
        };

        await fetch(`${API_BASE_URL}/results`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(resultData)
        });

    } catch (error) {
        console.error('Error while saving quiz result:', error);
    }

    localStorage.setItem('quizScore', score);
    localStorage.setItem('totalQuestions', questions.length);
    window.location.href = '/result.html';
}

document.addEventListener('DOMContentLoaded', initializeQuiz);
document.getElementById('next-button').addEventListener('click', nextQuestion);