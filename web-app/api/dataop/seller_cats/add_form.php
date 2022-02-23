<?php
$formlayout = array('formhead' => Lang('add_new_btn'), 'formdesc' => '',
    'uploadimg' => 1,
    'formbutton' => Lang('add_new_btn'),
    'files_head' => Lang('main_photo_label'),
    'up_img_btn' => Lang('select_image_label'),
    'change_img_btn' => Lang('change_image_label'),
    'camera' => 1,
    'uploadimgcount' => 1);
$id = 0;
$storeID = $user['store_id'];
$form = array();
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'icon' => 17,
    'type' => 1,
    'ettype' => 3,
    'title' => Lang('cat_order_label'),
    'name' => 'order',
    'hint' => Lang('cat_order_ph'));
$spindata = array();
$spindata[] = array('id' => -1, 'name' => Lang('no_main_cat'));
$pcats = $utiObj->getStoreCats(0, $storeID, array(1));
if (!empty($pcats))
    foreach ($pcats as $k => $v) {
        $spindata[] = array('id' => $v['category_id'], 'name' => Lang($v['category_name'], 3));
    }
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'required' => 1,
    'icon' => 17,
    'type' => 2,
    'fetch_more' => 'get_cats',
    'name' => 'cat',
    'title' => Lang('select_category'),
    'spinner' => $spindata);
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 9,
    'type' => 1,
    'ettype' => 1,
    'required' => 1,
    'title' => Lang('title_in_lang') . Lang('ar_lang'),
    'name' => 'title[ar]',
    'hint' => Lang('input_ph') . Lang('title_in_lang') . Lang('ar_lang'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 9,
    'type' => 1,
    'ettype' => 1,
    'title' => Lang('title_in_lang') . Lang('en_lang'),
    'name' => 'title[en]',
    'hint' => Lang('input_ph') . Lang('title_in_lang') . Lang('en_lang'));
$form[] = array(
    'id' => $id += 1,
    'type' => 8,
    'text' => 1,
    'name' => 'ctype');
$layout = array(
    'barback' => 1,
    'bartitle' => 1,
    'showhome' => 0,
    'showicon' => 0,
    'orientation' => 1,
    'abtitle' => Lang('seller_cats_label')
);
$response["result"] = "success";
$response["layout"] = $layout;
$response["form"] = $formlayout;
$response["form"]["formdata"] = $form;