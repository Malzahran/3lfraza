<?php
$itemID = (int)$_POST['item_id'];
if (isset($itemID) && isset($_POST['confirm']) && (int)$_POST['confirm'] == 1) {
    $itemObj = new \iCmsSeller\Item();
    $itemObj->setId($itemID);
    if ($itemObj->deleteItem()) {
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}