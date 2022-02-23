<?php
$themeData['web_home_url'] = smoothLink('index.php?tab1=home');
if (Lang('lang_code') == 'ar') {
    $themeData['current_lang'] = 'عربي';
} else {
    $themeData['current_lang'] = 'English';
}
if (!empty($_GET['tab2']) && empty($_GET['tab3'])) {
    $lang_url = $config['site_url'] . '/' . $CH_LANG . '/' . $backend_url . '/' . __GET__('tab2');
} else if (!empty($_GET['tab2']) && !empty($_GET['tab3'])) {
    $lang_url = $config['site_url'] . '/' . $CH_LANG . '/' . $backend_url . '/' . __GET__('tab2') . '/' . __GET__('tab3');
} else {
    $lang_url = $config['site_url'] . '/' . $CH_LANG . '/' . $backend_url;
}
$languages = getLanguages();
if (!empty($languages['langdata'])) {
    $themeData['lang_select'] = '';
    foreach ($languages['langdata'] as $k => $v) {
        if ($v['name'] != $themeData['current_lang']) {
            $themeData['lang_select'] .= '<li>';
            $themeData['lang_select'] .= '<a href="' . $lang_url . '" class="menu-toggler">' . $v['name'];
            $themeData['lang_select'] .= '</a></li>';
        }
    }
}
$themeData['logout_url'] = smoothLink('index.php?tab1=' . $backend_url . '&tab2=logout');
$themeData['profile_url'] = smoothLink('index.php?tab1=' . $backend_url . '&tab2=profile');
$themeData['header'] = \iCms\UI::view('backend/header/content');
