// Client-side validation and UX for comment form
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('comment-form');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        const ta = document.getElementById('comment-content');
        if (!ta) return;
        const v = ta.value.trim();
        if (!v) {
            e.preventDefault();
            ta.focus();
            alert('Please write a comment before posting.');
            return false;
        }
        // allow submit (server will validate again)
    });
});