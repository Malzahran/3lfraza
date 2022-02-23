<?php
$misc = $data->misc;
$type = (int)$misc->type;
$msgtext = $escapeObj->stringEscape($misc->msgtext);
$sender = $user['id'];
$userid = (int)$misc->userid;
$disable_notf = false;
if (empty($type)) $disable_notf = true;
$array = array();
$array['user_id'] = (int)$sender;
if ($type == 2) {
    $array['store_id'] = ($sellerLogged) ? $user['store_id'] : $userid;
    $array['recipient_id'] = ($sellerLogged) ? $userid : 0;
} else {
    $type = 1;
    $array['recipient_id'] = (int)$userid;
}
$array['type'] = $type;
$array['disable_notf'] = $disable_notf;
$continue = false;

if (!empty($msgtext)) {
    $array['text'] = $msgtext;
    $nbrText = str_replace("\n", "", trim($msgtext));
    if (!empty($nbrText)) $continue = true;
}
if ($continue == true) {
    $post_id = $msgObj->registerMessage($array);
	if (!empty($post_id)) {
        $response["result"] = "success";
        $response["lastid"] = $post_id;
    }
}