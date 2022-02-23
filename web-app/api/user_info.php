<?php
if ($isLogged) {
    $gps = $reqphone = 0;
    if ($user['type'] == "delivery") $gps = 3;
    if (empty($user['phone'])) $reqphone = 1;
    $response['result'] = 'success';
    $response["user"] =
        array(
            'uid' => $user['id'],
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
            'reqphone' => $reqphone,
            'push' => 15,
            'credit' => 0,
            'chat' => 0,
            'maps' => 5,
            'gps' => $gps,
            'usertype' => $user['type']);
} else $response["result"] = "logout";