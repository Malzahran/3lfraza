<?php
$data = array();
if ($adminLogged || $sellerLogged) {
    if (!empty($_POST['item_id'])) {
        $itemID = (int)$_POST['item_id'];
        $UserObj = new \iCms\User();
        $itemFetch = $utiObj->getOrder($itemID);
        if (!empty($itemFetch)) {
            $themeData['order_number'] = $itemID;
            $themeData['order_cost'] = round($itemFetch['price'], 2) . ' ' . Lang('currency_1');
            $themeData['order_discount'] = round($itemFetch['discount'], 2) . ' ' . Lang('currency_1');
            $themeData['order_delivery'] = round($itemFetch['delivery'], 2) . ' ' . Lang('currency_1');
            $themeData['orders_total'] = round(($itemFetch['delivery'] + $itemFetch['price']) - $itemFetch['discount'], 2) . ' ' . Lang('currency_1');
            if (!empty($itemFetch['arrival_time']) && !empty($itemFetch['arrival_date'])) {
                $slot_label = '<span dir="ltr">';
                $slot_label .= date("Y-m-d", strtotime($itemFetch['arrival_date']));
                $slot_label .= date(" g:i a", strtotime($itemFetch['arrival_time'] . " - 1 hour"));
                $slot_label .= '</span>';
                $themeData['order_arrival_time'] = Lang('order_arrival_time_slot') . ': ' . $slot_label;
            }
            if (!empty($itemFetch['promo_code'])) {
                $themeData['order_promo_code'] = '<br>' . Lang('promo_code_label') . ': <b>' . $itemFetch['promo_code'] . '</b>';
            }
            $themeData['client_info'] = '';
            $themeData['ord_img'] = '<img width="60%" src="' . $config['site_url'] . '/media/logo.png" class="img-rounded">';
            $themeData['seller_name'] = '';
            if ($itemFetch['store_id'] != 0) {
                $storeData = $utiObj->getStore($itemFetch['store_id']);
                $pInfo = $utiObj->getDirectory(0, 0, $storeData['dir']['parent_id']);
                if ($pInfo['featured_image'] != 0) {
                    $mediaObj = new \iCms\Media();
                    $fimage = $mediaObj->getById($pInfo['featured_image']);
                    $themeData['ord_img'] = '<img width="60%" src="' . $fimage['each'][0]['squ_url'] . '" class="img-rounded">';
                }
            }
            if ((@$admin['system'] || @$admin['ord']['view']) || $sellerLogged) {
                $themeData['order_user_name'] = $itemFetch['user_name'];
                $themeData['order_user_phone'] = $itemFetch['user_phone'];
                $themeData['order_user_address'] = '<br><b>' . Lang('street_label') . $itemFetch['street'] . '<br>' . Lang('building_label') . $itemFetch['building'] . '<br>' . Lang('floor_label') . $itemFetch['floor'] . '<br>' . Lang('apartment_label') . $itemFetch['apartment'] . (!empty($itemFetch['additional']) ? '<br>' . Lang('additional_label') . $itemFetch['additional'] : '') . '</b>';
                $order_area = $utiObj->getCity($itemFetch['city']);
                $themeData['order_user_area'] = isset($order_area['city_id']) ? Lang($order_area['city_name'], 2) : Lang('no_filter_label');
                $themeData['client_info'] = \iCms\UI::view('backend/orders/client-info');
            }

            if ($itemFetch['delv_id'] != 0) {
                $dlvInfo = $UserObj->getById($itemFetch['delv_id']);
                $themeData['order_delivery_name'] = $dlvInfo['first_name'] . ' ' . $dlvInfo['last_name'];
            }

            if ($itemFetch['worker_id'] != 0) {
                $wrkInfo = $UserObj->getById($itemFetch['worker_id']);
                $themeData['order_worker_name'] = $wrkInfo['first_name'] . ' ' . $wrkInfo['last_name'];
            }
            $themeData['order_notes_hidden'] = 'hidden';
            $themeData['order_notes'] = '';
            if ($itemFetch['type'] == 1 || $itemFetch['type'] == 4) {
                $themeData['order_content'] = '';
                if (!empty($itemFetch['notes'])) {
                    $themeData['order_notes_hidden'] = '';
                    $themeData['order_notes'] = htmlspecialchars_decode(stripslashes($itemFetch['notes']));
                }
                $ordItems = $utiObj->getOrderItems($itemID);
                if (!empty($ordItems)) {
                    $themeData['order_content'] .= '<table width="100%"><thead><tr>';
                    $themeData['order_content'] .= '<th>' . Lang('item_name_label') . '</th>';
                    $themeData['order_content'] .= '<th>' . Lang('price_label') . '</th>';
                    $themeData['order_content'] .= '<th>' . Lang('qty_label') . '</th>';
                    $themeData['order_content'] .= '<th>' . Lang('total_label') . '</th>';
                    $themeData['order_content'] .= '</tr></thead>';
                    foreach ($ordItems as $k => $v) {
                        $ordItem = $utiObj->getItem($v['item_id']);
                        if (isset($ordItem['id'])) {
                            $features = '';
                            $ordItemInfo = $utiObj->getItemInfo($ordItem['id']);
                            if ($v['ft_id'] != 0) {
                                $ordFtFetch = $utiObj->getOrderItemFeatures($v['ft_id']);
                                if (!empty($ordFtFetch)) {
                                    foreach ($ordFtFetch as $oft) {
                                        $ftFetch = $utiObj->getItemFeatures(0, $oft['ft_id']);
                                        if (isset($ftFetch['id'])) {
                                            $ftCatFetch = $utiObj->getStoreCats($ftFetch['category']);
                                            $ftCat = isset($ftCatFetch['category_id']) ? ' ' . Lang($ftCatFetch['category_name'], 3) . ' : ' : '';
                                            $ordItem['price'] += isset($ftFetch['price']) ? $ftFetch['price'] : 0;
                                            $ftInfo = $utiObj->getItemFeaturesInfo($ftFetch['id']);
                                            if (isset($ftInfo['f_id']))
                                                $features .= '<br>' . $ftCat . $ftInfo['title'];
                                        }
                                    }
                                }
                            }
                            $themeData['order_content'] .= '<tr>';
                            $themeData['order_content'] .= '<td>' . $ordItemInfo['title'] . $features . '</td>';
                            $themeData['order_content'] .= '<td>' . round($ordItem['price'], 2) . ' ' . Lang('currency_1') . '</td>';
                            $themeData['order_content'] .= '<td>' . $v['qty'] . '</td>';
                            $themeData['order_content'] .= '<td>' . round($ordItem['price'] * $v['qty'], 2) . ' ' . Lang('currency_1') . '</td>';
                            $themeData['order_content'] .= '</tr>';
                        }
                    }
                    $themeData['order_content'] .= '</table>';
                }
            }
        }
        $html = \iCms\UI::view('backend/orders/view-content');
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}