<?php
if (!$sellerLogged && (!@$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('workers_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('workers_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('workers_label');
$themeData['footer_jquery_bc'] = '
<script type="text/javascript">
$("#wrk_sp").addClass( "active" );
</script>';
$themeData['page_modals'] = null;
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_content'] = \iCms\UI::view('backend/worker/content');
    $themeData['page_modals'] = \iCms\UI::view('backend/users/view-modal');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/select2/select2.min.css" rel="stylesheet">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>';
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/worker/view.js"></script>';
    $themeData['footer_scripts_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/worker/add-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/add.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/users/edit-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/edit.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/activate-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/active.js"></script>';
}
if (@$seller['system']) {
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/delete-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/del.js"></script>';
}
