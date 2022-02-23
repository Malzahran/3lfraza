<?php
if (isset($_POST['item_id'])) {
    $html = '';
    $orderdID = (int)$_POST['item_id'];
    $ordinfo = $utiObj->getOrder($orderdID);
    if (isset($ordinfo['order_id'])) {
        $userDObj = new \iCms\User();
        $dataObj = new \iCms\DataGet();
        $html = '';
        if ($ordinfo['worker_id'] != 0) {
            $userFetch = $userDObj->getById($ordinfo['worker_id']);
            $agent = $userFetch['name'];
            $agentID = $userFetch['id'];
            $html = '<option value="' . $agentID . '">' . $agent . '</option>';
        }
        $filterdata = array();
        $filterdata['utype'] = array('worker');
        $filterdata['geo_info'] = mathGeoProximity($ordinfo['lat'], $ordinfo['lon'], 10);
        if ($sellerLogged) $filterdata['store_id'] = $user['store_id'];
        $wrkFetch = $dataObj->getUserIds($filterdata);
        if (!empty($wrkFetch['data'])) {
            foreach ($wrkFetch['data'] as $k => $v) {
                if ($ordinfo['worker_id'] != $v) {
                    $uinfo = $userDObj->getById($v);
                    $themeData['sel_id'] = $uinfo['id'];
                    $themeData['sel_name'] = $uinfo['name'];
                    $html .= \iCms\UI::view('backend/global/select/selectopt');
                }
            }
        } else {
            $themeData['sel_id'] = 0;
            $themeData['sel_name'] = Lang('req_order_sell_worker_label');
            $html .= \iCms\UI::view('backend/global/select/selectopt');
        }

        if (!empty($html)) {
            $data = array(
                'status' => 200,
                'html' => $html);
        }
    }
}