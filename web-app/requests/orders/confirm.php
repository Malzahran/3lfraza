<?php
if (isset($_POST['item_id'])) {
    $html = '';
    $orderdID = (int)$_POST['item_id'];
    $ordinfo = $utiObj->getOrder($orderdID);
    if (isset($ordinfo['order_id'])) {
        if ($ordinfo['progress'] == 1) {
            $actionObj = new \iCms\ActionUtilities();
            $actionObj->setId($orderdID);
            $actionObj->setAcType(2);
            if ($actionObj->setOrderProgress()) {
                $data = array('status' => 200);
            }
        }
    }
}