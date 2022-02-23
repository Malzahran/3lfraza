<?php
$items = array();
$notfObj = new \iCms\Notifications();
$ncount = $notfObj->getNotifications(0, false, true, true, true);
if ($ncount) {
    $items = $ncount['tmpl'];
    $response["result"] = "success";
    $response["message"] = "success";
    $response["items"] = $items;
} else $response["result"] = "failure";