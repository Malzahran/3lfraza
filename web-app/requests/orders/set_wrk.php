<?php
if (isset($_POST['item_id'])) {
    $ordID = (int)$_POST['item_id'];
    $wrkID = (int)$_POST['wrk_id'];
    if ($ordObj->setOrdWorker($ordID, $wrkID)) {
        $data['status'] = 200;
    }
}