<?php
$data = array();
if (!empty($_POST)) {
    $fetch = $dataObj->getLocationAlert($_POST);
    $viewData = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $n++;
            $timestamp = $v['time'];
            $datetimeFormat = 'Y-m-d h:i a';
            $date = new \DateTime();
            $date->setTimestamp($timestamp);
            $address = '';
            $location_address = geolocationaddress($v['lat'], $v['lon']);
            if (isset($location_address['addresses'][0]['address'])) {
                $fetch_address = $location_address['addresses'][0]['address'];
                $address .= isset($fetch_address['street']) ? $fetch_address['street'] . ',' : '';
                $address .= isset($fetch_address['municipalitySubdivision']) ? $fetch_address['municipalitySubdivision'] . ',' : '';
                $address .= isset($fetch_address['countrySubdivision']) ? $fetch_address['countrySubdivision'] : '';
            }
            $themeData['map_url'] = 'https://www.google.com/maps/?q=' . $v['lat'] . ',' . $v['lon'];
            $map_button = \iCms\UI::view('backend/orders/view-map-btn');
            $viewData[] = array(
                "DT_RowId" => 'tr_' . $v['id'],
                'id' => $n,
                'role' => $v['role'],
                'name' => $v['name'],
                'phone' => '<span dir="ltr">' . $v['phone'] . '</span>',
                'email' => '<span dir="ltr">' . $v['email'] . '</span>',
                'address' => '<span dir="ltr">' . $address . '</span>',
                'date' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                'map' => $map_button);
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewData);
}