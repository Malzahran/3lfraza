<?php
$items = array();
$misc = $data->misc;
$sender = $user['id'];
$recipient_id = intval($misc->itemid);
$page = intval($misc->page);
$before = (isset($misc->firstid) && $misc->firstid != 0) ? intval($misc->firstid) : null;
$after = (isset($misc->lastid) && $misc->lastid != 0) ? intval($misc->lastid) : null;
$userID = ($sellerLogged) ? $user['store_id'] : $user['id'];
$convarray = array(
    'recipient_id' => $recipient_id,
    'user_id' => $userID,
    'before_message_id' => $before,
    'after_message_id' => $after
);
$getconv = ($sellerLogged) ? $msgObj->getStoreMessages($convarray) : $msgObj->getMessages($convarray);
$count = count($getconv);
if (is_array($getconv) && $count > 0) {
    $narray = array_reverse($getconv);
    $after = $narray[$count - 1]['id'];
    $before = $narray[0]['id'];
    foreach ($narray as $msg) {
        $desc = $msg['text'];
        $time = date('Y/m/d h:i', $msg['time']);
        $name = $msg['account']['name'];
        if ($msg['owner'] == true) {
            $type = 2;
            $name = null;
            $thumb = ($sellerLogged) ? $msg['account']['thumbnail_url'] : $user['thumbnail_url'];
        } else {
            $type = 1;
            $thumb = $msg['account']['thumbnail_url'];
        }
        $items[] = array(
            'title' => $name,
            'text' => $desc,
            'time' => $time,
            'id' => $msg['id'],
            'type' => $type,
            'image_url' => $thumb);
    }
}
$layout = array('layout' => 1, 'coloumn' => 2, 'cimg' => 1, 'click' => 1);
$intentdata = array('intent' => 2, 'reqtype' => 'students', 'reqstype' => 'get', 'reqptype' => 'other');
$response["result"] = "success";
$response["messages"] = $items;
$response["layout"] = $layout;
$response["firstid"] = $after;
$response["lastid"] = $before;
$response["layout"]["intentData"] = $intentdata;