<?php
$locations = array();
if ($adminLogged || $sellerLogged) {
    $locObj = new \iCms\LocationController();
    $filterDT = array();
    if ($adminLogged) {
        $filterDT['delv_type'] = 0;
    } else if ($user['type'] == "seller") {
        $filterDT['delv_type'] = 1;
        $filterDT['store_id'] = $user['store_id'];
    }
    $locget = $locObj->getLocations($filterDT);

    if ($locget) {
        $userDObj = new \iCms\User();
        foreach ($locget as $k => $v) {
            $userginfo = $userDObj->getById($v['userid']);
            $time = $v['time'];
            $comptime = time() - (2 * 60);
            $datetimeFormat = '(m-d) h:i a';
            $datetimeFormat2 = 'h:i a';
            $date = new \DateTime();
            $date->setTimestamp($time);
            $dtime = $date->format($datetimeFormat);
            $dtime2 = $date->format($datetimeFormat2);
            $fspeed = '';
            if ($v['speed'] > 0) {
                $speed = $v['speed'] / 0.62137119;
                $speed = round($speed, 2);
            }
            if ($time >= $comptime) {
                if ($v['speed'] > 0) {
                    $fspeed = '/ ' . Lang('speed_label') . ' : ' . $speed . ' ' . Lang('km_label');
                }
                $sinppet = Lang('last_update_label') . ' : ' . $dtime2 . $fspeed;
                $color = 1;
            } else {
                if ($v['speed'] > 0) {
                    $fspeed = '/ ' . Lang('last_speed_label') . ' : ' . $speed . ' ' . Lang('km_label');
                }
                $sinppet = Lang('last_update_label') . ' : ' . $dtime . $fspeed;
                $color = 0;
            }
            $locations[] = array(
                'title' => $userginfo['name'],
                'snippet' => $sinppet,
                'color' => $color,
                'lat' => $v['lat'],
                'longt' => $v['lon'],
            );
        }
    }
}
$data["status"] = 200;
$data["locations"] = $locations;