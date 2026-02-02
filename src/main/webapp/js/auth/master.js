/* global CryptoJS */

'use strict';

$('#hashForm').submit(function() {
    const rawPass = document.getElementById('password').value;
    const md5Str = CryptoJS.MD5(rawPass).toString();

    document.getElementById('password').value = md5Str;
    return true; // return false to cancel form action
});