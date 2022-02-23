<?php
$data = array();
if (!empty($_POST['itemid'])) {
    $itemid = (int)$_POST['itemid'];
    $usersObj->setUserId($itemid);
    $activateUser = $usersObj->activeUser();
    if ($activateUser) {
        $data = array(
            'status' => 200
        );
    }
}