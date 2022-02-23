<?php
$itemID = (int)$_POST['item_id'];
if (isset($itemID) && !empty($_POST['reason'])) {
    $itemInfo = $utiObj->getOrder($itemID);
    if (isset($itemInfo['order_id'])) {
        $ordObj = new \iCms\Orders();
        $ordObj->setId($itemID);
        if ($ordObj->refuseOrder($_POST)) {
            $ntfObj = new \iCms\Notifications();
            $notarray = array('rid' => $itemInfo['order_id'], 'usid' => $itemInfo['user_id'], 'stid' => $itemInfo['store_id'], 'type' => 5, 'state' => 7);
            if ($ntfObj->registerNotif($notarray)) {
                if (isset($_POST['suspend']) && (int)$_POST['suspend'] == 1 && $user['type'] == 'seller') {
                    $usersObj = new \iCms\Users();
                    $usersObj->setUserId($itemInfo['user_id']);
                    if ($suspendUser = $usersObj->activeUser()) $response['error'] = false;
                } else $response['error'] = false;
                $response['message'] = Lang('success_done_msg');
            }
        }
    }
}