<?php
if (!$sellerLogged && !$aCATS && (!@$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('cities_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('cities_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('cities_label');
$themeData['footer_jquery_bc'] = '<script type="text/javascript">$("#cities_sp").addClass( "active" );</script>';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_modals'] = '';
    $themeData['add_new_btn'] = \iCms\UI::view('backend/global/buttons/add-btn');
    $themeData['page_content'] = \iCms\UI::view('backend/seller/cities/content');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['footer_jquery_p'] .= '
	<script src="https://maps.googleapis.com/maps/api/js?v=3&amp;key=' . $mapsKey . '&language=' . $L_CODE . '"></script>';
    $themeData['footer_jquery_p'] .= '
	<script src="' . $config['theme_url'] . '/backend/jquery/seller/cities/maps.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/cities/view.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/cities/add.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/cities/edit.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/cities/add-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/cities/edit-modal');
}
if (@$seller['system']) {
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/delete-modal');
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/cities/del.js"></script>';
}