<?php
$items = array();
$misc = $data->misc;
$totalcount = $misc->totalcount;
$page = (int)$misc->page;
$dataget = false;
if ($sellerLogged) $msgrecipients = $msgObj->getStoreMessageRecipient();
else $msgrecipients = $msgObj->getMessageRecipients();
if (is_array($msgrecipients) && count($msgrecipients) > 0) {
    if (($totalcount == 0) || (count($msgrecipients) > $totalcount && $totalcount != 0)) {
        $dataget = true;
        $totalcount = count($msgrecipients);
        foreach ($msgrecipients as $eachRecipient) {
            $messages = 0;
            $desc = null;
            $id = $eachRecipient['id'];
            $name = $eachRecipient['name'];
            $thumb = $eachRecipient['thumbnail_url'];
            if ($sellerLogged) {
                $lastmsg = $msgObj->getStoreMessages(
                    array(
                        'recipient_id' => $id,
                        'user_id' => $user['store_id']
                    )
                );
            } else {
                $lastmsg = $msgObj->getMessages(
                    array(
                        'recipient_id' => $id,
                        'user_id' => $user['id']
                    )
                );
            }
            if (is_array($lastmsg)) {
                $desc = $lastmsg[count($lastmsg) - 1]['text'];
                $time = date('Y/m/d h:i', $lastmsg[count($lastmsg) - 1]['time']);
            }
            $ctype = 1;
            if (isset($eachRecipient['ctype'])) $ctype = $eachRecipient['ctype'];
            if ($sellerLogged) $ctype = 2;
            $items[] = array(
                'title' => $name,
                'text' => $desc,
                'time' => $time,
                'ctype' => $ctype,
                'id' => $id,
                'image_url' => $thumb);
        }
    }
}

$layout = array(
    'layout' => 1,
    'barback' => 1,
    'click' => 1,
    'bartitle' => 1,
    'abtitle' => Lang('messages_label'));
if ($dataget || $totalcount == 0) $response["result"] = "success";
else if (!$dataget && $totalcount != 0) $response["result"] = "failure";
$response["totalcount"] = $totalcount;
$response["messages"] = $items;
$response["layout"] = $layout;