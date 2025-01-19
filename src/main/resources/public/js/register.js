document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("register-form");
    const message = document.getElementById("message");

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!username || !password) {
            message.textContent = "Please fill in all fields.";
            message.style.color = "red";
            return;
        }

        const response = await fetch("/api/register", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();
        message.textContent = result.message;
        message.style.color = response.ok ? "green" : "red";
    });
});
