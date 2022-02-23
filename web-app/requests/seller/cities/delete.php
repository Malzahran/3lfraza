<?php
if (isset($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $cityObj->setId($itemID);
    if ($cityObj->delete()) {
        $data['status'] = 200;
    }
}