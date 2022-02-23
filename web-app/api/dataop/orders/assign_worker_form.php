<?php
$itemID = (int)$data->misc->itemid;
$itemInfo = $utiObj->getOrder($itemID);
if (isset($itemInfo['order_id'])) {
    $formlayout = array('formhead' => Lang('assign_worker_label') . ' - ' . Lang('order_number_label') . ' ' . $itemInfo['order_id'],
        'formbutton' => Lang('assign_worker_label'));
    $id = 0;
    $form = array();
    $spindata = array();
    $dataObj = new \iCms\DataGet();
    $filterDT = array();
    $filterDT['store_id'] = $user['store_id'];
    $filterDT['utype'] = array('worker');
    $filterDT['geo_info'] = mathGeoProximity($itemInfo['lat'], $itemInfo['lon'], 10);
    $fetch = $dataObj->getUserIds($filterDT);
    if (!empty($fetch['data'])) {
        $UserDObj = new \iCms\User();
        foreach ($fetch['data'] as $v) {
            $userInfo = $UserDObj->getById($v);
            $spindata[] = array('id' => $userInfo['id'], 'name' => $userInfo['name']);
        }
    } else $spindata[] = array('id' => 0, 'name' => Lang('req_order_sell_worker_label'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 1,
        'icon' => 1,
        'type' => 2,
        'required' => 1,
        'name' => 'worker_id',
        'title' => Lang('req_order_sell_worker_label'),
        'spinner' => $spindata);
    $form[] = array(
        'id' => $id += 1,
        'type' => 8,
        'text' => $itemID,
        'name' => 'item_id');

    $layout = array(
        'barback' => 1,
        'bartitle' => 1,
        'allowback' => 1,
        'showhome' => 0,
        'showicon' => 0,
        'orientation' => 1,
        'abtitle' => Lang('orders_label')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["form"] = $formlayout;
    $response["form"]["formdata"] = $form;
}