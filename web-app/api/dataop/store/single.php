<?php
$item = array();
$product_images = array();
$itemid = (int)$data->misc->product_id;
$itemInfo = $utiObj->getItem($itemid);
if (isset($itemInfo['id'])) {
    $user_city_group = (int)$data->user->city_group;
    $storeID = $itemInfo['store_id'];
    $itemData = $utiObj->getItemInfo($itemid);
    $itemimg = $itemFullImg = $site_url . '/media/logo.png';
    if ($itemInfo['featured_image'] != 0) {
        $mediaObj = new \iCms\Media();
        $imgFetch = $mediaObj->getById($itemInfo['featured_image']);
        $itemimg = $imgFetch['each'][0]['rec_url'];
        $itemFullImg = $imgFetch['each'][0]['complete_url'];
        //$product_images[] = array('product_id' => $itemid, 'name' => $itemimg, 'full' => $itemFullImg);
    }
    $minfo = null;
    $item_category = $utiObj->getStoreCats($itemInfo['category']);
    $product_city_group = 0;
    if (isset($item_category['category_id']))
        $product_city_group = $item_category['city_group'];
    $cats = array();
    $insideCats = $utiObj->getStoreCats(0, $storeID, array(2, 3));
    if (!empty($insideCats))
        foreach ($insideCats as $v) {
            $cats[] = array('id' => $v['category_id'], 'name' => Lang($v['category_name'], 3));
        }
    $feature = 0;
    $features = array();
    if ($itemInfo['price'] == 0) {
        $featuresCat = $utiObj->getItemFeaturesCats($itemid);
        if (!empty($featuresCat)) {
            foreach ($featuresCat as $k => $v) {
                $catInfo = $utiObj->getStoreCats($v['category']);
                if (isset($catInfo['category_id'])) {
                    $featuresCatFt = $utiObj->getItemFeaturesCats($itemid, $v['category']);
                    if (!empty($featuresCatFt)) {
                        $ft = array();
                        foreach ($featuresCatFt as $ke => $va) {
                            $ftInfo = $utiObj->getItemFeaturesInfo($va['id']);
                            $ft[] = array('id' => $va['id'], 'name' => $ftInfo['title'], 'price' => $va['price'], 'dsc_price' => $va['dsc_price']);
                        }
                        if (!empty($ft)) $features[] = array('id' => $v['category'], 'name' => Lang($catInfo['category_name'], 3), 'features' => $ft);
                    }
                }
            }
        }
    }
    $acart = $anotify = $status = 0;
    if (!$itemInfo['cart'] && $product_city_group == $user_city_group || $product_city_group == 3) $acart = 1;
    if ($acart && !$itemInfo['stock'] && $itemInfo['inventory'] < 1) {
        $anotify = 1;
        $acart = 0;
        $status = 2;
    }
    if (!$anotify && $acart) $status = 1;
    if ($itemInfo['soon']) {
        $status = 3;
        $anotify = 1;
        $acart = 0;
    }

    $item = array(
        'name' => $itemData['title'],
        'id' => $itemid,
        'image' => $itemimg,
        'full_image' => $itemFullImg,
        'product_images' => $product_images,
        'features' => $features,
        'stock' => $itemInfo['inventory'],
        'a_notify' => $anotify,
        'status' => $status,
        'a_cart' => $acart,
        'description' => htmlspecialchars_decode(stripslashes($itemData['content'])),
        'price' => $itemInfo['price'],
        'price_discount' => $itemInfo['dsc_price'],
        'minfo' => $minfo,
        'currency' => Lang('currency_1'));

    $response["result"] = "success";
    $response["product"] = $item;
}