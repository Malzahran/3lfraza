<?php
if (isset($data->user) && !empty($data->user) && !empty($data->user->email) && !empty($data->user->name)
    && !empty($data->user->password) && !empty($data->user->phone)) {
    $user = $data->user;
    $gnusername = getUsernameStatus();
    if ($gnusername['code'] == 200) {
        $reguserObj = new \iCms\Users();
        $reguserObj->setUsername($gnusername['username']);
        $reguserObj->setPassword($user->password);
        $reguserObj->setEmail($user->email);
        $reguserObj->setPhone($user->phone);
        $reguserObj->setName($user->name);
        $reguserObj->setAuth('form');
        $register = $reguserObj->register();
        if ($register) {
            $UserID = $register;
            $cont = true;
        }
        if ($cont == true) {
            $loginObj = new \iCmsAPI\LoginControl();
            $userObj = new \iCms\User();
            $userinfo = $userObj->getById($UserID);
            $to = $userinfo['email'];
            $subject = Lang('app_name') . ' - ' . Lang('reg_mail_subject');
            $headers = "From: " . $config['email'] . "\r\n";
            $headers .= "MIME-Version: 1.0\r\n";
            $headers .= "Content-Type: text/html; charset=UTF-8\r\n";
            $themeData['mail_user_name'] = $userinfo['name'];
            $themeData['mail_user_uname'] = $userinfo['username'];
            $themeData['mail_user_password'] = $user->password;
            $themeData['hey_label'] = Lang('hey_label');
            $themeData['reg_mail_greating'] = Lang('reg_mail_greating');
            $themeData['reg_mail_username'] = Lang('reg_mail_username');
            $themeData['reg_mail_password'] = Lang('reg_mail_password');
            $themeData['sig_mail'] = Lang('sig_mail');
            $themeData['system_name'] = Lang('app_name');
            $themeData['system_title'] = Lang('app_name');
            $message = \iCms\UI::view('emails/app-email-register');
            mail($to, $subject, $message, $headers);
            if ($config['email_verification'] == 0) {
                $loginObj->setId($UserID);
                $loginObj->setUserpass(generateKey(7, 8));
                $loginhash = $loginObj->UpdateLoginHash();
                $response["result"] = 'success';
                $response["user"] = array('uid' => $userinfo['id'], 'password' => $loginhash,
                    'username' => $userinfo['username'], 'email' => $userinfo['email'], 'phone' => $userinfo['phone'], 'name' => $userinfo['name'],
                    'profileimg' => $userinfo['avatar_url']);
            } else {
                $userinfo['verification_link'] = $config['site_url'] . '/?tab1=email-verification&email=' . $userinfo['email'] . '&key=' . $userinfo['email_verification_key'];
                $subject = Lang('app_name') . ' - ' . Lang('ver_mail_subject');
                $themeData['mail_verify_link'] = $userinfo['verification_link'];
                $themeData['ver_mail_label'] = Lang('ver_mail_label');
                $message = \iCms\UI::view('emails/app-email-verification');
                mail($to, $subject, $message, $headers);
                $response["result"] = 'pending';
            }
        } else $response["result"] = "failure";
    }
} else $response["result"] = "failure";