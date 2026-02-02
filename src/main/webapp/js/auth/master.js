/* global CryptoJS */

'use strict';

document.getElementById('hashForm').addEventListener('submit', function (e) {
    e.preventDefault(); // Prevent form submission

    const password = document.getElementById('password').value.trim();

    // Input validation
    if (!password) {
        alert("Please enter a password.");
        return;
    }

    // Generate MD5 hash
    const hash = CryptoJS.MD5(password).toString();

    // Display the hash
    document.getElementById('hashOutput').textContent = hash;
});