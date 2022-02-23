<?php
$itemID = (int)$_POST['item_id'];
$delvID = (int)$_POST['delv_id'];
if ($itemID != 0 && $delvID != 0) {
    $ordObj = new \iCms\Orders();
    if ($ordObj->setOrdDelivery($itemID, $delvID)) {
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}