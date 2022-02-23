<?php
$name = $isLogged ? $user['name'] : '';
$role = $data->user->role;
$phone = $data->user->phone;
$email = $data->user->email;
$lat = $data->user->latitude;
$lon = $data->user->longitude;
$chk = $utiObj->chkLocationAlert($role, $email, $phone, $lat, $lon);
$reg = $chk ? $utiObj->upLocationAlert($role, $email, $phone, $lat, $lon) : $utiObj->regLocationAlert($role, $email, $phone, $name, $lat, $lon);
if ($reg) {
    $response["result"] = "success";
    $response["message"] = Lang('location_notify_success');
}