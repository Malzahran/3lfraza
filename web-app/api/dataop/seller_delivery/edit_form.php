<?php
$itemID = (int)$data->misc->itemid;
$userDObj = new \iCms\User();
$itemInfo = $userDObj->getById($itemID);
if (isset($itemInfo['id'])) {
    $img = '';
    if ($itemInfo['avatar_id'] != 0) $img = $itemInfo['avatar_url'];
    $formlayout = array('formhead' => Lang('edit_btn') . ' ' . $itemInfo['name'],
        'uploadimg' => 1,
        'formbutton' => Lang('edit_btn'),
        'files_head' => Lang('personal_image_label'),
        'up_img_btn' => Lang('select_image_label'),
        'change_img_btn' => Lang('change_image_label'),
        'image_url' => $img,
        'camera' => 0,
        'uploadimgcount' => 1);
    
    $id = 0;
    $form = array();
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 1,
        'type' => 1,
        'required' => 1,
        'ettype' => 1,
        'title' => Lang('name_label'),
        'text' => $itemInfo['name'],
        'hint' => Lang('input_ph') . Lang('name_label'),
        'name' => 'u_name');
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 5,
        'type' => 1,
        'ettype' => 5,
        'required' => 1,
        'title' => Lang('phone_label') . ':',
        'name' => 'u_phone',
        'text' => $itemInfo['phone'],
        'hint' => Lang('enter_phone_ph'));
    $birthArray = explode('-', $itemInfo['birthday']);
    $birth = '';
    if (is_array($birthArray)) {
        $birth = implode('/', $birthArray);
    }
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 11,
        'type' => 4,
        'text' => $birth,
        'title' => Lang('birthday_label') . ':',
        'name' => 'u_birth',
        'hint' => Lang('input_ph') . Lang('birthday_label'));
    
    $gender = array();
    $gender['male'] = array('id' => 1, 'name' => Lang('male_label'));
    $gender['female'] = array('id' => 1, 'name' => Lang('female_label'));
    $spindata = array();
    $spindata[] = array('id' => ($itemInfo['gender'] == 'male' ? 1 : 2), 'name' => Lang($itemInfo['gender'].'_label'));
    foreach ($gender as $k => $v) {
        if ($k != $itemInfo['gender'])
            $spindata[] = array('id' => $v['id'], 'name' => $v['name']);
    }
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 1,
        'type' => 2,
        'name' => 'u_gender',
        'title' => Lang('gender_label'),
        'spinner' => $spindata);
    
    // Identity Info
    $form[] = array(
        'id' => $id += 1,
        'place' => 3,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('personal_identity'),
        'text' => $itemInfo['nid'],
        'name' => 'u_nid',
        'hint' => Lang('input_ph') . Lang('personal_identity'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 3,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('driving_license'),
        'text' => $itemInfo['driving_license'],
        'name' => 'u_dlc',
        'hint' => Lang('input_ph') . Lang('driving_license'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 3,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('vehicle_license'),
        'text' => $itemInfo['vehicle_license'],
        'name' => 'u_vlc',
        'hint' => Lang('input_ph') . Lang('vehicle_license'));
        
    // Address Info
    $form[] = array(
        'id' => $id += 1,
        'place' => 4,
        'icon' => 7,
        'type' => 1,
        'ettype' => 7,
        'title' => Lang('street_label'),
        'text' => $itemInfo['street'],
        'name' => 'u_street',
        'hint' => Lang('input_ph') . Lang('street_label'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 4,
        'icon' => 17,
        'type' => 1,
        'ettype' => 7,
        'title' => Lang('building_label'),
        'text' => $itemInfo['building'],
        'name' => 'u_building',
        'hint' => Lang('input_ph') . Lang('building_label'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 4,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('floor_label'),
        'text' => $itemInfo['floor'],
        'name' => 'u_floor',
        'hint' => Lang('input_ph') . Lang('floor_label'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 4,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('apartment_label'),
        'text' => $itemInfo['apartment'],
        'name' => 'u_apartment',
        'hint' => Lang('input_ph') . Lang('apartment_label'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 4,
        'icon' => 17,
        'type' => 1,
        'ettype' => 7,
        'title' => Lang('additional_label'),
        'text' => $itemInfo['additional'],
        'name' => 'u_additional',
        'hint' => Lang('input_ph') . Lang('additional_label'));
    
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
        'abtitle' => Lang('delivery_agent_label')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["form"] = $formlayout;
    $response["form"]["formdata"] = $form;
}