<?php
if (isset($_POST['user_id'])) {
    $userID = (int)$_POST['user_id'];
    $fetch = $userDObj->getById($userID);

    if (isset($fetch['id'])) {
        $data = array(
            'status' => 200,
            'name' => $fetch['name'],
            'phone' => $fetch['phone'],
            'address' => $fetch['address']
        );
    }
}