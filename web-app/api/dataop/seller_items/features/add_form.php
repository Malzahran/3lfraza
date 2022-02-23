<?php
$itemID = (int)$data->misc->itemid;
$formlayout = array('formhead' => Lang('add_new_btn'), 'formdesc' => '',
    'formbutton' => Lang('add_new_btn'));
$form = array();
$spindata = array();
$storeID = $user['store_id'];
$featureCats = $utiObj->getStoreCats(0, $storeID, array(4));
if (!empty($featureCats))
    foreach ($featureCats as $k => $v) {
        $spindata[] = array('id' => $v['category_id'], 'name' => Lang($v['category_name'], 3));
    }
else $spindata[] = array('id' => 0, 'name' => Lang('features_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'required' => 1,
    'icon' => 17,
    'type' => 2,
    'name' => 'category',
    'title' => Lang('features_label'),
    'spinner' => $spindata);
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 3,
    'type' => 1,
    'ettype' => 4,
    'title' => Lang('price_label'),
    'name' => 'price',
    'hint' => Lang('input_ph') . Lang('price_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 3,
    'type' => 1,
    'ettype' => 4,
    'title' => Lang('price_dsc_label'),
    'name' => 'dscprice',
    'hint' => Lang('input_ph') . Lang('price_dsc_label'));
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
    'text' => $itemID,
    'name' => 'item_id');
$layout = array(
    'barback' => 1,
    'bartitle' => 1,
    'showhome' => 0,
    'showicon' => 0,
    'orientation' => 1,
    'abtitle' => Lang('features_label')
);
$response["result"] = "success";
$response["layout"] = $layout;
$response["form"] = $formlayout;
$response["form"]["formdata"] = $form;