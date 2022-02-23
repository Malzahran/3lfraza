<?php
if ($isLogged) {
    $usersObj = new \iCms\Users();
    $phone = $escapeObj->stringEscape($data->user->phone);
    $usersObj->setUserId($user['id']);
    $usersObj->setName($user['name']);
    $usersObj->setUsername($user['username']);
    $usersObj->setEmail($user['email']);
    $usersObj->setPhone($phone);
    if ($usersObj->edit($user['active'])) {
        $response["result"] = "success";
        $response["user"]["phone"] = $phone;
    }
}