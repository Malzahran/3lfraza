<?php
$items = array();
$page = $data->misc->page;
$total = 100;
$limit = 10;
$pages = $total / $limit;
$from = $page - 1 * $limit;
$to = $from + $limit;
$datapage = (int)$data->misc->page;
$page = ($datapage == 1 ? 1 : $datapage);
$start = (5 - 1) * $limit;
$notfObj = new \iCms\Notifications();
$ncount = $notfObj->getNotifications(0, false, false, true, true);
$items = $ncount['tmpl'];
$layout = array(
    'layout' => 2,
    'coloumn' => 3,
    'indvclk' => 1,
    'allowbuttons' => 0,
    'click' => 1,
    'allowback' => 1,
    'barback' => 1,
    'showicon' => 0,
    'bartitle' => 1,
    'abtitle' => Lang('notification_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;