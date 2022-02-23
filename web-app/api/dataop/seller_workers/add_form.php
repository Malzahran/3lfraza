<?php
$formlayout = array('formhead' => Lang('add_new_btn'),
    'uploadimg' => 1,
    'formbutton' => Lang('add_new_btn'),
    'files_head' => Lang('required_documents'),
    'up_img_btn' => Lang('select_files'),
    'photo_msg' => Lang('required_files_upload'),
    'location_title' => Lang('add_location_label'),
    'forminst' => '<h2>' . Lang('required_documents') . '</h2>
    .' . Lang('personal_identity') . '<br>'
        . '.' . Lang('personal_image_label') . '<br>'
        . '.' . Lang('health_certificate') . '<br>',
    'change_img_btn' => Lang('change_files'),
    'camera' => 0,
    'location' => 2,
    'uploadimgcount' => 4);
$id = 0;
$gnusername = getUsernameStatus('w' . generateKey(5, 5, false, false, true));
$username = $gnusername['username'];
$form = array();

// Account Info
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'icon' => 9,
    'type' => 1,
    'text' => $username,
    'required' => 1,
    'ettype' => 1,
    'disabled' => 1,
    'title' => Lang('username_label') . ':',
    'name' => 'u_uname');
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'icon' => 10,
    'type' => 1,
    'text' => $username,
    'required' => 1,
    'disabled' => 1,
    'ettype' => 1,
    'hint' => Lang('input_ph') . Lang('password_label'),
    'title' => Lang('password_label') . ':',
    'name' => 'u_pass');
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'icon' => 8,
    'type' => 1,
    'required' => 0,
    'ettype' => 8,
    'title' => Lang('email_label') . ':',
    'hint' => Lang('input_ph') . Lang('email_label'),
    'name' => 'u_email');

// Personal Info
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 1,
    'type' => 1,
    'required' => 1,
    'ettype' => 1,
    'title' => Lang('name_label') . ':',
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
    'hint' => Lang('enter_phone_ph'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 17,
    'type' => 1,
    'ettype' => 1,
    'required' => 0,
    'title' => Lang('qualification_label') . ':',
    'name' => 'u_qualification',
    'hint' => Lang('input_ph') . Lang('qualification_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 11,
    'type' => 4,
    'required' => 0,
    'title' => Lang('birthday_label') . ':',
    'name' => 'u_birth',
    'hint' => Lang('input_ph') . Lang('birthday_label'));

$spindata = array();
$spindata[] = array('id' => 2, 'name' => Lang('female_label'));
$spindata[] = array('id' => 1, 'name' => Lang('male_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 1,
    'type' => 2,
    'required' => 1,
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
    'required' => 0,
    'title' => Lang('personal_identity'),
    'name' => 'u_nid',
    'hint' => Lang('input_ph') . Lang('personal_identity'));
    
// Address Info
$form[] = array(
    'id' => $id += 1,
    'place' => 4,
    'icon' => 7,
    'type' => 1,
    'ettype' => 7,
    'required' => 0,
    'title' => Lang('street_label'),
    'name' => 'u_street',
    'hint' => Lang('input_ph') . Lang('street_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 4,
    'icon' => 17,
    'type' => 1,
    'ettype' => 7,
    'required' => 0,
    'title' => Lang('building_label'),
    'name' => 'u_building',
    'hint' => Lang('input_ph') . Lang('building_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 4,
    'icon' => 17,
    'type' => 1,
    'ettype' => 3,
    'required' => 0,
    'title' => Lang('floor_label'),
    'name' => 'u_floor',
    'hint' => Lang('input_ph') . Lang('floor_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 4,
    'icon' => 17,
    'type' => 1,
    'ettype' => 3,
    'required' => 0,
    'title' => Lang('apartment_label'),
    'name' => 'u_apartment',
    'hint' => Lang('input_ph') . Lang('apartment_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 4,
    'icon' => 17,
    'type' => 1,
    'ettype' => 7,
    'required' => 0,
    'title' => Lang('additional_label'),
    'name' => 'u_additional',
    'hint' => Lang('input_ph') . Lang('additional_label'));

$layout = array(
    'barback' => 1,
    'bartitle' => 1,
    'showhome' => 0,
    'showicon' => 0,
    'orientation' => 1,
    'abtitle' => Lang('workers_label')
);
$response["result"] = "success";
$response["layout"] = $layout;
$response["form"] = $formlayout;
$response["form"]["formdata"] = $form;