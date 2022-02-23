<?php
if (!$sellerLogged && (!@$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('delivery_agent_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('delivery_tracking_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('delivery_agent_label');
$themeData['footer_jquery_bc'] = '
<script type="text/javascript">
$("#delv_trk_sp").addClass( "active" );
</script>';
$themeData['page_modals'] = '';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['page_content'] = \iCms\UI::view('backend/delivery-tracking/content');
    $themeData['footer_jquery_p'] .= '
	<script src="https://maps.googleapis.com/maps/api/js?v=3&amp;key=' . $mapsKey . '&language=' . $L_CODE . '"></script>';
    $themeData['footer_jquery_p'] .= '
	<script src="' . $config['theme_url'] . '/backend/jquery/delivery-tracking/maps.js"></script>';
}