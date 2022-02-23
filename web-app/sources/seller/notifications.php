<?php

if (!$sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('notification_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('notification_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('notification_label');
$ncount = $notfObj->getNotifications(0, true, false, true, false);
$themeData['ntf_head'] = ($ncount['count'] > 0) ? Lang('new_notification_label') : Lang('no_new_notification_label');
$themeData['notification'] = $ncount['tmpl'];
$themeData['page_content'] = \iCms\UI::view('backend/notification/content');