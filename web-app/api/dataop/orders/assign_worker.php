<?php
$itemID = (int)$_POST['item_id'];
$workerID = (int)$_POST['worker_id'];
if ($itemID != 0 && $workerID != 0) {
    $ordObj = new \iCms\Orders();
    if ($ordObj->setOrdWorker($itemID, $workerID)) {
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}