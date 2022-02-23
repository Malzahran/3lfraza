<?php

if (!$sellerLogged || !(@$seller['system'] || @$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('reports_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('orders_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('orders_label');
$themeData['footer_jquery_bc'] .= '
<script type="text/javascript">
$("#rep_ord_sp").addClass( "active" );
</script>';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_modals'] = \iCms\UI::view('backend/report/orders/view-modal-seller');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">
	<link href="' . $config['theme_url'] . '/backend/vendor/datepicker/datepicker3.css" rel="stylesheet">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
	<script src="' . $config['theme_url'] . '/backend/vendor/datepicker/bootstrap-datepicker.js"></script>
	<script src="' . $config['theme_url'] . '/backend/vendor/printThis.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/report/orders/view.js"></script>';
    $datefrom = new DateTime('first day of this month');
    $dateto = new \DateTime();
    $datetimeFormat = 'd-m-Y';
    $themeData['from_date'] = $datefrom->format($datetimeFormat);
    $themeData['to_date'] = $dateto->format($datetimeFormat);
    $themeData['page_content'] = \iCms\UI::view('backend/report/orders/content-seller');
}