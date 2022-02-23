<?php
$cart = array();
$mediaObj = new \iCms\Media();
$promo = $escapeObj->stringEscape($data->misc->promo_code);
$promoInfo = $utiObj->getPromoCode($promo);
$cartObj->setUserId($user['id']);
$discount = $sub_total = $delivery_cost = 0;
$storeInfo = $utiObj->getStore(1);
if (isset($storeInfo['store']['store_id'])) {
    $pInfo = $utiObj->getDirectory(0, 0, $storeInfo['dir']['parent_id']);
    $cartItems = array();
    $count = 0;
    $cartObj->setStoreId(1);
    $cartFetch = $cartObj->getCartByStoreId();
    if (!empty($cartFetch)) {
        $current_city = $utiObj->getCity($user['current_city']);
        if (isset($current_city['city_id'])) $delivery_cost += $current_city['shipping'];
        foreach ($cartFetch as $ke => $va) {
            $itemInfo = $utiObj->getItem($va['item_id']);
            $features = array();
            if (isset($itemInfo['id'])) {
                $count += 1;
                if ($va['ft_id'] != 0) {
                    $cartObj->setFtId($va['ft_id']);
                    $cartFtFetch = $cartObj->getCartFeatures();
                    if (!empty($cartFtFetch))
                        foreach ($cartFtFetch as $cft) {
                            $ftFetch = $utiObj->getItemFeatures(0, $cft['ft_id']);
                            if (isset($ftFetch['id'])) {
                                $ftCatFetch = $utiObj->getStoreCats($ftFetch['category']);
                                $ftCat = isset($ftCatFetch['category_id']) ? ' ' . Lang($ftCatFetch['category_name'], 3) . ' : ' : '';
                                $itemInfo['price'] += isset($ftFetch['price']) ? $ftFetch['price'] : 0;
                                $ftInfo = $utiObj->getItemFeaturesInfo($ftFetch['id']);
                                if (isset($ftInfo['f_id']))
                                    $features[] = array('name' => '<small>' . $ftCat . $ftInfo['title'] . '</small>');
                            }
                        }
                }
                $itemTotal = $itemInfo['price'] * $va['qty'];
                $itemData = $utiObj->getItemInfo($itemInfo['id']);
                $features[] = array('name' => '');
                $product_image = $site_url . '/media/logo.png';
                if ($itemInfo['featured_image'] != 0) {
                    $fimage = $mediaObj->getById($itemInfo['featured_image']);
                    $product_image = $fimage['each'][0]['thumb_url'];
                }
                $cart['data'][] = array(
                    'price_item' => round($itemInfo['price'], 2),
                    'product_name' => $itemData['title'],
                    'currency' => Lang('currency_1'),
                    'image' => $product_image,
                    'features' => $features,
                    'a_stock' => !$itemInfo['stock'] ? 1 : 0,
                    'stock' => $itemInfo['inventory'],
                    'ftid' => $va['ft_id'],
                    'amount' => $va['qty'],
                    'total' => round($itemTotal, 2) . ' ' . Lang('currency_1'),
                    'product_id' => $itemInfo['id']);
                $sub_total += $itemTotal;
            }
        }
    }
}
$radio = array();
if (!empty($cart)) {
    $current_slot = $utiObj->getTimeSlot(date('H:i', time()));
    if (isset($current_slot[0]['id'])) {
        $current_slot = $current_slot[0];
        $get_slots = $utiObj->getTimeSlot();
        if (!empty($get_slots)) {
            foreach ($get_slots as $slot) {
                if ($slot['type'] == 1) {
                    $slot_label = date("g:i a", strtotime($slot['start'])) . ' - ' . date("g:i a", strtotime($slot['end']));
                    $today = date("Y-m-d");
                    $mod_date = strtotime($today . "+ " . ($current_slot['type'] == 2 || $current_slot['id'] > $slot['id'] ? 2 : 1) . " days");
                    $slot_label .= ' [<b><font color="#4db749">' . date("l: jS F", $mod_date) . '</font></b>]';
                    $radio[] = array('text' => $slot_label, 'id' => $slot['id'], 'date' => $slot['start'] . ' ' . date("d-m-Y", $mod_date));
                }
            }
            $name = 'date';
            usort($radio, function ($a, $b) use (&$name) {
                return $a[$name] - $b[$name];
            });
            $cart['radio'] = $radio;
        }
    }

}
$response["result"] = "success";
$cart['notes'] = Lang('client_ordering_notes');
$cart['time_label'] = Lang('select_arrival_time');
$cart['subtotal'] = round($sub_total, 2);
$cart['delivery'] = round($delivery_cost, 2);
if ($promoInfo) {
    if ($promoInfo['type'] == 1) {
        $discValue = ($sub_total * $promoInfo['value']) / 100;
        $discount = $discValue;
        $cart['discount'] = '(' . round($promoInfo['value']) . '%) ' . round($discValue, 2);
    } else {
        $discount = $promoInfo['value'];
        $cart['discount'] = round($promoInfo['value'], 2);
    }
} else if ($promo && !$promoInfo) {
    $cart['promo_error'] = Lang('promo_code_wrong');
    $cart['discount'] = '';
} else $cart['discount'] = '';
$cart['promo_code'] = $promo;
$cart['currency'] = Lang('currency_1');
$cart['total'] = round(($sub_total + $delivery_cost) - $discount, 2) . ' ' . Lang('currency_1');
$response["cart"] = $cart;