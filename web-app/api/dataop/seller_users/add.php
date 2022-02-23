<?php
if (isset($_POST['u_name']) && isset($_POST['u_uname']) && isset($_POST['u_pass']) && isset($_POST['u_phone'])) {
    $uType = (int)$_POST['ac_type'];
    $usersObj = new \iCms\Users();
    $usersObj->setAuth('form');
    $usersObj->setUsertype('seller');
    $usersObj->setName($_POST['u_name']);
    $usersObj->setEmail($_POST['u_uname'] . '@' . $site_domain);
    $usersObj->setPhone($_POST['u_phone']);
    $usersObj->setStore($user['store_id']);
    $usersObj->setUsername($_POST['u_uname']);
    $usersObj->setPassword($_POST['u_pass']);
    $usersObj->setStoreAdmin($uType);
    if ($register = $usersObj->register()) {
        $userID = $register;
        if (isset($_FILES['photos']['name'])) {
            $photos = $_FILES['photos'];
            $count = count($photos['name']);
            if ($count == 1) {
                $params = array(
                    'tmp_name' => $photos['tmp_name'][0],
                    'name' => $photos['name'][0],
                    'size' => $photos['size'][0]
                );
                $media = registerMedia($params);
            }
            if (isset($media['id'])) $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET avatar_id=" . $media['id'] . " WHERE id=" . $userID);
        }
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}