<?php
$themeData['site_title'] .= ' - ' . Lang('privacy_label');
$themeData['page_bread'] = '<li class="active">' . Lang('privacy_label') . '</li>';
$themeData['lang_url'] = $config['site_url'] . '/' . $CH_LANG . '/privacy-policy';
$themeData['page_title'] = Lang('privacy_label');
$privacyFetch = $utiObj->getMiscInfo('privacy_policy');
$themeData['more_content'] = !empty($privacyFetch['content']) ? \iCms\UI::html($privacyFetch['content']) : '';
$themeData['page_content'] = \iCms\UI::view('more/content');