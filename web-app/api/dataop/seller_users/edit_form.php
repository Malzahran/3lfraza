<?php
$itemID = (int)$data->misc->itemid;
$userDObj = new \iCms\User();
$itemInfo = $userDObj->getById($itemID);
if (isset($itemInfo['id'])) {
    $img = null;
    if ($itemInfo['avatar_id'] != 0) $img = $itemInfo['avatar_url'];
    $formlayout = array('formhead' => Lang('edit_btn') . ' ' . $itemInfo['name'],
        'uploadimg' => 1,
        'formbutton' => Lang('edit_btn'),
        'files_head' => Lang('personal_image_label'),
        'up_img_btn' => Lang('select_image_label'),
        'change_img_btn' => Lang('change_image_label'),
        'image_url' => $img,
        'camera' => 1,
        'uploadimgcount' => 1);
    $id = 0;
    $form = array();
    $uTypes = array();
    if (@$seller['system']) $uTypes[] = array('id' => 1, 'name' => Lang('sadmin_type_label'));
    $uTypes[] = array('id' => 2, 'name' => Lang('admin_type_label'));
    $uTypes[] = array('id' => 3, 'name' => Lang('seller_type'));
    $uType = null;
    switch ($itemInfo['store_admin']) {
        case 1:
            $uType = Lang('sadmin_type_label');
            break;
        case 2:
            $uType = Lang('admin_type_label');
            break;
        case 3:
            $uType = Lang('seller_type');
            break;
    }
    $spindata = array();
    $spindata[] = array('id' => $itemInfo['store_admin'], 'name' => $uType);
    foreach ($uTypes as $v) {
        if ($v['id'] != $itemInfo['store_admin']) $spindata[] = array('id' => $v['id'], 'name' => $v['name']);
    }
    $form[] = array(
        'id' => $id += 1,
        'place' => 1,
        'required' => 1,
        'icon' => 17,
        'type' => 2,
        'name' => 'ac_type',
        'title' => Lang('account_type_label'),
        'spinner' => $spindata);
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 1,
        'type' => 1,
        'required' => 1,
        'ettype' => 1,
        'title' => Lang('name_label'),
        'text' => $itemInfo['name'],
        'name' => 'u_name');
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 5,
        'type' => 1,
        'ettype' => 5,
        'required' => 1,
        'title' => Lang('phone_label'),
        'name' => 'u_phone',
        'text' => $itemInfo['phone']);
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
        'abtitle' => Lang('users_label')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["form"] = $formlayout;
    $response["form"]["formdata"] = $form;
}