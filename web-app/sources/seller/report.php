<?php
if (!$sellerLogged || !(@$seller['system'] || @$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['footer_jquery_bc'] = '
<script type="text/javascript">
$("#reports_main_sp").addClass( "active open" );
</script>';
if (isset($_GET['tab3']) && $_GET['tab3'] == "orders") {
    include('report/orders.php');
}