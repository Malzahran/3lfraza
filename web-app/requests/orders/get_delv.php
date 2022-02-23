<?php
if (isset($_POST['item_id'])) {
    $html = '';
    $orderdID = (int)$_POST['item_id'];
    $ordinfo = $utiObj->getOrder($orderdID);
    if (isset($ordinfo['order_id'])) {
        $userDObj = new \iCms\User();
        $dataObj = new \iCms\DataGet();
        $html = '';
        if ($ordinfo['delv_id'] != 0) {
            $userFetch = $userDObj->getById($ordinfo['delv_id']);
            $agent = $userFetch['name'];
            $agentID = $userFetch['id'];
            $html = '<option value="' . $agentID . '">' . $agent . '</option>';
        }
        $filterdata = array();
        $filterdata['delv_type'] = 0;
        $filterdata['utype'] = array('delivery');
        $filterdata['geo_info'] = mathGeoProximity($ordinfo['lat'], $ordinfo['lon'], 10);
        if ($sellerLogged) {
            $filterdata['store_id'] = $user['store_id'];
            $filterdata['delv_type'] = 1;
        }
        $delvFetch = $dataObj->getUserIds($filterdata);

        if (!empty($delvFetch['data'])) {
            foreach ($delvFetch['data'] as $k => $v) {
                if ($ordinfo['delv_id'] != $v) {
                    $uinfo = $userDObj->getById($v);
                    $themeData['sel_id'] = $uinfo['id'];
                    $themeData['sel_name'] = $uinfo['name'];
                    $html .= \iCms\UI::view('backend/global/select/selectopt');
                }
            }
        } else {
            $themeData['sel_id'] = 0;
            $themeData['sel_name'] = Lang('req_order_sell_delv_label');
            $html .= \iCms\UI::view('backend/global/select/selectopt');
        }

        if (!empty($html)) {
            $data = array(
                'status' => 200,
                'html' => $html);
        }
    }
}