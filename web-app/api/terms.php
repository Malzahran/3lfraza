<?php
$termsFetch = $utiObj->getMiscInfo('terms');
$item = array(
    'title' => Lang('terms_label'),
    'id' => 1,
    'desc' => !empty($termsFetch['content']) ? \iCms\UI::html($termsFetch['content']) : '');
$layout = array(
    'allowback' => 1,
    'barback' => 1,
    'orientation' => 0,
    'showicon' => 1,
    'bartitle' => 1,
    'abtitle' => Lang('terms_label')
);
$response["result"] = "success";
$response["item"] = $item;
$response["layout"] = $layout;