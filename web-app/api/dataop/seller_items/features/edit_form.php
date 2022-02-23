<?php
$itemID = (int)$data->misc->itemid;
$itemInfo = $utiObj->getItemFeatures(0, $itemID);
if (isset($itemInfo['id'])) {
    $itemData = $utiObj->getItemFeaturesInfo($itemID);
    $itemDataAR = $utiObj->getItemFeaturesInfo($itemID, 'ar');
    $itemDataEN = $utiObj->getItemFeaturesInfo($itemID, 'en');
    $formlayout = array('formhead' => Lang('edit_btn') . ' ' . $itemData['title'], 'formdesc' => '',
        'formbutton' => Lang('edit_btn'));
    $id = 0;
    $form = array();
    $spindata = array();
    $catInfo = $utiObj->getStoreCats($itemInfo['category']);
    if (isset($catInfo['category_id'])) $spindata[] = array('id' => $catInfo['category_id'], 'name' => Lang($catInfo['category_name'], 3));
    $storeID = $user['store_id'];
    $featureCats = $utiObj->getStoreCats(0, $storeID, array(4));
    if (!empty($featureCats))
        foreach ($featureCats as $k => $v) {
            if ($v['category_id'] != $itemInfo['category']) $spindata[] = array('id' => $v['category_id'], 'name' => Lang($v['category_name'], 3));
        }
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
        'hint' => Lang('input_ph') . Lang('price_label'),
        'text' => $itemInfo['price'] != 0 ? round($itemInfo['price'], 2) : '');
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 3,
        'type' => 1,
        'ettype' => 4,
        'title' => Lang('price_dsc_label'),
        'name' => 'dscprice',
        'hint' => Lang('input_ph') . Lang('price_dsc_label'),
        'text' => $itemInfo['dsc_price'] != 0 ? round($itemInfo['dsc_price'], 2) : '');
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 9,
        'type' => 1,
        'ettype' => 1,
        'required' => 1,
        'title' => Lang('title_in_lang') . Lang('ar_lang'),
        'name' => 'title[ar]',
        'text' => $itemDataAR['title']);
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 9,
        'type' => 1,
        'ettype' => 1,
        'title' => Lang('title_in_lang') . Lang('en_lang'),
        'name' => 'title[en]',
        'text' => $itemDataEN['title']);
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
}