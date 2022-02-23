<?php
$mediaObj = new \iCms\Media();
$themeData['header_metatags'] = \iCms\UI::view('global/header/metatags');
$themeData['header_title_start'] = '<title>';
$themeData['site_title'] = Lang('system_title');
$themeData['header_title_end'] = '</title>';
$themeData['meta_desc'] = Lang('meta_desc');
$themeData['meta_keys'] = Lang('meta_keys');
$themeData['header_favicon'] = \iCms\UI::view('global/header/favicon');
$themeData['header_stylesheets'] = \iCms\UI::view('global/header/stylesheets');
$themeData['header_stylesheets'] .= (Lang('lang_dir') == "rtl") ? '<link rel="stylesheet" href="' . $config['theme_url'] . '/frontend/assets/css/style-rtl.css" type="text/css">' : '';
$themeData['header_scripts'] = \iCms\UI::view('global/header/scripts');
$themeData['header_scripts'] .= ($config['smooth_links'] == 1) ? '
<script>
function reqSource(){return \'' . $config['site_url'] . '/ajax/' . '\';}
function themesource(){return \'' . $config['theme_url'] . '\';}
</script>' : '
<script>
function reqSource(){return \'' . $config['site_url'] . '/request.php\';}
function themesource(){return \'' . $config['theme_url'] . '\';}
</script>';
$themeData['header_tags'] = \iCms\UI::view('global/header/all');
$themeData['lang_name'] = (Lang('lang_code') == 'ar') ? 'English' : 'عربي';
$themeData['home_url'] = smoothLink('index.php?tab1=home');