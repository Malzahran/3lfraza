<?php
if (!$sellerLogged || !(@$seller['system'] || @$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('clients_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('clients_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('clients_label');
$themeData['footer_jquery_bc'] = '
<script type="text/javascript">
$("#clients_sp").addClass( "active" );
</script>';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_modals'] = \iCms\UI::view('backend/seller/users/view-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/activate-modal');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/clients/view.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/clients/suspend.js"></script>';
    $themeData['page_content'] = \iCms\UI::view('backend/clients/content');
}