<?php
$itemID = (int)$data->misc->product_id;
$ftId = (int)$data->misc->ftid;
$itemInfo = $utiObj->getItem($itemID);
if (isset($itemInfo['id'])) {
    $cartObj->setId($itemID);
    $cartObj->setFtId($ftId);
    $cartObj->setUserId($user['id']);
    $cartObj->setStoreId($itemInfo['store_id']);
    $checkcart = $cartObj->CheckItem();
    if ($checkcart)
        if ($cartObj->RemoveItem()) $response["result"] = "success";
}