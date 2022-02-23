<?php
$privacyFetch = $utiObj->getMiscInfo('privacy_policy');
$item = array(
    'title' => Lang('privacy_label'),
    'id' => 1,
    'desc' => !empty($privacyFetch['content']) ? \iCms\UI::html($privacyFetch['content']) : '');
$layout = array(
    'allowback' => 1,
    'barback' => 1,
    'orientation' => 0,
    'showicon' => 1,
    'bartitle' => 1,
    'abtitle' => Lang('privacy_label')
);
$response["result"] = "success";
$response["item"] = $item;
$response["layout"] = $layout;