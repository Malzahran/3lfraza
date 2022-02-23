<?php
if (isset($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $ordObj->setId($itemID);
    if ($ordObj->editOrder($_POST)) {
        $data = array(
            'status' => 200,
        );
    }
}