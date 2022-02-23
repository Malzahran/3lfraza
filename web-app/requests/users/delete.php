<?php
$data = array();
if (!empty($_POST['itemid'])) {
    $itemid = (int)$_POST['itemid'];
    $usersObj->setUserId($itemid);
    $delUser = $usersObj->deleteUser();
    if ($delUser) {
        $data = array(
            'status' => 200
        );
    }
}