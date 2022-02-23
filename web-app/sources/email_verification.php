<?php

$redirect = true;

if (!empty($_GET['email']) && !empty($_GET['key'])) {
    $escapeObj = new \iCms\Escape();
    $email = $escapeObj->stringEscape($_GET['email']);
    $key = $escapeObj->stringEscape($_GET['key']);

    $query = $conn->query("SELECT id,password FROM " . DB_ACCOUNTS . " WHERE email='$email' AND email_verification_key='$key'");

    if ($query->num_rows == 1) {
        $fetch = $query->fetch_array(MYSQLI_ASSOC);
        $query_two = $conn->query("UPDATE " . DB_ACCOUNTS . " SET email_verified=1 WHERE id=" . $fetch['id']);
    }
}

if ($redirect) {
    header('Location: ' . smoothLink('index.php?tab1=login'));
}