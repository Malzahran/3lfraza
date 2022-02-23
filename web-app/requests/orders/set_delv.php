<?php
if (isset($_POST['item_id'])) {
    $ordID = (int)$_POST['item_id'];
    $dlvID = (int)$_POST['delv_id'];
    if ($ordObj->setOrdDelivery($ordID, $dlvID)) {
        $data['status'] = 200;
    }
}