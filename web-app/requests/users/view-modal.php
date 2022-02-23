<?php
if (!empty($_POST['itemid'])) {
    $itemid = (int)$_POST['itemid'];
    $UserDObj = new \iCms\User();
    $uinfo = $UserDObj->getById($itemid);

    $themeData['user_info_username'] = $uinfo['username'];
    $themeData['user_info_name'] = $uinfo['name'];
    $themeData['user_info_email'] = $uinfo['email'];
    if (!empty($uinfo['phone'])) {
        $themeData['user_info_phone'] = Lang('phone_label') . ' : ' . $uinfo['phone'] . '<hr>';
    }

    $themeData['user_info_img'] = $uinfo['avatar_url'];

    switch ($uinfo['authmethod']) {
        case 'web':
            $regfrom = '<span class="label label-inverse"><i class="fa fa-globe"></i> ' . Lang('form_web') . '</span>';
            break;
        case 'form':
            $regfrom = '<span class="label label-success"><i class="fa fa-mobile"></i> ' . Lang('form_mobile') . '</span>';
            break;
        case 'facebook':
            $regfrom = '<span class="label label-default"><i class="fa fa-facebook"></i> ' . Lang('facebook_reg') . '</span>';
            break;
        case 'google':
            $regfrom = '<span class="label label-danger"><i class="fa fa-google"></i> ' . Lang('google_reg') . '</span>';
            break;
    }
    $themeData['user_info_auth'] = $regfrom;
    if ($uinfo['havetoken'] == true) {
        $themeData['user_info_notf'] = '<span class="label label-warning"><i class="fa fa-bell"></i></span>';
    }
    $datetimeFormat = 'Y-m-d h:i a';
    $regdate = new \DateTime();
    $regdate->setTimestamp($uinfo['time']);
    $themeData['user_info_reg'] = '<span dir="ltr">' . $regdate->format($datetimeFormat) . '</span>';
    $themeData['user_info_last_seen'] = null;
    if ($uinfo['last_logged'] != 0) {
        $lastwebdate = new \DateTime();
        $lastwebdate->setTimestamp($uinfo['last_logged']);
        $themeData['user_info_last_seen'] .= Lang('last_seen_web') . ' : <span dir="ltr">' . $lastwebdate->format($datetimeFormat) . '</span><hr>';
    }
    if ($uinfo['app_lastlogged'] != 0) {
        $lastappdate = new \DateTime();
        $lastappdate->setTimestamp($uinfo['app_lastlogged']);
        $themeData['user_info_last_seen'] .= Lang('last_seen_app') . ' : <span dir="ltr">' . $lastappdate->format($datetimeFormat) . '</span><hr>';
    }
    if ($uinfo['current_city'] != 0) {
        $city = Lang('no_filter_label');
        if ($uinfo['current_city'] != 500) {
            $city_info = $utiObj->getCity($uinfo['current_city']);
            if (isset($city_info['city_id'])) $city = Lang($city_info['city_name'], 2);
            $themeData['user_info_city'] = $city;

        }
    }
    if (!empty($uinfo['lat']) && !empty($uinfo['lon'])) {
        $themeData['map_url'] = 'https://www.google.com/maps/?q=' . $uinfo['lat'] . ',' . $uinfo['lon'];
        $themeData['user_info_location'] = '<div class="text-center">' . \iCms\UI::view('backend/orders/view-map-btn') . '</div><br>';
    }
    $files = $UserDObj->getFiles($itemid);
    if ($files) {
        $i = 0;
        $mediaObj = new \iCms\Media();
        $themeData['user_info_files'] = '<hr><div class="row"><div class="col-sm-12 text-center bold">' . Lang('required_documents') . '</div>';
        foreach ($files as $file) {
            $fileGet = $mediaObj->getById($file['file_id']);
            if (isset($fileGet['each'][0]['id'])) {
                $i++;
                $fileInfo = $fileGet['each'][0];
                $themeData['user_info_files'] .= '<div class="col-sm-12"><a href="' . $fileInfo['complete_url'] . '" target="_blank">' . $i . ' - ' . $fileInfo['name'] . '</a></div>';
            }
        }
        $themeData['user_info_files'] .= '</div>';
    }
    $themeData['user_info_more'] = '';
    if (!empty($uinfo['gender'])) {
        $themeData['user_info_more'] .= Lang('gender_label') . ' : <span dir="ltr">' . Lang($uinfo['gender'] . '_label') . '</span><hr>';
    }
    if (!empty($uinfo['birthday'])) {
        $themeData['user_info_more'] .= Lang('birthday_label') . ' : <span dir="ltr">' . $uinfo['birthday'] . '</span><hr>';
    }
    if (!empty($uinfo['qualification'])) {
        $themeData['user_info_more'] .= Lang('qualification_label') . ' : ' . $uinfo['qualification'] . '<hr>';
    }
    if (!empty($uinfo['street'])) {
        $themeData['user_info_more'] .= Lang('street_label') . ' ' . $uinfo['street'] . ', ';
    }
    if (!empty($uinfo['building'])) {
        $themeData['user_info_more'] .= Lang('building_label') . ' ' . $uinfo['building'] . ', ';
    }
    if (!empty($uinfo['floor'])) {
        $themeData['user_info_more'] .= Lang('floor_label') . ' ' . $uinfo['floor'] . ', ';
    }
    if (!empty($uinfo['apartment'])) {
        $themeData['user_info_more'] .= Lang('apartment_label') . ' ' . $uinfo['apartment'];
    }
    if (!empty($uinfo['additional'])) {
        $themeData['user_info_more'] .= ', ' . Lang('additional_label') . ' ' . $uinfo['additional'] . '.<hr>';
    } else if (!empty($uinfo['street'])) $themeData['user_info_more'] .= '.<hr>';

    if (!empty($uinfo['nid'])) {
        $themeData['user_info_more'] .= Lang('personal_identity') . ' : <span dir="ltr">' . $uinfo['nid'] . '</span><hr>';
    }
    if (!empty($uinfo['driving_license'])) {
        $themeData['user_info_more'] .= Lang('driving_license') . ' : <span dir="ltr">' . $uinfo['driving_license'] . '</span><hr>';
    }
    if (!empty($uinfo['vehicle_license'])) {
        $themeData['user_info_more'] .= Lang('vehicle_license') . ' : <span dir="ltr">' . $uinfo['vehicle_license'] . '</span><hr>';
    }
    if ($uinfo['active'] == 0) {
        $actvst = '<span class="label label-warning">' . Lang('suspend_user') . '</span>';
    } else {
        $actvst = '<span class="label label-success">' . Lang('active_user') . '</span>';
    }
    $themeData['user_info_state'] = $actvst;
    $html = \iCms\UI::view('backend/users/view-content');
    $data = array(
        'status' => 200,
        'html' => $html
    );
}