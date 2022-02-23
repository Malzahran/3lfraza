<?php
$themeData['footer_scripts_vn'] = \iCms\UI::view('global/footer/scripts-vn');
$themeData['footer_scripts_core'] = \iCms\UI::view('global/footer/scripts-core');
$themeData['footer_scripts_p'] = '';
$themeData['footer_scripts_bc'] = '';
$themeData['reset_pass_url'] = smoothLink('index.php?tab1=password_reset');
$themeData['privacy_url'] = smoothLink('index.php?tab1=privacy-policy');
$themeData['terms_url'] = smoothLink('index.php?tab1=terms-and-conditions');
$themeData['footer_tags'] = \iCms\UI::view('global/footer/all');