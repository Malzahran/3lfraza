<?php
if (isset($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $catObj->setId($itemID);
    if ($catObj->deleteCategory()) {
        $data['status'] = 200;
    }
}