<?php
$itemID = (int)$_POST['item_id'];
if (isset($itemID) && isset($_POST['confirm']) && (int)$_POST['confirm'] == 1) {
    $catObj = new \iCmsSeller\Category();
    $catObj->setId($itemID);
    if ($catObj->deleteCategory()) {
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}