<?php
$data['error_message'] = Lang('error_empty_login');
$loginId = $escapeObj->stringEscape($_POST['login_id']);
$loginPassword = trim($_POST['login_password']);
$loginPasswordENC = sha1($loginPassword . md5($loginPassword));

$userId = getUserId($conn, $loginId);

if ($userId) {
    $adminLogin = isset($_SESSION[$config['spf'] . 'admin_login']) ? $_SESSION[$config['spf'] . 'admin_login'] : false;
    $adminQ = '';
    if ($adminLogin) {
        $adminQ = "AND type IN ('admin','moderator','delivery')";
    }
    $sellerLogin = isset($_SESSION[$config['spf'] . 'seller_login']) ? $_SESSION[$config['spf'] . 'seller_login'] : false;
    if ($sellerLogin) {
        $adminQ = "AND type IN ('seller')";
    }
    $query = $conn->query("SELECT id,username,email_verified,active FROM " . DB_ACCOUNTS . " WHERE id=$userId AND password='$loginPasswordENC' $adminQ");
    $data['error_message'] = Lang('error_bad_login');

    if ($query->num_rows == 1) {
        $fetch = $query->fetch_array(MYSQLI_ASSOC);
        $continue = true;

        if ($fetch['active'] == 0) {
            $continue = false;
            $data['error_message'] = Lang('error_user_suspend');
        }

        if ($config['email_verification'] == 1 && $fetch['email_verified'] == 0) {
            $continue = false;
            $data['error_message'] = Lang('error_verify_email');
        }

        if ($continue == true) {
            $_SESSION[$config['spf'] . 'user_id'] = $fetch['id'];
            $_SESSION[$config['spf'] . 'user_pass'] = $loginPasswordENC;
            if ($sellerLogin) {
                $_SESSION[$config['spf'] . 'pos_uniqid'] = uniqid();
            }
            if (isset($_POST['keep_logged_in']) && $_POST['keep_logged_in'] == true) {
                setcookie('sk_u_i', $_SESSION[$config['spf'] . 'user_id'], time() + (60 * 60 * 24 * 7));
                setcookie('sk_u_p', $_SESSION[$config['spf'] . 'user_pass'], time() + (60 * 60 * 24 * 7));
            }

            $data['status'] = 200;
            $redirect = $_SESSION[$config['spf'] . 'redirect_url'];
            if (isset($redirect)) {
                $data['redirect_url'] = $redirect;
                unset($_SESSION[$config['spf'] . 'redirect_url']);
            } else {
                $data['redirect_url'] = smoothLink('index.php?tab1=home');
            }
        }
    } else {
        $data['error_message'] = Lang('incorrect_password');
    }
} else {
    $data['error_message'] = Lang('no_user_found');
}