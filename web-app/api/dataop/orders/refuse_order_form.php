<?php
$itemID = (int)$data->misc->itemid;
$itemInfo = $utiObj->getOrder($itemID);
if (isset($itemInfo['order_id'])) {
    $formlayout = array('formhead' => Lang('ord_refuse_btn') . ' - ' . Lang('order_number_label') . ' ' . $itemInfo['order_id'],
        'formbutton' => Lang('ord_refuse_btn'));
    $id = 0;
    $chkObj = new \iCms\CheckUtilities();
    $form = array();
    if ($user['type'] == 'seller') {
        $userDObj = new \iCms\User();
        $userFetch = $userDObj->getById($itemInfo['user_id']);
        if (isset($userFetch['id']) && $userFetch['active']) {
            $form[] = array(
                'id' => $id += 1,
                'place' => 1,
                'icon' => 1,
                'type' => 7,
                'title' => Lang('suspend_btn') . ' ' . Lang('user_label'),
                'name' => 'suspend');
        }
    }
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 2,
        'type' => 1,
        'ettype' => 2,
        'height' => 150,
        'required' => 1,
        'title' => Lang('refuse_reason'),
        'name' => 'reason',
        'hint' => Lang('input_ph') . Lang('refuse_reason'));
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