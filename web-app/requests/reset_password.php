<?php
if (!empty($_POST['resetToken'])) {
    $resetPattern = $escapeObj->stringEscape($_POST['resetToken']);
    $error = Lang('error_try_again');
    $chkToken = isValidPasswordResetToken($resetPattern);
    if ($chkToken) {
        $newPass = $escapeObj->stringEscape($_POST['password']);
        $cnfPass = $escapeObj->stringEscape($_POST['confirm_password']);
        if ($newPass != $cnfPass) {
            $error = Lang('error_confirm_password');
        } else {
            $Password = trim($newPass);
            $PasswordENC = sha1($newPass . md5($newPass));
            $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET password='$PasswordENC' WHERE id=" . $chkToken['id']);
            if ($query) {
                $data['status'] = 200;
                $data['message'] = Lang('success_rested_password');
                $data['redirect_url'] = smoothLink('index.php?tab1=home');
            }
        }
    } else {
        $error = Lang('error_empty_registration');
    }
    $data['error_message'] = $error;
}