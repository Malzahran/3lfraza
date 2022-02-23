<?php
if (isset($_POST['itemid'])) {
    $itemID = (int)$_POST['itemid'];
    $ordObj->setId($itemID);
    if ($ordObj->deleteOrder()) {
        $data = array(
            'status' => 200,
        );
    }
}