<?php
$themeData['footer_scripts_vn'] = \iCms\UI::view('backend/global/footer/scripts-vn');
$themeData['theme_js'] = $sellerLogged ? 'seller' : 'main';
$themeData['footer_scripts_core'] = \iCms\UI::view('backend/global/footer/scripts-core');
$themeData['footer_scripts_p'] = '';
$themeData['footer_jquery_p'] = '';
$themeData['footer_scripts_bc'] = '';
$themeData['footer_scripts_core'] .= ($isLogged) ? '' : '';
$themeData['footer_tags'] = '';
$languages = getLanguages();
$languagesHtml = (!empty($languages['langhtml'])) ? implode(' - ', $languages['langhtml']) : '';
$themeData['languages'] = $languagesHtml;