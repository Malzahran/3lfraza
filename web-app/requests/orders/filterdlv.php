<?php
$html = '';
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $dataObj = new \iCms\DataGet();
    $UserDObj = new \iCms\User();
    $filterdata = array();
    $filterdata['utype'] = array('delivery');
    if ($sellerLogged) {
        $filterdata['store_id'] = $user['store_id'];
        $filterdata['delv_type'] = 1;
    } else if ($adminLogged) {
        $filterdata['delv_type'] = 0;
    }
    $deliveryIds = $dataObj->getUserIds($filterdata);
    if (isset($deliveryIds['data'])) {
        $themeData['sel_id'] = 0;
        $themeData['sel_name'] = Lang('no_filter_label');
        $html .= \iCms\UI::view('backend/global/select/selectopt');

        foreach ($deliveryIds['data'] as $k => $v) {
            $uinfo = $UserDObj->getById($v);
            $themeData['sel_id'] = $uinfo['id'];
            $themeData['sel_name'] = $uinfo['name'];
            $html .= \iCms\UI::view('backend/global/select/selectopt');
        }

    }
    if (!empty($html)) {
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}