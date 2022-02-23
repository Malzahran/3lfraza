<?php
$itemID = (int)$data->misc->itemid;
$itemInfo = $utiObj->getItem($itemID);
if (isset($itemInfo['id'])) {
    $itemData = $utiObj->getItemInfo($itemID);
    $itemDataAR = $utiObj->getItemInfo($itemID, 'ar');
    $itemDataEN = $utiObj->getItemInfo($itemID, 'en');
    $img = null;
    if ($itemInfo['featured_image'] != 0) {
        $mediaObj = new \iCms\Media();
        $imgGet = $mediaObj->getById($itemInfo['featured_image']);
        $img = $imgGet['each'][0]['rec_url'];
    }
    $formlayout = array('formhead' => Lang('edit_btn') . ' ' . $itemData['title'], 'formdesc' => '',
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
    $aCart = 0;
    if ($itemInfo['cart'] == 0) $aCart = 1;
    $form[] = array(
        'id' => $id += 1,
        'place' => 1,
        'type' => 7,
        'ettype' => $aCart,
        'title' => Lang('allow_orders_label'),
        'name' => 'cart');
    if ($aCATS) {
        $spindata = array();
        $storeID = $user['store_id'];
        $catinfo = $utiObj->getStoreCats($itemInfo['category']);
        $catName = '';
        if (isset($catinfo['category_id'])) {
            $pCat = $catinfo['pid'] != 0 ? $utiObj->getStoreCats($catinfo['pid']) : '';
            $mCat = $catinfo['mid'] != 0 ? $utiObj->getStoreCats($catinfo['mid']) : '';
            $catName = Lang($catinfo['category_name'], 3);
            $catName .= isset($pCat['category_id']) ? ' / ' . Lang($pCat['category_name'], 3) : '';
            $catName .= isset($mCat['category_id']) ? ' / ' . Lang($mCat['category_name'], 3) : '';
        }
        $spindata[] = array('id' => $itemInfo['category'], 'name' => $catName);
        $pcats = $utiObj->getStoreCats(0, $storeID);
        if (!empty($pcats))
            foreach ($pcats as $k => $v) {
                $mcat = $utiObj->getStoreMainCats($v['category_id']);
                if (!empty($mcat))
                    foreach ($mcat as $key => $value) {
                        if ($value['category_id'] != $itemInfo['category']) {
                            if ($value['has_sub'] == 1) {
                                $scat = $utiObj->getStoreSubCat($value['category_id']);
                                if (!empty($scat)) {
                                    foreach ($scat as $ke => $val) {
                                        if ($itemInfo['category'] != $val['category_id']) $spindata[] = array('id' => $val['category_id'], 'name' => Lang($val['category_name'], 3) . ' / ' . Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3));
                                    }
                                }
                            } else $spindata[] = array('id' => $value['category_id'], 'name' => Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3));
                        }
                    }
            }
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
        'text' => $itemInfo['item_code']);
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 17,
        'type' => 1,
        'ettype' => 3,
        'title' => Lang('sort_label') . ' ' . Lang('seller_cat_type_1'),
        'name' => 'order',
        'hint' => Lang('cat_order_ph'),
        'text' => $itemInfo['itemsorder'] != 9000 ? $itemInfo['itemsorder'] : '');
    if ($aINV) {
        $form[] = array(
            'id' => $id += 1,
            'place' => 2,
            'icon' => 17,
            'type' => 1,
            'ettype' => 3,
            'title' => Lang('inventory_label'),
            'name' => 'inventory',
            'text' => $itemInfo['inventory']);
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
        'title' => Lang('item_title_in_lang') . Lang('ar_lang'),
        'name' => 'title[ar]',
        'text' => $itemDataAR['title']);

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
        'text' => htmlspecialchars_decode(stripslashes($itemDataAR['content'])));

    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 9,
        'type' => 1,
        'ettype' => 1,
        'title' => Lang('item_title_in_lang') . Lang('en_lang'),
        'name' => 'title[en]',
        'text' => $itemDataEN['title']);
    $form[] = array(
        'id' => $id += 1,
        'place' => 2,
        'icon' => 2,
        'type' => 1,
        'ettype' => 2,
        'height' => 150,
        'title' => Lang('item_desc_in_lang') . Lang('en_lang'),
        'name' => 'desc[en]',
        'text' => htmlspecialchars_decode(stripslashes($itemDataEN['content'])));
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
        'abtitle' => Lang('seller_items_label')
    );
    $response["result"] = "success";
    $response["layout"] = $layout;
    $response["form"] = $formlayout;
    $response["form"]["formdata"] = $form;
}