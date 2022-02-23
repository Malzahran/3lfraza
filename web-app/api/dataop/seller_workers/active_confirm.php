<?php
$itemID = (int)$data->misc->itemid;
$type = (int)$data->misc->mid;
$UserDObj = new \iCms\User();
$itemInfo = $UserDObj->getById($itemID);
if (isset($itemInfo['id'])) {
    $formlayout = array('formhead' => Lang('are_you_sure') . ' ' . ($type == 1 ? Lang('deactiviate_btn') : Lang('activiate_btn')) . ' ' . $itemInfo['name'] . ' ' . Lang('question_mark'), 'formdesc' => '',
        'formbutton' => $type == 1 ? Lang('deactiviate_btn') : Lang('activiate_btn'));
    $id = 0;
    $form = array();
    $form[] = array(
        'id' => $id += 1,
        'place' => 1,
        'type' => 7,
        'required' => 1,
        'title' => Lang('check_to_confirm'),
        'name' => 'confirm');
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
        'abtitle' => Lang('workers_label')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["form"] = $formlayout;
    $response["form"]["formdata"] = $form;
}