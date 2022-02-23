<?php
$themeData['header_title_start'] = '<title>';
$themeData['site_title'] = Lang('system_title');
$themeData['header_title_end'] = '</title>';
$themeData['header_meta'] = \iCms\UI::view('backend/global/header/metatags');
$themeData['header_favicon'] = \iCms\UI::view('backend/global/header/favicon');
$themeData['header_stylesheet_main'] = \iCms\UI::view('backend/global/header/stylesheets-main');
$themeData['theme_color'] = $sellerLogged ? 1 : 2;
$themeData['header_stylesheet_theme'] = \iCms\UI::view('backend/global/header/stylesheets-theme');
$themeData['header_stylesheet_theme'] .= (Lang('lang_dir') == "rtl") ? '
<link rel="stylesheet" href="' . $config['theme_url'] . '/backend/vendor/bootstrap/css/bootstrap-rtl.min.css"/>
<link rel="stylesheet" href="' . $config['theme_url'] . '/backend/assets/css/rtl.css"/>' : '';
$themeData['header_stylesheet_page'] = '';
$themeData['header_tags'] = '';
$themeData['header_tags'] .= ($config['smooth_links'] == 1) ? '
<script>
function reqSource(){return \'' . $config['site_url'] . '/ajax/' . '\';}
function themesource(){return \'' . $config['theme_url'] . '\';}
</script>' : '
<script>
function reqSource(){return \'' . $config['site_url'] . '/request.php\';}
function themesource(){return \'' . $config['theme_url'] . '\';}
</script>';
$backend_url = $adminLogged ? 'backend' : 'admin';
if ($isLogged) {
    $themeData['notification_url'] = smoothLink('index.php?tab1=' . $backend_url . '&tab2=notifications');
    $notfObj = new \iCms\Notifications();
    $ncount = $notfObj->getNotifications(0, true, true, true, false);
    $themeData['badge_hidden'] = ($ncount['count'] > 0) ? '' : 'hidden';
    $themeData['ntf_head'] = ($ncount['count'] > 0) ? Lang('new_notification_label') : Lang('no_new_notification_label');
    $themeData['notifications'] = $ncount['tmpl'];
}