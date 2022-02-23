<?php
$order_id = (int)$data->misc->itemid;
$ordinfo = $utiObj->getOrder($order_id);
if (isset($ordinfo['order_id'])) {
    $locations[] = array(
        'title' => $ordinfo['user_name'],
        'snippet' => $ordinfo['user_name'],
        'color' => 0,
        'lat' => $ordinfo['lat'],
        'longt' => $ordinfo['lon'],
    );
    $layout = array(
        'allowback' => 1,
        'barback' => 1,
        'orientation' => 0,
        'searchtype' => 1,
        'bartitle' => 1,
        'refresh' => 0,
        'zoom' => 0,
        'allowmore' => 0,
        'abtitle' => Lang('view_client_map')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["locations"] = $locations;
}