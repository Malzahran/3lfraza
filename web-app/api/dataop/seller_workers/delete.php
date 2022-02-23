<?php
$itemID = (int)$_POST['item_id'];
if (isset($itemID) && isset($_POST['confirm']) && (int)$_POST['confirm'] == 1) {
    $usersObj = new \iCms\Users();
    $usersObj->setUserId($itemID);
    if ($usersObj->deleteUser()) {
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}