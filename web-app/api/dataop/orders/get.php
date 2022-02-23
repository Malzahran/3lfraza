<?php
$orders = array();
$dataObj = new \iCms\DataGet();
$filterDT = array();
$filterDT['userid'] = $user['id'];
$fetch = $dataObj->getOrdIds($filterDT);
if (!empty($fetch['data'])) {
    $mediaObj = new \iCms\Media();
    foreach ($fetch['data'] as $v) {
        $ordinfo = $utiObj->getOrder($v);
        if (isset($ordinfo['order_id'])) {
            $ordItems = $utiObj->getOrderItems($ordinfo['order_id']);
            if (!empty($ordItems)) {
                $items = array();
                foreach ($ordItems as $ke => $va) {
                    $ordItem = $utiObj->getItem($va['item_id']);
                    if (isset($ordItem['id'])) {
                        $features = array();
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
                                            $features[] = array('name' => '<small>' . $ftCat . $ftInfo['title'] . '</small>');
                                    }
                                }
                            }
                        }
                        $features[] = array('name' => '');
                        $itemTotal = $ordItem['price'] * $va['qty'];
                        $product_image = $site_url . '/media/logo.png';
                        if ($ordItem['featured_image'] != 0) {
                            $fimage = $mediaObj->getById($ordItem['featured_image']);
                            $product_image = $fimage['each'][0]['thumb_url'];
                        }
                        $items[] = array(
                            'price_item' => round($ordItem['price'], 2),
                            'product_name' => $ordItemInfo['title'],
                            'currency' => Lang('currency_1'),
                            'image' => $product_image,
                            'features' => $features,
                            'ftid' => $va['ft_id'],
                            'amount' => $va['qty'],
                            'total' => round($itemTotal, 2) . ' ' . Lang('currency_1'),
                            'product_id' => $ordItem['id']
                        );
                    }
                }
                $date = new \DateTime();
                $date->setTimestamp($ordinfo['time']);
                $progress = '';
                switch ($ordinfo['progress']) {
                    case 1:
                        $progress = Lang('pending_label');
                        break;
                    case 2:
                        $progress = Lang('order_received');
                        break;
                    case 3:
                    case 4:
                        $progress = Lang('order_scheduled');
                        break;
                    case 5:
                        $progress = Lang('order_on_the_way');
                        break;
                    case 6:
                        $progress = Lang('success_deliverd');
                        break;
                    case 7:
                        $progress = Lang('refused_type');
                        break;
                }
                $rating = 0;
                $getratingpr = $ordinfo['progress'] == 6 ? $utiObj->getRatingTotal($ordinfo['order_id'], 4) : '';
                if (isset($getratingpr['count']) && $getratingpr['count'] != 0) {
                    $sumrate = $getratingpr['rate'];
                    $sumvoters = $getratingpr['count'];
                    $rating = $sumrate / $sumvoters;
                }
                $orders[] = array(
                    'id' => $ordinfo['order_id'],
                    'code' => $ordinfo['order_id'],
                    'refuse' => $ordinfo['refuse_reasons'],
                    'rating' => $rating,
                    'a_rating' => $rating == 0 && $ordinfo['progress'] == 6 ? 1 : 0,
                    'total_fees' => ($ordinfo['price'] + $ordinfo['delivery']) - $ordinfo['discount'] . ' ' . Lang('currency_1'),
                    'status' => $progress,
                    'notes' => $ordinfo['notes'],
                    'cart_list' => $items,
                    'time' => $date->format('Y-m-d h:i a')
                );
            }
        }
    }
}
$response['result'] = 'success';
$response['orders'] = $orders;