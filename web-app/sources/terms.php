<?php
$themeData['site_title'] .= ' - ' . Lang('terms_label');
$themeData['page_bread'] = '<li class="active">' . Lang('terms_label') . '</li>';
$themeData['lang_url'] = $config['site_url'] . '/' . $CH_LANG . '/terms-and-conditions';
$themeData['page_title'] = Lang('terms_label');
$termsFetch = $utiObj->getMiscInfo('terms');
$themeData['more_content'] = !empty($termsFetch['content']) ? \iCms\UI::html($termsFetch['content']) : '';
$themeData['page_content'] = \iCms\UI::view('more/content');