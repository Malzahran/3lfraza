<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $cityObj->setId($itemID);
    $editCity = $cityObj->edit($_POST);
    if ($editCity) {
        LangSet($L_CODE, 1);
        $data = array(
            'status' => 200,
        );
    }
}