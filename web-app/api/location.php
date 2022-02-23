<?php
if ($isLogged) {
    $tracker = $data->tracker;
    $locObj = new \iCms\LocationController();
    $locObj->setUserid($user['id']);
    $checkloc = $locObj->CheckLocation();
    $locObj->setSpeed($tracker->speed);
    $locObj->setLong($tracker->longitude);
    $locObj->setLat($tracker->latitude);
    $done = false;
    if ($checkloc) {
        $locObj->setId($checkloc);
        $done = $locObj->UpdateLocation();
    } else $done = $locObj->InsertLocation();
    if ($done) $response["result"] = "success";
}