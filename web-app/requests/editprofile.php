<?php
$data = array();
userOnly();
if (!empty($_POST['email'])) {
    $done = false;
    $userid = $user['id'];
    $activest = 1;
    $editObj = new \iCms\Users();
    $editObj->setUserId($userid);
    $editObj->setName($user['name']);
    $editObj->setEmail($_POST['email']);
    if (!empty($_POST['phone'])) {
        $editObj->setPhone($_POST['phone']);
    }
    if (!empty($_POST['new_password']) && !empty($_POST['current_password'])) {
        $editObj->setoldpassword($_POST['current_password']);
        $editObj->setPassword($_POST['new_password']);
    }
    $editObj->setUsername($user['username']);
    $edit = $editObj->edit($activest);
    if ($edit) {
        $userDObj = new \iCms\User();
        $userinfo = $userDObj->getById($userid);
        if (!empty($_FILES['user_image']['tmp_name'])) {
            if ($userinfo['avatar_id'] != 0) {
                $delObj = new \iCms\DeleteMedia();
                $delObj->deleteMedia($userinfo['avatar_id']);
            }
            $image = $_FILES['user_image'];
            $avatar = registerMedia($image);
            if (isset($avatar['id'])) {
                $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET avatar_id=" . $avatar['id'] . " WHERE id=" . $userid);
                if ($query) {
                    $done = true;
                    $userinfo = $userDObj->getById($userid);
                }
            }
        } else {
            $done = true;
        }
    }
    if ($done) {
        $data['avatar'] = '<img src="' . $userinfo['avatar_url'] . '" alt="' . $userinfo['name'] . '">';
        $data['status'] = (!empty($_POST['new_password']) && !empty($_POST['current_password'])) ? 400 : 200;
        $data['redirect_url'] = smoothLink('index.php?tab1=logout');
        if (isset($_SESSION[$config['spf'] . 'backend_profile'])) {
            $data['redirect_url'] = smoothLink('index.php?tab1=backend&tab2=logout');
        } else if (isset($_SESSION[$config['spf'] . 'seller_profile'])) {
            $data['redirect_url'] = smoothLink('index.php?tab1=admin&tab2=logout');
        }
        $data['avatar_code'] = (isset($_SESSION[$config['spf'] . 'backend_profile']) || isset($_SESSION[$config['spf'] . 'seller_profile'])) ? 2 : 1;
    } else {
        $data['error_message'] = Lang('error_empty_registration');
    }
} else {
    $data['error_message'] = Lang('error_empty_registration');
}