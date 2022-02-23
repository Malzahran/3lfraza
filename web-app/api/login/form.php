<?php
if (isset($data->user) && !empty($data->user) && isset($data->user->username) && isset($data->user->password)) {
    $user = $data->user;
    $username = $user->username;
    $usertype = $user->usertype;
    $password = sha1($user->password . md5($user->password));
    $loginObj->setUsername($username);
    $loginObj->setUserpass($password);
    $loginObj->setUsertype($usertype);
    $loggedchk = $loginObj->CheckLogin();
    if ($loggedchk) {
        $user = $userObj->getById($loggedchk);
        if (isset($user['id']) && in_array($user['type'], $userTypes)) {
            $isLogged = true;
            $loginObj->setId($loggedchk);
            $loginObj->UpdateLastLogin();
            $admin = $userObj->isSysAdmin();
            $city = $user['current_city'];
            if ($user['type'] == "admin" || $user['type'] == "moderator") $adminLogged = true;
            if ($user['type'] == "seller") {
                $sellerLogged = $user['seller']['logged'];
                $seller = $user['seller'];
                if ($sellerLogged) {
                    $aCATS = ($seller['store']['cats'] == 1) ? true : false;
                    $aINV = ($seller['store']['inventory'] == 1) ? true : false;
                    $aDLV = ($seller['store']['delivery'] == 1) ? true : false;
                }
            }
            if ($config['email_verification'] == 1 && $user['email_verified'] == 0) {
                $response["result"] = "failure";
                $response["message"] = "بيانات الدخول غير صحيحة";
            } else {
                $loginhash = $loginObj->UpdateLoginHash();
                $response["result"] = 'success';
                $response["message"] = "Login Successful";
                if ($user['type'] == "delivery") $gps = 3;
                if (empty($user['phone'])) $reqphone = 1;
                $response["user"] = array('uid' => $user['id'],
                    'password' => $loginhash,
                    'username' => $user['username'],
                    'email' => $user['email'],
                    'phone' => $user['phone'],
                    'street' => $user['street'],
                    'building' => $user['building'],
                    'floor' => $user['floor'],
                    'apartment' => $user['apartment'],
                    'additional' => $user['additional'],
                    'name' => $user['name'],
                    'profileimg' => $user['avatar_url'],
                    'balance' => '',
                    'reqphone' => $reqphone,
                    'push' => 15,
                    'credit' => 0,
                    'chat' => 0,
                    'maps' => 5,
                    'gps' => $gps,
                    'usertype' => $user['type']);
            }
        }
    } else {
        $response["result"] = "failure";
        $response["message"] = Lang('error_bad_login');
    }
} else {
    $response["result"] = "failure";
    $response["message"] = Lang('error_empty_registration');
}
