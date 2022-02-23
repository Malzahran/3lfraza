<?php
if (isset($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $itemObj->setId($itemID);
    if ($itemObj->deleteItem()) {
        $data['status'] = 200;
    }
}