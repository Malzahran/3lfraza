<?php
$recordsWithinRadius = array();
$dataObj = new \iCms\DataGet();
$sourceLat = isset($data->tracker->latitude) ? $data->tracker->latitude : null;
$sourceLon = isset($data->tracker->longitude) ? $data->tracker->longitude : null;
$role = $data->user->role;
$radiusKm = 500;
$geoInfo = mathGeoProximity($sourceLat, $sourceLon, $radiusKm);
$fetch_cities = $utiObj->getCity(0, array(2, 3), $geoInfo);
if ($fetch_cities) {
    foreach ($fetch_cities as $city) {
        $distance = mathGeoDistance($sourceLat, $sourceLon, $city['lat'], $city['lon']);
        if ($distance <= $city['radius']) {
            $available = false;
            if ($role === 'client') {
                if (isset($user['type']) && $user['type'] == 'seller') $available = true;
                else {
                    $filterDT = array();
                    $filterDT['store_id'] = 1;
                    $filterDT['utype'] = array('worker');
                    $filterDT['geo_info'] = mathGeoProximity($city['lat'], $city['lon'], 10);
                    $get_active_workers = $dataObj->getUserIds($filterDT);
                    $check_worker = !empty($get_active_workers['data']) ? true : false;
                    $filterDT['utype'] = array('delivery');
                    $filterDT['delv_type'] = 1;
                    $get_active_delivery = $dataObj->getUserIds($filterDT);
                    $check_delivery = !empty($get_active_delivery['data']) ? true : false;
                    if ($check_worker && $check_delivery) $available = true;
                }
            } else $available = true;
            if ($available) {
                $city['distance'] = $distance;
                $recordsWithinRadius[] = $city;
            }
        }
    }
}
if (isset($recordsWithinRadius[0]['city_id'])) {
    $response["result"] = "success";
    $response["city"] = array('city_id' => $recordsWithinRadius[0]['city_id'], 'city_group' => $recordsWithinRadius[0]['city_group'], 'city_desc' => Lang('your_area_label'), 'city_name' => Lang($recordsWithinRadius[0]['city_name'], 2));
} else {
    $response["result"] = "no_cover";
    $response["message"] = Lang('no_coverage_msg');
}