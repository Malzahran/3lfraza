<?php
$items = array();
$userDObj = new \iCms\User();
$dataObj = new \iCms\DataGet();
$filterDT = array();
$limit = 10;
$btnid = 0;
$datapage = (int)$data->misc->page;
$page = ($datapage == 1) ? 1 : $datapage;
$start = ($datapage - 1) * $limit;
$filterDT['length'] = $limit;
$filterDT['start'] = $start;
$title = Lang('orders_label');
if ($user['type'] == "delivery") $filterDT['dlvid'] = $user['id'];
else if ($user['type'] == "worker") $filterDT['workid'] = $user['id'];
else if ($user['type'] == "seller") {
    $filterDT['store_id'] = $user['store_id'];
} else {
    $filterDT['userid'] = $user['id'];
}
$fetch = $dataObj->getOrdIds($filterDT);
if (!empty($fetch['data'])) {
    $mediaObj = new \iCms\Media();
    foreach ($fetch['data'] as $v) {
        $ordinfo = $utiObj->getOrder($v);
        $timestamp = $ordinfo['time'];
        $datetimeFormat = 'Y-m-d h:i a';
        $date = new \DateTime();
        $date->setTimestamp($timestamp);
        $buttons = array();
        $img = $site_url . '/media/cart_icon.png';
        $store = '';
        $convid = $convtype = 0;
        $name = $ardate = $progress = $actionIntent = $action = $phone = $sms = $contact = $convname = null;
        if ($ordinfo['progress'] != 6 && $ordinfo['progress'] != 7 && $user['type'] == "seller") {
            $contact = Lang('contact_client_label');
            $phone = $sms = $ordinfo['user_phone'];
            $name = Lang('req_client_name_label') . ' ' . $ordinfo['user_name'];
        }
        if (!empty($ordinfo['arrival_time']) && !empty($ordinfo['arrival_date'])) {
            $slot_label = '<span dir="ltr">';
            $slot_label .= date("Y-m-d", strtotime($ordinfo['arrival_date']));
            $slot_label .= date(" g:i a", strtotime($ordinfo['arrival_time'] . " - 1 hour"));
            $slot_label .= '</span>';
            $ardate = Lang('order_arrival_time_slot') . ': ' . $slot_label;
        }
        $desc = '<h3><font color="red">' . Lang('req_order_detailes_label') . '</font></h3>';
        if ($ordinfo['type'] == 1 || $ordinfo['type'] == 4) {
            $storeInfo = $utiObj->getStore($ordinfo['store_id']);
            if (isset($storeInfo['store']['store_id'])) {
                $pInfo = $utiObj->getDirectory(0, 0, $storeInfo['dir']['parent_id']);
                if ($pInfo['featured_image'] != 0) {
                    $fimage = $mediaObj->getById($pInfo['featured_image']);
                    $img = $fimage['each'][0]['thumb_url'];
                } else $img = $site_url . '/media/store_icon.png';
                $store = $storeInfo['dir']['title'] . ' - ';
            }
            $ordItems = $utiObj->getOrderItems($ordinfo['order_id']);
            if (!empty($ordItems)) {
                foreach ($ordItems as $ke => $va) {
                    $ordItem = $utiObj->getItem($va['item_id']);
                    if (isset($ordItem['id'])) {
                        $features = '';
                        $ordItemInfo = $utiObj->getItemInfo($ordItem['id']);
                        if ($va['ft_id'] != 0) {
                            $ordFtFetch = $utiObj->getOrderItemFeatures($va['ft_id']);
                            if (!empty($ordFtFetch)) {
                                foreach ($ordFtFetch as $oft) {
                                    $ftFetch = $utiObj->getItemFeatures(0, $oft['ft_id']);
                                    if (isset($ftFetch['id'])) {
                                        $ftCatFetch = $utiObj->getStoreCats($ftFetch['category']);
                                        $ftCat = isset($ftCatFetch['category_id']) ? ' ' . Lang($ftCatFetch['category_name'], 3) . ': ' : '';
                                        $ordItem['price'] += isset($ftFetch['price']) ? $ftFetch['price'] : 0;
                                        $ftInfo = $utiObj->getItemFeaturesInfo($ftFetch['id']);
                                        if (isset($ftInfo['f_id']))
                                            $features .= '<br>' . $ftCat . $ftInfo['title'];
                                    }
                                }
                            }
                        }
                        $desc .= '<span><b>' . $ordItemInfo['title'] . $features . ' ';
                        $desc .= '<br>( ' . round($ordItem['price'], 2) . ' x ' . $va['qty'] . ' )';
                        $desc .= ' / ' . Lang('total_label') . ': ' . round($ordItem['price'] * $va['qty'], 2) . ' ' . Lang('currency_1');
                        $desc .= !empty($ordItemInfo['notes']) && ($user['type'] == "worker" || $user['type'] == "seller") ? '<br><font color="red">' . Lang('preparation_notes_label') . ': ' . $ordItemInfo['notes'] . '</font>' : '';
                        $desc .= '</b></span>';
                    }
                }
            }
            if (!empty($ordinfo['notes'])) {
                $desc .= '<h3><font color="red">' . Lang('req_order_notes_label') . '</font></h3>';
                $desc .= '<h5>' . htmlspecialchars_decode(stripslashes($ordinfo['notes'])) . '</h5>';
            }
            if ($ordinfo['progress'] == 7 && !empty($ordinfo['refuse_reasons'])) {
                $button_intent = array(
                    'intent' => 27,
                    'reqatype' => Lang('refuse_reason'),
                    'reqtype' => htmlspecialchars_decode(stripslashes($ordinfo['refuse_reasons']))

                );
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid += 1),
                    'text' => Lang('refuse_reason'),
                    'color' => 1,
                    'intentData' => $button_intent);
            }
            if ($user['type'] == "seller" || ($user['type'] == "delivery" && $ordinfo['progress'] == 5)) {
                $desc .= '<h4><font color="red">' . Lang('req_client_address_label') . '</font></h4>';
                $desc .= '<b>' . Lang('street_label') . $ordinfo['street'] . '<br>' . Lang('building_label') . $ordinfo['building'] . '<br>' . Lang('floor_label') . $ordinfo['floor'] . '<br>' . Lang('apartment_label') . $ordinfo['apartment'] . (!empty($ordinfo['additional']) ? '<br>' . Lang('additional_label') . $ordinfo['additional'] : '') . '</b>';
            }
        }

        if ($user['type'] == "seller" || ($user['type'] == "delivery" && $ordinfo['progress'] != 7)) {
            $desc .= '<h6><font color="red">';
            if ($ordinfo['price'] != 0) $desc .= Lang('req_order_cost_label') . ' : ' . round($ordinfo['price'], 2) . ' ' . Lang('currency_1');
            if ($ordinfo['delivery'] != 0) {
                if ($ordinfo['price'] != 0) $desc .= ' - ';
                $desc .= Lang('req_order_delv_cost_label') . ': ' . round($ordinfo['delivery'], 2) . ' ' . Lang('currency_1');
            }
            if ($ordinfo['discount'] != 0) {
                if ($ordinfo['price'] != 0) $desc .= ' - ';
                $desc .= Lang('pos_dsc_label') . ': ' . round($ordinfo['discount'], 2) . ' ' . Lang('currency_1');
            }
            $desc .= ' / ' . Lang('total_label') . ': ' . round(($ordinfo['price'] + $ordinfo['delivery']) - $ordinfo['discount'], 2) . ' ' . Lang('currency_1');
            $desc .= '</font></h6>';
        }

        switch ($ordinfo['progress']) {
            case 1:
                $progress = Lang('pending_label');
                break;
            case 2:
                $progress = Lang('order_received');
                break;
            case 3:
                $progress = Lang('preparing_label');
                break;
            case 4:
                $progress = Lang('order_prepared');
                break;
            case 5:
                $progress = Lang('with_delivery_label');
                break;
            case 6:
                $progress = Lang('success_deliverd');
                break;
            case 7:
                $progress = Lang('refused_type');
                break;
        }
        if ($user['type'] == "seller" || ($user['type'] == "delivery" && $ordinfo['progress'] == 5)) {
            if ($ordinfo['lat'] != 0 && $ordinfo['lon'] != 0) {
                $action = Lang('view_client_map');
                $actionIntent = array('intent' => 3, 'reqtype' => 'orders', 'reqstype' => 'getmap', 'reqid' => $ordinfo['order_id']);
            }
        }
        if ($ordinfo['progress'] == 1) {
            if ($user['type'] == "seller") {
                $button_intent = array(
                    'intent' => 20,
                    'reqid' => $ordinfo['order_id'],
                    'reqaid' => $ordinfo['order_id'] . ($btnid += 1),
                    'reqsid' => 2,
                    'reqtype' => 'order_progress'
                );
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid),
                    'text' => Lang('recived_action_btn'),
                    'intentData' => $button_intent);
                $intentdata = array('intent' => 6, 'reqtype' => 'orders', 'reqstype' => 'refuse_order_form', 'reqptype' => 'refuse_order', 'reqid' => $ordinfo['order_id']);
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid += 1),
                    'text' => Lang('ord_refuse_btn'),
                    'color' => 1,
                    'intentData' => $intentdata);
            }
        }
        if ($user['type'] == "seller" && $ordinfo['progress'] > 1 && $ordinfo['progress'] < 6) {
            if ($aDLV && $ordinfo['type'] == 1) {
                $intentdata = array('intent' => 6, 'reqtype' => 'orders', 'reqstype' => 'assign_worker_form', 'reqptype' => 'assign_worker', 'reqid' => $ordinfo['order_id']);
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid += 1),
                    'text' => Lang('assign_worker_label'),
                    'intentData' => $intentdata);
            }
            if ($aDLV && $ordinfo['type'] == 1) {
                $intentdata = array('intent' => 6, 'reqtype' => 'orders', 'reqstype' => 'assign_delivery_form', 'reqptype' => 'assign_delivery', 'reqid' => $ordinfo['order_id']);
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid += 1),
                    'text' => Lang('assign_delv_label'),
                    'intentData' => $intentdata);
            }
        }
        if ($ordinfo['progress'] == 3 && $ordinfo['delv_id'] != 0) {
            if ($user['type'] == "worker") {
                $button_intent = array(
                    'intent' => 20,
                    'reqid' => $ordinfo['order_id'],
                    'reqaid' => $ordinfo['order_id'] . ($btnid += 1),
                    'reqsid' => $ordinfo['delv_id'],
                    'reqtype' => 'order_prepared'
                );
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid),
                    'text' => Lang('order_prepared'),
                    'intentData' => $button_intent);
            }
        }
        if ($ordinfo['progress'] == 4) {
            if ($user['type'] == "delivery") {
                $userDObj = new \iCms\User();
                $worker_info = $userDObj->getById($ordinfo['worker_id']);
                if (isset($worker_info['id'])) {
                    $desc .= '<h4><font color="red">' . Lang('worker_address') . '</font></h4>';
                    $desc .= '<b>' . Lang('street_label') . $worker_info['street'] . '<br>' . Lang('building_label') . $worker_info['building'] . '<br>' . Lang('floor_label') . $worker_info['floor'] . '<br>' . Lang('apartment_label') . $worker_info['apartment'] . (!empty($worker_info['additional']) ? '<br>' . Lang('additional_label') . $worker_info['additional'] : '') . '</b>';
                    if ($worker_info['lat'] != 0 && $worker_info['lon'] != 0) {
                        $action = Lang('view_worker_map');
                        $actionIntent = array('intent' => 3, 'reqtype' => 'orders', 'reqstype' => 'get_worker_map', 'reqid' => $ordinfo['order_id']);
                    }
                    if (!empty($worker_info['phone'])) {
                        $contact = Lang('contact_worker_label');
                        $phone = $sms = $worker_info['user_phone'];
                    }
                    $name = Lang('req_worker_name_label') . ' ' . $worker_info['name'];
                }
                $button_intent = array(
                    'intent' => 20,
                    'reqid' => $ordinfo['order_id'],
                    'reqaid' => $ordinfo['order_id'] . ($btnid += 1),
                    'reqsid' => $ordinfo['user_id'],
                    'reqtype' => 'order_on_way'
                );
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid),
                    'text' => Lang('recived_action_btn'),
                    'intentData' => $button_intent);
            }
        }
        if ($ordinfo['progress'] == 5) {
            if ($user['type'] == "delivery") {
                $contact = Lang('contact_client_label');
                $phone = $sms = $ordinfo['user_phone'];
                $name = Lang('req_client_name_label') . ' ' . $ordinfo['user_name'];
                $button_intent = array(
                    'intent' => 20,
                    'reqid' => $ordinfo['order_id'],
                    'reqaid' => $ordinfo['order_id'] . ($btnid += 1),
                    'reqsid' => $ordinfo['user_id'],
                    'reqtype' => 'order_delivered'
                );
                $buttons[] = array(
                    'id' => $ordinfo['order_id'] . ($btnid),
                    'text' => Lang('success_deliverd'),
                    'intentData' => $button_intent);
            }
        }
        $items[] = array(
            'title' => $store . Lang('req_order_number_label') . ' ' . $ordinfo['order_id'],
            'id' => $ordinfo['order_id'],
            'actionintent' => $actionIntent,
            'action' => $action,
            'buttons' => $buttons,
            'minfo' => $name,
            'convid' => $convid,
            'convname' => $convname,
            'type' => $convtype,
            'minfo2' => $ardate,
            'desc' => $desc,
            'image_url' => $img,
            'contact_title' => $contact,
            'state' => $progress,
            'time' => $date->format($datetimeFormat),
            'phone' => $phone,
            'sms' => $sms,
            'click' => 0);
    }
}
$layout = array(
    'layout' => 2,
    'indvclk' => 1,
    'allowback' => 1,
    'barback' => 1,
    'allowmore' => 1,
    'refreshInterval' => 0,
    'orientation' => 0,
    'searchtype' => 1,
    'showicon' => 0,
    'bartitle' => 1,
    'abtitle' => $title
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;