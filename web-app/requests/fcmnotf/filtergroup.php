<?php
$html = '';
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $totalcount = $fcmObj->getTokens(array('count' => 1));
    if ($totalcount != 0) {
        $themeData['sel_id'] = 'all';
        $themeData['sel_name'] = Lang('all_label') . ' (' . $totalcount . ')';
        $html .= \iCms\UI::view('backend/global/select/selectopt');

        $cityfilter = $fcmObj->CitiesFilter();
        if (!empty($cityfilter)) {
            $filterdcities = array();
            foreach ($cityfilter as $k => $v) {
                if ($v != 5000) {
                    $cityinfo = $utiObj->getCity($v);
                    $cityname = Lang($cityinfo['city_name'], 2);
                } else {
                    $cityname = Lang('no_filter_label');
                }
                $totalcount = $fcmObj->getTokens(array('fgroup' => $v, 'count' => 1));
                $themeData['sel_id'] = $v;
                $themeData['sel_name'] = $cityname . ' (' . $totalcount . ')';
                $html .= \iCms\UI::view('backend/global/select/selectopt');
            }
        }
    }
    if (!empty($html)) {
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}