<?php
$ncount = 0;
$notfObj = new \iCms\Notifications();
$ncount = $notfObj->getNotifications(0, false, true, false, true);
$title = '';
if ($ncount) {
    $ncount = $ncount['count'];
    if ($ncount == 1) $title = Lang('new_notifications') . Lang('s_notification_label');
    else if ($ncount > 1) $title = Lang('new_notifications') . $ncount . Lang('g_notification_label');
} else $ncount = 0;
$notification = array('count' => $ncount, 'title' => $title, 'action' => $title);
if ($workerLogged || $deliveryLogged) {
    $found = false;
    $dataObj = new \iCms\DataGet();
    $sourceLat = isset($data->user->latitude) ? $data->user->latitude : null;
    $sourceLon = isset($data->user->longitude) ? $data->user->longitude : null;
    $radiusKm = 10;
    $filterDT = array();
    $filterDT['geo_info'] = mathGeoProximity($sourceLat, $sourceLon, $radiusKm);
    if ($workerLogged) $filterDT['progress'] = array(2);
    else if ($deliveryLogged) $filterDT['progress'] = array(3);
    $fetch = $dataObj->getOrdIds($filterDT);
    if (isset($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $ordinfo = $utiObj->getOrder($v);
            if (isset($ordinfo['order_id'])) {
                if ($deliveryLogged && !$ordinfo['delv_id']) $found = true;
                if ($workerLogged && !$ordinfo['worker_id']) $found = true;
            }
        }
        if ($found) $response["message"] = Lang('available_orders_area');
    }
}
$response["result"] = "success";
$response["notification"] = $notification;