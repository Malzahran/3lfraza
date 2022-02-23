<?php
$data = array();
if (!empty($_POST['userid']) && !empty($_POST['name']) && !empty($_POST['email']) && !empty($_POST['username'])) {
    $done = false;
    $userid = (int)$_POST['userid'];
    $activest = isset($_POST['activestate']) && $_POST['activestate'] == 1 ? 1 : 0;
    $usersObj->setUserId($userid);
    $usersObj->setName($_POST['name']);
    $usersObj->setEmail($_POST['email']);
    if (!empty($_POST['phone'])) {
        $usersObj->setPhone($_POST['phone']);
    }
    if (!empty($_POST['password'])) {
        $usersObj->setPassword($_POST['password']);
    }
    if (!empty($_POST['oldpassword'])) {
        $usersObj->setoldpassword($_POST['oldpassword']);
    }
    if (!empty($_POST['nid'])) {
        $usersObj->setNationalID($_POST['nid']);
    }
    $usersObj->setUsername($_POST['username']);
    $edit = $usersObj->edit($activest);
    if ($edit) {
        $userDObj = new \iCms\User();
        $userinfo = $userDObj->getById($userid);
        if (!empty($_FILES['avatar']['tmp_name'])) {
            if ($userinfo['avatar_id'] != 0) {
                $delObj = new \iCms\DeleteMedia();
                $delObj->deleteMedia($userinfo['avatar_id']);
            }
            $image = $_FILES['avatar'];
            $avatar = registerMedia($image);
            if (isset($avatar['id'])) {
                $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET avatar_id=" . $avatar['id'] . " WHERE id=" . $userid);
            }
        }
        if ($userinfo['type'] == "moderator" && !empty($_POST['perm'])) {
            $usersObj->setPerm($_POST['perm']);
            $editperm = $usersObj->updatePerm();
            if ($editperm) {
                $done = true;
            }
        } else {
            $done = true;
        }
    }
    if ($done) {
        $data = array(
            'status' => 200
        );
    }
}