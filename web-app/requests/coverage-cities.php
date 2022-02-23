<?php
$locations = array();
if ($adminLogged || $sellerLogged) {
    $getObj = new \iCmsSeller\DataGet();
    $locGet = $getObj->getCityIds();
    if (isset($locGet['data'])) {
        foreach ($locGet['data'] as $v) {
            $cityFetch = $utiObj->getCity($v);
            $locations[] = array(
                'title' => Lang($cityFetch['city_name'], 2),
                'lat' => $cityFetch['lat'],
                'lon' => $cityFetch['lon'],
                'snippet' => $cityFetch['radius'] . ' KM',
                'radius' => $cityFetch['radius'],
            );
        }
    }
}
$data["status"] = 200;
$data["locations"] = $locations;