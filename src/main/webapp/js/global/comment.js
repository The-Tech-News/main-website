// Client-side validation and UX for comment form
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('comment-form');
    if (!form)
        return;

    form.addEventListener('submit', function (e) {
        const ta = document.getElementById('comment-content');
        if (!ta)
            return;
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
window.formatText = function (type) {
    const textarea = document.getElementById("comment-content");
    if (!textarea)
        return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;

    const selectedText = textarea.value.substring(start, end);

    let formatted = selectedText;

    if (type === "bold") {
        formatted = `**${selectedText}**`;
    } else if (type === "italic") {
        formatted = `*${selectedText}*`;
    }

    textarea.value =
            textarea.value.substring(0, start) +
            formatted +
            textarea.value.substring(end);

    textarea.focus();
};