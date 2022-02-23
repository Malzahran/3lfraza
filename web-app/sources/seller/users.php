<?php
if (!$sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('users_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('users_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('users_label');
$themeData['footer_jquery_bc'] = '
<script type="text/javascript">
$("#users_sp").addClass( "active" );
</script>';
$themeData['page_modals'] = null;
if (@$seller['system']) {
    $themeData['sel_id'] = 8;
    $themeData['sel_name'] = Lang('sadmin_type_label');
    $themeData['super_admin_sel'] = \iCms\UI::view('backend/global/select/selectopt');
}
if (@$seller['system']) {
    $themeData['sel_id'] = 8;
    $themeData['sel_name'] = Lang('sadmin_type_label');
    $themeData['super_admin_sel'] = \iCms\UI::view('backend/global/select/selectopt');
}
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_content'] = \iCms\UI::view('backend/seller/users/content');
    $themeData['page_modals'] = \iCms\UI::view('backend/seller/users/view-modal');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/select2/select2.min.css" rel="stylesheet">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>';
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/view.js"></script>';
    $themeData['footer_scripts_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/users/add-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/add.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/users/edit-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/edit.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/delete-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/del.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/activate-modal');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/users/active.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/users/send-notification');
    $themeData['footer_jquery_p'] .= '<script src="' . $config['theme_url'] . '/backend/jquery/notifications/send.js"></script>';
}