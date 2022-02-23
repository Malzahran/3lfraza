<?php
if (isset($data->user) && !empty($data->user) && isset($data->user->username)) {
    $user = $data->user;
    $username = 'fb_' . $user->username;
    $loginObj->setUserid($username);
    $loginObj->setUserpass(sha1($username . md5($username)));
    $loggedchk = $loginObj->CheckFacebookLogin();
    if ($loggedchk) {
        $UserID = $loggedchk;
        $cont = true;
    } else {
        $reguserObj = new \iCms\Users();
        $reguserObj->setUsername($username);
        $reguserObj->setPassword($username);
        $reguserObj->setEmail($username . '@facebook.com');
        $reguserObj->setAuth('facebook');
        if (!empty($user->name)) $name = $user->name;
        else $name = $username;
        $reguserObj->setName($name);
        $register = $reguserObj->register();
        if ($register) {
            $UserID = $register;
            $cont = true;
        }
    }
    if ($cont == true) {
        $isLogged = true;
        if (!empty($data->user->profileimg)) {
            $imgurl = $escapeObj->stringEscape($data->user->profileimg);
            $profimg = $imgurl . '&redirect=false';
            $querychk = $conn->query("SELECT avatar_id FROM " . DB_ACCOUNTS . " WHERE id=" . $UserID);
            if ($querychk) {
                $fetch = $querychk->fetch_array(MYSQLI_ASSOC);
                $mediaid = $fetch['avatar_id'];
                if ($mediaid != 0) {
                    $deleteObj = new \iCms\DeleteMedia();
                    $deleteObj->deleteMedia($mediaid);
                }
            }
            $mediaObj = new \iCms\Media();
            $importmedia = $mediaObj->importMedia($profimg, 'facebook');
            if ($importmedia) {
                $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET avatar_id=" . $importmedia . " WHERE id=" . $UserID);
                if ($query) $cont = true;
            }
        }
        $user = $userObj->getById($UserID);
        $loginObj->setId($UserID);
        $loginhash = $loginObj->UpdateLoginHash();
        $response["result"] = 'success';
        $response["message"] = "Login Successful";
        if (empty($user['phone'])) $reqphone = 1;
        $response["user"] = array(
            'uid' => $user['id'],
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
    } else {
        $response["result"] = "failure";
        $response["message"] = Lang('error_bad_login');
    }
} else {
    $response["result"] = "failure";
    $response["message"] = Lang('error_empty_registration');
}