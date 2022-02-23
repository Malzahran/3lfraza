<?php
$items = array();
$userDObj = new \iCms\User();
$dataObj = new \iCms\DataGet();
$sourceLat = isset($data->user->latitude) ? $data->user->latitude : null;
$sourceLon = isset($data->user->longitude) ? $data->user->longitude : null;
$radiusKm = 10;
$filterDT = array();
$filterDT['geo_info'] = mathGeoProximity($sourceLat, $sourceLon, $radiusKm);
$limit = 10;
$btnid = 0;
$datapage = (int)$data->misc->page;
$page = ($datapage == 1) ? 1 : $datapage;
$start = ($datapage - 1) * $limit;
$filterDT['length'] = $limit;
$filterDT['start'] = $start;
$filterDT['dlvid'] = 0;
if ($user['type'] == "delivery") $filterDT['progress'] = array(3);
$fetch = $dataObj->getOrdIds($filterDT);
if (!empty($fetch['data'])) {
    $mediaObj = new \iCms\Media();
    foreach ($fetch['data'] as $v) {
        $ordinfo = $utiObj->getOrder($v);
        if (isset($ordinfo['order_id']) && !$ordinfo['delv_id']) {
            $timestamp = $ordinfo['time'];
            $datetimeFormat = 'Y-m-d h:i a';
            $date = new \DateTime();
            $date->setTimestamp($timestamp);
            $buttons = array();
            $img = $site_url . '/media/cart_icon.png';
            $store = '';
            $convid = $convtype = 0;
            $name = $ardate = $progress = $actionIntent = $action = $phone = $sms = $contact = $convname = null;
            $desc = '<h3><font color="red">' . Lang('req_order_detailes_label') . '</font></h3>';
            if ($ordinfo['type'] == 2 || $ordinfo['type'] == 3) {
                $desc .= '<h5>' . htmlspecialchars_decode(stripslashes($ordinfo['content'])) . '</h5>';
            } else {
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
                                            $ftCat = isset($ftCatFetch['category_id']) ? ' ' . Lang($ftCatFetch['category_name'], 3) . ' : ' : '';
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
                            $desc .= ' / ' . Lang('total_label') . ' : ' . round($ordItem['price'] * $va['qty'], 2) . ' ' . Lang('currency_1');
                            $desc .= '</b></span>';
                        }
                    }
                }
                if (!empty($ordinfo['arrival_time']) && !empty($ordinfo['arrival_date'])) {
                    $slot_label = '<span dir="ltr">';
                    $slot_label .= date("Y-m-d", strtotime($ordinfo['arrival_date']));
                    $slot_label .= date(" g:i a", strtotime($ordinfo['arrival_time'] . " - 1 hour"));
                    $slot_label .= '</span>';
                    $ardate = Lang('order_arrival_time_slot') . ': ' . $slot_label;
                }
                if (!empty($ordinfo['notes'])) {
                    $desc .= '<h3><font color="red">' . Lang('req_order_notes_label') . '</font></h3>';
                    $desc .= '<h5>' . htmlspecialchars_decode(stripslashes($ordinfo['notes'])) . '</h5>';
                }
                if ($ordinfo['progress'] != 7 && $user['type'] == "delivery") {
                    $desc .= '<h4><font color="red">' . Lang('req_client_address_label') . '</font></h4>';
                    $desc .= '<b>' . Lang('street_label') . $ordinfo['street'] . '<br>' . Lang('building_label') . $ordinfo['building'] . '<br>' . Lang('floor_label') . $ordinfo['floor'] . '<br>' . Lang('apartment_label') . $ordinfo['apartment'] . (!empty($ordinfo['additional']) ? '<br>' . Lang('additional_label') . $ordinfo['additional'] : '') . '</b>';
                }
            }

            if ($ordinfo['progress'] != 7 && ($ordinfo['price'] != 0 || $ordinfo['delivery'] != 0)) {
                $desc .= '<h6><font color="red">';
                if ($ordinfo['price'] != 0) $desc .= Lang('req_order_cost_label') . ' : ' . round($ordinfo['price'], 2) . ' ' . Lang('currency_1');
                if ($ordinfo['delivery'] != 0) {
                    if ($ordinfo['price'] != 0) $desc .= ' - ';
                    $desc .= Lang('req_order_delv_cost_label') . ' : ' . round($ordinfo['delivery'], 2) . ' ' . Lang('currency_1');
                }
                $desc .= ' / ' . Lang('total_label') . ' : ' . round($ordinfo['price'] + $ordinfo['delivery'], 2) . ' ' . Lang('currency_1');
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
            }
            if ($ordinfo['progress'] == 3) {
                if ($user['type'] == "delivery") {
                    $button_intent = array(
                        'intent' => 20,
                        'reqid' => $ordinfo['order_id'],
                        'reqaid' => $ordinfo['order_id'] . ($btnid += 1),
                        'reqsid' => $user['id'],
                        'reqtype' => 'order_delivery'
                    );
                    $buttons[] = array(
                        'id' => $ordinfo['order_id'] . ($btnid),
                        'text' => Lang('recived_action_btn'),
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
                'time' => $date->format($datetimeFormat),
                'phone' => $phone,
                'sms' => $sms,
                'click' => 0);
        }
    }
}
$layout = array(
    'layout' => 2,
    'indvclk' => 1,
    'allowback' => 1,
    'barback' => 1,
    'allowmore' => 1,
    'keepOn' => 1,
    'refreshInterval' => 25,
    'orientation' => 0,
    'searchtype' => 1,
    'showicon' => 0,
    'bartitle' => 1,
    'abtitle' => Lang('find_orders_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;