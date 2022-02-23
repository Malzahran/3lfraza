<?php
$itemID = (int)$data->misc->itemid;
$UserDObj = new \iCms\User();
$mediaObj = new \iCms\Media();
$itemInfo = $UserDObj->getById($itemID);
if (isset($itemInfo['id'])) {
    $files_download = array();
    $item = array();
    $date = new \DateTime();
    $date->setTimestamp($itemInfo['time']);
    $item['time'] = $date->format('Y-m-d h:i a');
    
    if ($itemInfo['app_lastlogged']) {
        $date->setTimestamp($itemInfo['app_lastlogged']);
        $item['state'] = Lang('last_seen_app') . ': ' . $date->format('Y-m-d h:i a');
    }
    $item['title'] = $itemInfo['name'];
    $active = $itemInfo['active'] ? 1 : 2;
    $item['title2'] = $active == 1 ? Lang('active_label') : Lang('unactive_label');
    $files = $UserDObj->getFiles($itemID);
    if ($itemInfo['avatar_id']) $item['image_url'] = $itemInfo['avatar_url'];
    $item['desc'] = '';
    $item['desc'] .= '<b><font color="red">' . Lang('address_label') . '</font></b>
    <br>
    <b>' . Lang('street_label') . $itemInfo['street'] . '<br>' . Lang('building_label') . $itemInfo['building'] . '<br>' . Lang('floor_label') . $itemInfo['floor'] . '<br>' . Lang('apartment_label') . $itemInfo['apartment'] . (!empty($itemInfo['additional']) ? '<br>' . Lang('additional_label') . $itemInfo['additional'] : '') . '</b><br>';
    
    $item['desc'] .= !empty($itemInfo['nid']) ? '<br><b><font color="#4db849">' . Lang('personal_identity') . ':</font></b> ' . $itemInfo['nid'] : '';
    $item['desc'] .= !empty($itemInfo['driving_license']) ? '<br><b><font color="#4db849">' . Lang('driving_license') . ':</font></b> ' . $itemInfo['driving_license'] : '';
    $item['desc'] .= !empty($itemInfo['vehicle_license']) ? '<br><b><font color="#4db849">' . Lang('vehicle_license') . ':</font></b> ' . $itemInfo['vehicle_license'] : '';
    $item['desc'] .= '<br>';
    
    $item['desc'] .= '<br><b><font color="#4db849">' . Lang('birthday_label') . ':</font></b> <span dir="ltr">' . $itemInfo['birthday'] . '</span>';
    
    $item['desc'] .= '<br><b><font color="#4db849">' . Lang('gender_label') . ':</font></b> ' . Lang($itemInfo['gender'] . '_label');
    
    if ($itemInfo['lat'] != 0 && $itemInfo['lon'] != 0) {
        $location = array();
        $location[] = array('lat' => $itemInfo['lat'],
            'longt' => $itemInfo['lon'], 'title' => $itemInfo['name']);
        $item['location'] = $location;
    }
    if ($files) {
        foreach ($files as $file) {
            $fileGet = $mediaObj->getById($file['file_id']);
            if (isset($fileGet['each'][0]['id'])) {
                $fileInfo = $fileGet['each'][0];
                $files_download[] = array('fname' => $fileInfo['name'], 'furl' => $fileInfo['complete_url'], 'fext' => $fileInfo['extension'], 'type' => $fileInfo['type'] == 'photo' ? 1 : 2);
            }
        }
    }
    $item['minfo'] = Lang('username_label') . ': ' . $itemInfo['username'];
    $item['minfo2'] = Lang('email_label') . ': ' . $itemInfo['email'];

    if (!empty($itemInfo['phone'])) {
        $item['contact_title'] = Lang('contact_delivery_label');
        $item['phone'] = $item['sms'] = $itemInfo['phone'];
    }
    $item['files'] = $files_download;

    $layout = array(
        'barback' => 1,
        'orientation' => 0,
        'bartitle' => 1,
        'abtitle' => Lang('view_info_btn')
    );
    $response["result"] = "success";
    $response["item"] = $item;
    $response["layout"] = $layout;
}