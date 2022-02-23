<?php
$itemID = (int)$data->misc->itemid;
$itemInfo = $utiObj->getStoreCats($itemID);
if (isset($itemInfo['category_id'])) {
    $itemData = Lang($itemInfo['category_name'], 3);
    $itemDataAR = Lang($itemInfo['category_name'], 3, 'ar');
    $itemDataEN = Lang($itemInfo['category_name'], 3, 'en');
    $img = null;
    if ($itemInfo['image_id'] != 0) {
        $mediaObj = new \iCms\Media();
        $imgGet = $mediaObj->getById($itemInfo['image_id']);
        $img = $imgGet['each'][0]['rec_url'];
    }
    $formlayout = array('formhead' => Lang('edit_btn') . ' ' . $itemData, 'formdesc' => '',
        'uploadimg' => 1,
        'formbutton' => Lang('edit_btn'),
        'files_head' => Lang('main_photo_label'),
        'up_img_btn' => Lang('select_image_label'),
        'change_img_btn' => Lang('change_image_label'),
        'image_url' => $img,
        'camera' => 1,
        'uploadimgcount' => 1);
    $id = 0;
    $form = array();
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('cat_order_label'),
        'name' => 'order',
        'text' => $itemInfo['cat_order'] != 5000 ? $itemInfo['cat_order'] : '');
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 9,
        'type' => 1,
        'ettype' => 1,
        'required' => 1,
        'title' => Lang('title_in_lang') . Lang('ar_lang'),
        'name' => 'title[ar]',
        'text' => $itemDataAR);
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 9,
        'type' => 1,
        'ettype' => 1,
        'title' => Lang('title_in_lang') . Lang('en_lang'),
        'name' => 'title[en]',
        'text' => $itemDataEN);
    $form[] = array(
        'id' => $id += 1,
        'type' => 8,
        'text' => $itemID,
        'name' => 'item_id');
    $form[] = array(
        'id' => $id += 1,
        'type' => 8,
        'text' => $itemInfo['type'],
        'name' => 'ctype');
    $form[] = array(
        'id' => $id += 1,
        'type' => 8,
        'text' => $itemInfo['mid'],
        'name' => 'subcat');
    $form[] = array(
        'id' => $id += 1,
        'type' => 8,
        'text' => $itemInfo['pid'],
        'name' => 'cat');
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
}