<?php
$order_id = (int)$data->misc->itemid;
$ordinfo = $utiObj->getOrder($order_id);
if (isset($ordinfo['order_id'])) {
    $userDObj = new \iCms\User();
    $worker_info = $userDObj->getById($ordinfo['worker_id']);
    if (isset($worker_info['id']) && !empty($worker_info['lat']) && !empty($worker_info['lon'])) {
        $locations[] = array(
            'title' => $worker_info['name'],
            'snippet' => $worker_info['name'],
            'color' => 0,
            'lat' => $worker_info['lat'],
            'longt' => $worker_info['lon'],
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
            'abtitle' => Lang('view_worker_map')
        );
        $response["result"] = "success";
        $response["layout"] = $layout;
        $response["locations"] = $locations;
    }
}