<?php
$itemID = (int)$data->misc->itemid;
$itemInfo = $utiObj->getItem($itemID);
if (isset($itemInfo['id'])) {
    $itemData = $utiObj->getItemInfo($itemID);
    $formlayout = array('formhead' => Lang('are_you_sure_del') . ' ' . $itemData['title'] . ' ' . Lang('question_mark'), 'formdesc' => '',
        'formbutton' => Lang('delete_btn'));
    $id = 0;
    $form = array();
    $form[] = array(
        'id' => $id += 1,
        'place' => 1,
        'type' => 7,
        'required' => 1,
        'title' => Lang('delete_confirm_head'),
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
        'abtitle' => Lang('seller_items_label')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["form"] = $formlayout;
    $response["form"]["formdata"] = $form;
}