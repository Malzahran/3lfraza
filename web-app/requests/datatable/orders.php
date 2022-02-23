<?php
$data = array();
if (!empty($_POST)) {
    $filterDT = array();
    if ($user['type'] == "delivery") {
        $filterDT['dlvid'] = $user['id'];
    } else if ($user['type'] == "worker") {
        $filterDT['workid'] = $user['id'];
    } else if ($sellerLogged) {
        $filterDT['store_id'] = $user['store_id'];
    }
    $fetch = $dataObj->getOrdIds(array_merge($filterDT, $_POST));
    $viewdata = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        $userDObj = new \iCms\User();
        foreach ($fetch['data'] as $v) {
            $n++;
            $ordinfo = $utiObj->getOrder($v);
            if (isset($ordinfo['order_id'])) {
                $timestamp = $ordinfo['time'];
                $datetimeFormat = 'Y-m-d h:i a';
                $date = new \DateTime();
                $date->setTimestamp($timestamp);
                $agent = '';
                if ($ordinfo['delv_id'] != 0) {
                    $userFetch = $userDObj->getById($ordinfo['delv_id']);
                    $agent = $userFetch['name'];
                }
                $worker = '';
                if ($ordinfo['worker_id'] != 0) {
                    $userFetch = $userDObj->getById($ordinfo['worker_id']);
                    $worker = $userFetch['name'];
                }
                $area = '';
                if ($ordinfo['city'] != 0) {
                    $area_fetch = $utiObj->getCity($ordinfo['city']);
                    $area = isset($area_fetch['city_id']) ? Lang($area_fetch['city_name'], 2) : Lang('no_filter_label');
                }
                $state = '';
                switch ($ordinfo['progress']) {
                    case 1:
                        $state = '<span class="label label-warning">' . Lang('pending_label') . '</span>';
                        break;
                    case 2:
                        $state = '<span class="label label-warning">' . Lang('order_received') . '</span>';
                        break;
                    case 3:
                        $state = '<span class="label label-warning">' . Lang('preparing_label') . '</span>';
                        break;
                    case 4:
                        $state = '<span class="label label-warning">' . Lang('order_prepared') . '</span>';
                        break;
                    case 5:
                        $state = '<span class="label label-warning">' . Lang('with_delivery_label') . '</span>';
                        break;
                    case 6:
                        $state = '<span class="label label-success">' . Lang('success_deliverd') . '</span>';
                        break;
                    case 7:
                        $state = '<span class="label label-danger">' . Lang('refused_type') . '</span>';
                        break;
                }


                $content = '';
                if ($ordinfo['type'] == 1 || $ordinfo['type'] == 4) {
                    $storeInfo = $utiObj->getStore($ordinfo['store_id']);
                    if (!empty($ordinfo['notes']))
                        $content .= Lang('req_order_notes_label') . ' : ' . htmlspecialchars_decode(stripslashes($ordinfo['notes'])) . '<br>';
                    if (!empty($ordinfo['arrival_time']) && !empty($ordinfo['arrival_date'])) {
                        $slot_label = '<span dir="ltr">';
                        $slot_label .= date("Y-m-d", strtotime($ordinfo['arrival_date']));
                        $slot_label .= date(" g:i a", strtotime($ordinfo['arrival_time'] . " - 1 hour"));
                        $slot_label .= '</span>';
                        $content .= Lang('order_arrival_time_slot') . ': ' . $slot_label . '<br>';
                    }
                    if ($ordinfo['progress'] == 7 && !empty($ordinfo['refuse_reasons']))
                        $content .= Lang('refuse_reason') . ' : ' . htmlspecialchars_decode(stripslashes($ordinfo['refuse_reasons'])) . '<br>';
                }
                $themeData['item_id'] = $ordinfo['order_id'];
                $themeData['item_name'] = Lang('order_number_label') . ' ' . $ordinfo['order_id'];

                if (((@$admin['system']) || (@$admin['ord']['view'] || @$admin['ord']['add'] || @$admin['ord']['edit'] || @$admin['ord']['del'])) || $sellerLogged) {
                    $themeData['more_mange'] = '';
                    if ((@$admin['system'] || @$admin['ord']['view']) || ($sellerLogged && $ordinfo['progress'] != 6)) {
                        if (!empty($ordinfo['lon']) && !empty($ordinfo['lat'])) {
                            $themeData['map_url'] = 'https://www.google.com/maps/?q=' . $ordinfo['lat'] . ',' . $ordinfo['lon'];
                            $themeData['more_mange'] .= \iCms\UI::view('backend/orders/view-map-btn');
                        }
                    }

                    if ($ordinfo['progress'] == 1) {
                        $themeData['more_mange'] .= \iCms\UI::view('backend/orders/refuse-btn');
                        if ($sellerLogged) $themeData['more_mange'] .= \iCms\UI::view('backend/orders/order-confirm-btn');
                    }

                    if ((@$admin['system'] || @$admin['ord']['add']) || $sellerLogged) {
                        if ($ordinfo['delv_id'] == 0 && $ordinfo['progress'] > 1 && $ordinfo['progress'] != 7)
                            $themeData['more_mange'] .= \iCms\UI::view('backend/orders/assign-delv-btn');
                        if ($ordinfo['worker_id'] == 0 && $ordinfo['progress'] > 1 && $ordinfo['progress'] != 7)
                            $themeData['more_mange'] .= \iCms\UI::view('backend/orders/assign-wrk-btn');
                    }
                    if ((@$admin['system'] || @$admin['ord']['edit']) || $sellerLogged) {
                        if ($ordinfo['delv_id'] != 0 && $ordinfo['progress'] > 1 && $ordinfo['progress'] != 7)
                            $themeData['more_mange'] .= \iCms\UI::view('backend/orders/edit-delv-btn');
                        if ($ordinfo['worker_id'] != 0 && $ordinfo['progress'] > 1 && $ordinfo['progress'] != 7)
                            $themeData['more_mange'] .= \iCms\UI::view('backend/orders/edit-wrk-btn');
                    }
                }
                $moremange = \iCms\UI::view('backend/global/buttons/morebtn');
                $viewdata[] = array(
                    "DT_RowId" => 'tr_' . $ordinfo['order_id'],
                    'id' => $n,
                    'number' => $ordinfo['order_id'],
                    'cost' => round($ordinfo['price'], 2),
                    'discount' => round($ordinfo['discount'], 2),
                    'dlv_cost' => round($ordinfo['delivery'], 2),
                    'detailes' => $content,
                    'agent' => $agent,
                    'worker' => $worker,
                    'time' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                    'area' => $area,
                    'state' => $state,
                    'more' => $moremange);
            }
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewdata);
}