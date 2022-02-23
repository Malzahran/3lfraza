<?php

if (!$sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}

$_SESSION[$config['spf'] . 'seller_profile'] = true;
$themeData['page_header_title'] = Lang('my_profile_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('my_profile_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('my_profile_label');
$themeData['page_content'] = \iCms\UI::view('backend/profile/content');
$themeData['footer_scripts_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
$themeData['footer_jquery_p'] = '<script src="' . $config['theme_url'] . '/js/myprofile.js"></script>';