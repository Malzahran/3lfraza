<?php
$formlayout = array('formhead' => Lang('add_new_btn'),
    'uploadimg' => 1,
    'formbutton' => Lang('add_new_btn'),
    'files_head' => Lang('personal_image_label'),
    'up_img_btn' => Lang('select_image_label'),
    'change_img_btn' => Lang('change_image_label'),
    'camera' => 1,
    'uploadimgcount' => 1);
$id = 0;
$gnusername = getUsernameStatus('c' . generateKey(5, 5, false, false, true));
$username = $gnusername['username'];
$form = array();
$spindata = array();
if (@$seller['system']) $spindata[] = array('id' => 1, 'name' => Lang('sadmin_type_label'));
$spindata[] = array('id' => 2, 'name' => Lang('admin_type_label'));
$spindata[] = array('id' => 3, 'name' => Lang('seller_type'));
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
    'icon' => 9,
    'type' => 1,
    'text' => $username,
    'required' => 1,
    'ettype' => 1,
    'disabled' => 1,
    'title' => Lang('username_label'),
    'name' => 'u_uname');
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 10,
    'type' => 1,
    'text' => $username,
    'required' => 1,
    'ettype' => 1,
    'disabled' => 1,
    'title' => Lang('password_label'),
    'name' => 'u_pass');
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 1,
    'type' => 1,
    'required' => 1,
    'ettype' => 1,
    'title' => Lang('name_label'),
    'hint' => Lang('input_ph') . Lang('name_label'),
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
    'hint' => Lang('enter_phone_ph'));
$layout = array(
    'barback' => 1,
    'bartitle' => 1,
    'showhome' => 0,
    'showicon' => 0,
    'orientation' => 1,
    'abtitle' => Lang('users_label')
);
$response["result"] = "success";
$response["layout"] = $layout;
$response["form"] = $formlayout;
$response["form"]["formdata"] = $form;