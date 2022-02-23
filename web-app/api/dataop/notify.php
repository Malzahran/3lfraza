<?php
$itemID = (int)$data->misc->product_id;
$itemInfo = $utiObj->getItem($itemID);
if (isset($itemInfo['id'])) {
    $chk = $utiObj->chkStockAlert($itemID);
    $reg = $chk? $utiObj->upStockAlert($itemID) : $utiObj->regStockAlert($itemID);
    if ($reg) $response["result"] = "success";
}