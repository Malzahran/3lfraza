<?php
if (isset($_POST['item_id']) && isset($_POST['type']) && isset($_POST['from']) && isset($_POST['to'])) {
    $itemID = (int)$_POST['item_id'];
    $dataObj = new \iCms\DataGet();
    $filterDT = array();
    $filterDT['progress'] = array(1, 2, 3, 4, 5, 6);
    $reptype = (int)$_POST['type'];
    $ordtype = (int)$_POST['ordtype'];
    $filterDT['from_date'] = $_POST['from'];
    $filterDT['to_date'] = $_POST['to'];
    if ($reptype != 1) $userDObj = new \iCms\User();

    switch ($ordtype) {
        case 1:
            $filterDT['type'] = array(2, 3, 4);
            break;
        case 2:
        case 4:
            if ($sellerLogged) {
                $filterDT['type'] = array(1, 4);
            } else {
                $filterDT['type'] = array(1);
            }
            break;
        case 3:
            $filterDT['type'] = array(1, 2, 3, 4);
            break;
    }
    switch ($reptype) {
        case 1:
            $themeData['client_label'] = Lang('store_label');
            $filterDT['store_id'] = $itemID;
            $info = $utiObj->getStore($itemID);
            $themeData['order_client'] = $info['dir']['title'];
            break;
        case 2:
            $themeData['client_label'] = Lang('assigned_delv_label');
            $filterDT['delv_type'] = 0;
            if ($sellerLogged) {
                $filterDT['store_id'] = $user['store_id'];
                $filterDT['delv_type'] = 1;
            }
            $filterDT['dlvid'] = $itemID;
            $info = $userDObj->getById($itemID);
            $themeData['order_client'] = $info['name'];
            break;
        case 4:
            $themeData['client_label'] = Lang('assigned_wrk_label');
            if ($sellerLogged) $filterDT['store_id'] = $user['store_id'];
            $filterDT['workid'] = $itemID;
            $info = $userDObj->getById($itemID);
            $themeData['order_client'] = $info['name'];
            break;
        case 3:
            if ($user['type'] == "seller") $filterDT['store_id'] = $user['store_id'];
            $themeData['client_label'] = Lang('req_client_name_label');
            $filterDT['userid'] = $itemID;
            $info = $userDObj->getById($itemID);
            $themeData['order_client'] = $info['name'];
            break;
    }
    $fetch = $dataObj->getOrdrep($filterDT, $reptype);
    if (isset($fetch['data'][0])) {
        $date = new \DateTime();
        $datetimeFormat = 'd-m-Y';
        $themeData['today_date'] = $date->format($datetimeFormat);
        $themeData['from_date'] = $escapeObj->stringEscape($_POST['from']);
        $themeData['to_date'] = $escapeObj->stringEscape($_POST['to']);
        $themeData['pay_amount'] = round($fetch['data'][0]['total'] * 1.5, 2) . ' ' . Lang('currency_1');
        $themeData['orders_count'] = $fetch['data'][0]['total'];
        $themeData['orders_cost'] = round($fetch['data'][0]['sum'], 2) . ' ' . Lang('currency_1');
        $themeData['orders_discount'] = round($fetch['data'][0]['discount'], 2) . ' ' . Lang('currency_1');
        $themeData['orders_delivery'] = round($fetch['data'][0]['dlv'], 2) . ' ' . Lang('currency_1');
        $themeData['orders_total'] = ($fetch['data'][0]['dlv'] + $fetch['data'][0]['sum']) - $fetch['data'][0]['discount'] . ' ' . Lang('currency_1');
        $themeData['orders_data'] = '';
        $ordersFetch = $dataObj->getOrdIds($filterDT);
        if (isset($ordersFetch['data'])) {
            $n = 0;
            foreach ($ordersFetch['data'] as $v) {
                $ordinfo = $utiObj->getOrder($v);
                if (isset($ordinfo['order_id'])) {
                    $n++;
                    $timestamp = $ordinfo['time'];
                    $datetimeFormat = 'Y-m-d h:i a';
                    $date = new \DateTime();
                    $date->setTimestamp($timestamp);
                    $themeData['order_count'] = $n;
                    $themeData['order_number'] = $ordinfo['order_id'];
                    $themeData['order_cost'] = round($ordinfo['price'], 2) . ' ' . Lang('currency_1');
                    $themeData['order_discount'] = round($ordinfo['discount'], 2) . ' ' . Lang('currency_1');
                    $themeData['order_delivery'] = round($ordinfo['delivery'], 2) . ' ' . Lang('currency_1');
                    $themeData['order_date'] = '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>';
                    $themeData['orders_data'] .= \iCms\UI::view('backend/report/orders/report-detailes-row');
                }
            }
        }
        if ($sellerLogged) {
            $html = \iCms\UI::view('backend/report/orders/view-content-seller');
        } else {
            $html = \iCms\UI::view('backend/report/orders/view-content');
        }
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}