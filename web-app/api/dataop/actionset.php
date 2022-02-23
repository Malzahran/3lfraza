<?php
$intentData = null;
if ($data->misc->item_id != 0) {
    $itemid = (int)$data->misc->item_id;
    $actype = 4;
} else if ($data->misc->did != 0) {
    $itemid = (int)$data->misc->did;
    $actype = 1;
} else {
    $itemid = (int)$data->misc->itemid;
    $actype = (int)$data->misc->sid;
}
if (isset($itemid)) {
    $done = false;
    $type = $escapeObj->stringEscape($data->misc->type);
    $ActionObj = new \iCms\ActionUtilities();
    $ActionObj->setId($itemid);
    if ($type == 'phone' || $type == 'openinfo') {
        switch ($type) {
            case 'phone' :
                $acid = 3;
                break;
            case 'openinfo' :
                $acid = 2;
                break;
        }
        $actionset = $ActionObj->setDirectoryAction($acid);
        if ($actionset) {
            $done = true;
        }
    } else {
        $action = null;
        $msg = null;
        $ActionObj->setUserId($user['id']);
        $ActionObj->setAcType($actype);
        $ChkObj = new \iCms\CheckUtilities();
        $ChkObj->setId($itemid);
        $ChkObj->setUserId($user['id']);
        $ChkObj->setAcType($actype);
        switch ($type) {
            case 'order_rate' :
                $rating = $data->misc->rating;
                $chkrate = $ChkObj->CheckRate();
                if (!$chkrate) {
                    $ActionObj->setRating($rating);
                    $actionset = $ActionObj->registerRating();
                } else $actionset = false;
                break;
            case 'order_progress' :
                if ($ActionObj->setOrderProgress()) {
                    $actionset = true;
                    $action = array('type' => 3);
                }
                break;
            case 'order_worker' :
                if ($ActionObj->setOrderWorker()) {
                    $actionset = true;
                    $action = array('type' => 6);
                    $intentData = array('intent' => 15, 'reqtype' => 'orders', 'reqstype' => 'getorders');
                    $response['notes'] = Lang('worker_preparation_notes');

                }
                break;
            case 'order_delivery' :
                if ($ActionObj->setOrderDelivery()) {
                    $actionset = true;
                    $action = array('type' => 6);
                    $intentData = array('intent' => 15, 'reqtype' => 'orders', 'reqstype' => 'getorders');
                    $response['notes'] = Lang('delivery_receiving_order_notes');
                }
                break;
            case 'order_prepared' :
                if ($ActionObj->setOrderPrepared()) {
                    $actionset = true;
                    $action = array('type' => 3);
                }
                break;
            case 'order_on_way' :
                if ($ActionObj->setOrderOnWay()) {
                    $actionset = true;
                    $action = array('type' => 3);
                }
                break;
            case 'order_delivered' :
                if ($ActionObj->setOrderDelivered()) {
                    $actionset = true;
                    $action = array('type' => 3);
                }
                break;
        }
        if ($actionset) $done = true;
    }
    if ($done) {
        $response["result"] = "success";
        $response["message"] = $msg;
        $response["action"] = $action;
        $response["intentData"] = $intentData;
    }
}