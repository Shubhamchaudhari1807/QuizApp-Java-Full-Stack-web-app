document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    const messageEl = document.getElementById('message');
    const API_BASE_URL = '/api/auth'; // Using relative URL

    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Prevent the default form submission

        const messageEl = document.getElementById('message');
        messageEl.textContent = 'Processing... Please wait.';
        messageEl.className = '';

        const profileImageFile = document.getElementById('profileImage').files[0];
        let processedFile = profileImageFile;

        // --- Image Compression Logic ---
        if (profileImageFile) {
            console.log(`Original file size: ${(profileImageFile.size / 1024 / 1024).toFixed(2)} MB`);

            // --- NEW, FASTER Compression Options ---
            const options = {
                maxSizeMB: 0.5,         // Target a smaller file size (500KB is plenty for a profile pic)
                maxWidthOrHeight: 800,  // Resize to a max of 800px (faster than 1024px)
                useWebWorker: true,
                initialQuality: 0.7     // Start compression at a slightly lower quality to speed it up
            }

            try {
                messageEl.textContent = 'Optimizing image...';
                processedFile = await imageCompression(profileImageFile, options);
                console.log(`Compressed file size: ${(processedFile.size / 1024 / 1024).toFixed(2)} MB`);
            } catch (error) {
                console.error('Image compression error:', error);
                messageEl.textContent = 'Error compressing image. Please try another file.';
                messageEl.className = 'error';
                return; // Stop the submission
            }
        }
        // --- End of Logic ---


        // Now we create the FormData with the (potentially compressed) file
        const formData = new FormData();
        const userDetails = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        };

        formData.append('user', new Blob([JSON.stringify(userDetails)], { type: "application/json" }));

        if (processedFile) {
            formData.append('profileImage', processedFile);
        }

        messageEl.textContent = 'Registering user...';

        try {
            const response = await fetch(`${API_BASE_URL}/register`, {
                method: 'POST',
                body: formData,
            });

            if (response.ok) {
                const result = await response.text();
                messageEl.textContent = result + " Redirecting to login...";
                messageEl.className = 'success';
                setTimeout(() => { window.location.href = 'login.html'; }, 2000);
            } else {
                const errorText = await response.text();
                messageEl.textContent = 'Error: ' + errorText;
                messageEl.className = 'error';
            }
        } catch (error) {
            console.error('Registration failed:', error);
            messageEl.textContent = 'Registration failed. Please try again later.';
            messageEl.className = 'error';
        }
    });
});