<?php
if (!empty($_POST['email'])) {
    $forgotpassId = $escapeObj->stringEscape($_POST['email']);
    $forgotUserId = getUserId($conn, $forgotpassId);
    if ($forgotUserId) {
        $error = Lang('error_try_again');
        $query = $conn->query("SELECT id,password,username,email,name FROM " . DB_ACCOUNTS . " WHERE id=$forgotUserId AND type NOT IN ('admin','moderator') AND active=1");
        if ($query->num_rows == 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            $resetPattern = $fetch['id'] . '_' . $fetch['password'];
            $fetch['url'] = smoothLink('index.php?tab1=password_reset&tab2=' . $resetPattern);
            $to = $fetch['email'];
            $subject = Lang('app_name') . ' - ' . Lang('reset_password_label');

            $headers = "From: " . $config['email'] . "\r\n";
            $headers .= "MIME-Version: 1.0\r\n";
            $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

            $themeData['hey_label'] = Lang('hey_label');
            $themeData['mail_user_name'] = $fetch['name'];

            $themeData['res_mail_label'] = Lang('reset_password_label');
            $themeData['res_mail_label'] .= '<br><br>' . Lang('username_label') . ' : ' . $fetch['username'];
            $themeData['mail_reset_inst'] = Lang('reset_password_inst');
            $themeData['mail_reset_url'] = $fetch['url'];
            $themeData['sig_mail'] = Lang('sig_mail');
            $themeData['system_name'] = Lang('app_name');

            $message = \iCms\UI::view('emails/email-reset-pass');
            if (mail($to, $subject, $message, $headers)) {
                $data['status'] = 200;
                $data['message'] = Lang('success_reset_password');
            }
        } else {
            $error = Lang('error_user_suspend');
        }
    } else {
        $error = Lang('no_user_found');
    }
    $data['error_message'] = $error;
}