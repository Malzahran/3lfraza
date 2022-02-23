<?php
$response["message"] = "لم يتم تغير كلمة المرور";
if ($isLogged && isset($data->user->new_password) && isset($data->user->old_password)) {
    $userD = $data->user;
    $opass = $escapeObj->stringEscape($userD->old_password);
    $npass = $escapeObj->stringEscape($userD->new_password);
    $usersObj = new \iCms\Users();
    $usersObj->setUserId($user['id']);
    $usersObj->setName($user['name']);
    $usersObj->setUsername($user['username']);
    $usersObj->setEmail($user['email']);
    $usersObj->setoldpassword($opass);
    $usersObj->setPassword($npass);
    if ($usersObj->edit($user['active'])) {
        $response["result"] = "success";
        $response["message"] = "تم تغير كلمة المرور";
    } else {
        $response["result"] = "failure";
        $response["message"] = "لم يتم تغير كلمة المرور";
    }
} else {
    $response["result"] = "failure";
    $response["message"] = "تحقق من بيانات حسابك";
}