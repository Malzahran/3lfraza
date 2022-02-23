<?php
$data = array();
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemid = (int)$_POST['item_id'];
    $itemObj->setId($itemid);
    $featItem = $itemObj->featItem();
    if ($featItem) $data = array('status' => 200);
}