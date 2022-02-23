<?php
$itemID = (int)$data->misc->product_id;
$ftId = (int)$data->misc->ftid;
$qty = (int)$data->misc->qty;
$itemInfo = $utiObj->getItem($itemID);
if (isset($itemInfo['id'])) {
    $cartObj->setId($itemID);
    $cartObj->setFtId($ftId);
    $cartObj->setUserId($user['id']);
    $cartObj->setStoreId($itemInfo['store_id']);
    $checkcart = $cartObj->CheckItem();
    if (isset($checkcart['qty'])) {
        if (!$itemInfo['stock'] && $itemInfo['inventory'] < $qty) $qty = $itemInfo['inventory'];
        if ($qty && $checkcart['qty'] != $qty) $done = $cartObj->UpdateItem($qty, false) ? true : false;
        else if (!$qty) {
            $done = false;
            $response["message"] = Lang('out_of_stock');
            $cartObj->RemoveItem();
        } else $done = true;
        $response["result"] = $done ? "success" : "failure";
    }
}