<?php
if (!$sellerLogged && (!@$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('location_alert_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('location_alert_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('location_alert_label');
$themeData['footer_jquery_bc'] = '<script type="text/javascript">$("#lcalrt_sp").addClass( "active" );</script>';
$themeData['page_modals'] = '';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_content'] = \iCms\UI::view('backend/seller/location_alert/content');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/location_alert/view.js"></script>';
}