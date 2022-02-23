<?php
if (!empty($_POST)) {
    $filterData = array();
    $fetch = $dataObj->getCityIds($_POST, $filterData);
    $viewData = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $n++;
            $cityInfo = $utiObj->getCity($v);

            $timestamp = $cityInfo['time'];
            $datetimeFormat = 'Y-m-d h:i a';
            $date = new \DateTime();
            $date->setTimestamp($timestamp);
            $themeData['item_id'] = $cityInfo['city_id'];
            $themeData['item_name'] = Lang($cityInfo['city_name'], 2);
            $themeData['more_mange'] = '';
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/edit-btn');
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/delete-btn');
            $moremange = \iCms\UI::view('backend/global/buttons/more-btn');


            $viewData[] = array(
                "DT_RowId" => 'tr_' . $cityInfo['city_id'],
                'id' => $n,
                'title' => $themeData['item_name'],
                'group' => $cityInfo['city_group'],
                'lat' => $cityInfo['lat'],
                'lon' => $cityInfo['lon'],
                'radius' => '<span dir="ltr">' . $cityInfo['radius'] . ' KM</span>',
                'shipping' => $cityInfo['shipping'] != 0 ? round($cityInfo['shipping'], 2) . ' ' . Lang('currency_1') : '',
                'date' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                'more' => $moremange);
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewData);
}