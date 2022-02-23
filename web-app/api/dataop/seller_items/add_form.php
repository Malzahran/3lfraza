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
$form = array();
$form[] = array(
    'id' => $id += 1,
    'place' => 1,
    'type' => 7,
    'ettype' => 1,
    'title' => Lang('allow_orders_label'),
    'name' => 'cart');
if ($aCATS) {
    $spindata = array();
    $storeID = $user['store_id'];
    $pcats = $utiObj->getStoreCats(0, $storeID);
    if (!empty($pcats))
        foreach ($pcats as $k => $v) {
            $mcat = $utiObj->getStoreMainCats($v['category_id']);
            if (!empty($mcat)) {
                foreach ($mcat as $key => $value) {
                    if ($value['has_sub'] == 1) {
                        $scat = $utiObj->getStoreSubCat($value['category_id']);
                        if (!empty($scat))
                            foreach ($scat as $ke => $val) {
                                $spindata[] = array('id' => $val['category_id'], 'name' => Lang($val['category_name'], 3) . ' / ' . Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3));
                            }
                    } else $spindata[] = array('id' => $value['category_id'], 'name' => Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3));
                }
            }
        }
    else $spindata[] = array('id' => 0, 'name' => Lang('select_category'));
    $form[] = array(
        'id' => $id += 1,
        'place' => 1,
        'required' => 1,
        'icon' => 17,
        'type' => 2,
        'name' => 'cat',
        'title' => Lang('select_category'),
        'spinner' => $spindata);
}
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 17,
    'type' => 1,
    'required' => 1,
    'ettype' => 3,
    'title' => Lang('item_code_label'),
    'name' => 'code',
    'hint' => Lang('input_ph') . Lang('item_code_label'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 17,
    'type' => 1,
    'ettype' => 3,
    'title' => Lang('sort_label') . ' ' . Lang('seller_cat_type_1'),
    'name' => 'order',
    'hint' => Lang('cat_order_ph'));
if ($aINV) {
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('inventory_label'),
        'name' => 'inventory',
        'hint' => Lang('input_ph') . Lang('inventory_label'));
}
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 3,
    'type' => 1,
    'ettype' => 4,
    'required' => $aFeatures ? 0 : 1,
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
    'title' => Lang('item_title_in_lang') . Lang('ar_lang'),
    'name' => 'title[ar]',
    'hint' => Lang('input_ph') . Lang('item_title_in_lang') . Lang('ar_lang'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 2,
    'type' => 1,
    'ettype' => 2,
    'height' => 150,
    'required' => 1,
    'title' => Lang('item_desc_in_lang') . Lang('ar_lang'),
    'name' => 'desc[ar]',
    'hint' => Lang('input_ph') . Lang('item_desc_in_lang') . Lang('ar_lang'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 9,
    'type' => 1,
    'ettype' => 1,
    'title' => Lang('item_title_in_lang') . Lang('en_lang'),
    'name' => 'title[en]',
    'hint' => Lang('input_ph') . Lang('item_title_in_lang') . Lang('en_lang'));
$form[] = array(
    'id' => $id += 1,
    'place' => 2,
    'icon' => 2,
    'type' => 1,
    'ettype' => 2,
    'height' => 150,
    'title' => Lang('item_desc_in_lang') . Lang('en_lang'),
    'name' => 'desc[en]',
    'hint' => Lang('input_ph') . Lang('item_desc_in_lang') . Lang('en_lang'));
$layout = array(
    'barback' => 1,
    'bartitle' => 1,
    'showhome' => 0,
    'showicon' => 0,
    'orientation' => 1,
    'abtitle' => Lang('seller_items_label')
);
$response["result"] = "success";
$response["layout"] = $layout;
$response["form"] = $formlayout;
$response["form"]["formdata"] = $form;