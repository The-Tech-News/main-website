'use strict';
function getCookieByName(name) {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        cookie = cookie.trim();
        if (cookie.startsWith(name + '=')) {
            return cookie.substring(name.length + 1);
        }
    }
    return null;
}

document.addEventListener('DOMContentLoaded', () => {
    const isLoginValue = getCookieByName('isLogin');
    if (isLoginValue === null || isLoginValue === 'false') {
        document.getElementById("signed-in-tab").hidden = true;
        document.getElementById("not-signed-in-tab").hidden = false;
        document.getElementById("not-signed-in-tab-2").hidden = false;
    } else {
        document.getElementById("signed-in-tab").hidden = false;
        document.getElementById("not-signed-in-tab").hidden = true;
        document.getElementById("not-signed-in-tab-2").hidden = true;
    }
});