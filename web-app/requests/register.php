<?php
$proceed = false;
$data['error_message'] = Lang('error_bad_captcha');

if ($_POST['captcha'] == $_SESSION[$config['spf'] . 'captcha_key']) {
    $proceed = true;
}

if ($_POST['password'] != $_POST['confirm_password']) {
    $proceed = false;
    $data['error_message'] = Lang('error_confirm_password');
}

if ($proceed == true) {
    $data['error_message'] = Lang('error_empty_registration');

    if (filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
        $mailQuery = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE email='" . $escapeObj->stringEscape($_POST['email']) . "'");

        if ($mailQuery->num_rows > 0) {
            $data['error_message'] = Lang('error_email_exists');
        }
    }

    $registerObj = new \iCms\Users();
    $registerObj->setName($_POST['name']);
    $usernamegenrate = genrateUsername();
    $username = $usernamegenrate['username'];
    $registerObj->setUsername($username);
    $registerObj->setEmail($_POST['email']);
    $registerObj->setPhone($_POST['phone']);
    $registerObj->setPassword($_POST['password']);

    if ($register = $registerObj->register()) {
        $userDObj = new \iCms\User();
        $userinfo = $userDObj->getById($register);

        $to = $userinfo['email'];
        $subject = Lang('system_name') . ' - ' . Lang('reg_mail_subject');

        $headers = "From: " . $config['email'] . "\r\n";
        $headers .= "MIME-Version: 1.0\r\n";
        $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

        $themeData['mail_user_name'] = $userinfo['name'];
        $themeData['mail_user_uname'] = $userinfo['username'];
        $themeData['mail_user_password'] = $_POST['password'];

        $message = \iCms\UI::view('emails/email-register');

        mail($to, $subject, $message, $headers);

        $userinfo['verification_link'] = $config['site_url'] . '/?tab1=email-verification&email=' . $userinfo['email'] . '&key=' . $userinfo['email_verification_key'];

        $subject = Lang('system_name') . ' - ' . Lang('ver_mail_subject');
        $themeData['mail_verify_link'] = $userinfo['verification_link'];
        $message = \iCms\UI::view('emails/email-verification');
        mail($to, $subject, $message, $headers);

        if ($config['email_verification'] == 0) {
            $_SESSION[$config['spf'] . 'user_id'] = $register;
            $_SESSION[$config['spf'] . 'user_pass'] = sha1($_POST['password'] . md5($_POST['password']));

            $data['status'] = 200;
            $redirect = $_SESSION[$config['spf'] . 'redirect_url'];
            if (isset($redirect)) {
                $data['redirect_url'] = $redirect;
                unset($_SESSION[$config['spf'] . 'redirect_url']);
            } else {
                $data['redirect_url'] = smoothLink('index.php?tab1=home');
            }
        } else {
            $data['status'] = 400;
            $data['redirect_url'] = smoothLink('index.php?tab1=login');
            $data['error_message'] = Lang('verification_email_sent');
        }
    }
}