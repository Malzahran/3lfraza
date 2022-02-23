<?php

if (!$sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('messages_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('messages_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('messages_label');
$msgObj = new \iCms\Messages();
/* */
if (!empty($_GET['recipient_id'])) {
    $recipientId = (int)$_GET['recipient_id'];
    $recipientObj = new \iCms\User();
    $recipientObj->setId($recipientId);
    $recipient = $recipientObj->getRows();

    $timelineId = $user['id'];
    $timelineObj = new \iCms\User();
    $timelineObj->setId($timelineId);
    $timeline = $timelineObj->getRows();


    $messages = $msgObj->getStoreMessages(
        array(
            'recipient_id' => $recipientId,
            'timeline_id' => $timelineId
        )
    );
    if (is_array($messages)) {
        $messg = '';
        foreach ($messages as $msg) {
            $themeData['list_message_id'] = $msg['id'];
            $themeData['list_message_text'] = $msg['text'];
            $themeData['list_message_name'] = $msg['account']['name'];
            $themeData['list_message_img'] = $msg['account']['thumbnail_url'];
            $themeData['list_message_time'] = date('Y/m/d h:i', $msg['time']);

            if ($msg['owner'] == true) {
                //$themeData['list_message_buttons'] = \iCms\UI::view('backend/messages/list-message-each-buttons');
            }

            $messg .= \iCms\UI::view('backend/messages/list-message-each');
        }
        $themeData['recp_name'] = $recipient['name'];
        $themeData['recp_id'] = $recipient['id'];
        $themeData['recp_msg'] = $messg;
        $themeData['recp_msg_replay'] = \iCms\UI::view('backend/messages/replay-message');

    }
}
$listRecipients = '';
$i = 0;
$current = '';
foreach ($msgObj->getStoreMessageRecipient() as $eachRecipient) {
    $themeData['list_recipient_message_num'] = 0;
    $themeData['list_recipient_id'] = $eachRecipient['id'];
    $themeData['list_recipient_name'] = $eachRecipient['name'];
    $themeData['list_recipient_thumbnail_url'] = $eachRecipient['thumbnail_url'];
    $themeData['list_recipient_online_class'] = '';

    $themeData['list_recipient_message_num'] = 5;

    $listRecipients .= \iCms\UI::view('backend/messages/recipient-list');
    $i++;
}

$themeData['msg_recip'] = $listRecipients;
$themeData['page_content'] = \iCms\UI::view('backend/messages/content');