<?php
if (!$sellerLogged && (!@$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('news_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('news_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('news_label');
$themeData['footer_jquery_bc'] = '<script type="text/javascript">$("#news_sp").addClass( "active" );</script>';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['more_add_modal'] = '';
    $themeData['page_content'] = \iCms\UI::view('backend/seller/news/content');
    $themeData['page_modals'] = \iCms\UI::view('backend/seller/news/view-modal');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/news/view.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/news/add.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/news/edit.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/news/active.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/news/add-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/news/edit-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/activate-modal');
}
if (@$seller['system']) {
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/delete-modal');
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/news/del.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/feat-modal');
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/news/feat.js"></script>';
}