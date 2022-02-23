<?php
if ($sellerLogged) {
    $themeData['footer_jquery_p'] = '<script>
			jQuery(document).ready(function() {
				Main.init();
			});
		</script>';
    $themeData['site_title'] .= ' - ' . Lang('backend_label');
    $themeData['page_header_title'] = Lang('backend_label');
    $themeData['page_breadcrumb'] = '<li><span>' . Lang('backend_label') . '</span></li>';
    $themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
}
if (isset($_GET['tab2']) && $_GET['tab2'] == "login") {
    include('login.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "profile") {
    include('profile.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "notifications") {
    include('notifications.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "messages") {
    include('messages.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "coverage") {
    include('city.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "privacy") {
    include('privacy.php');
}  elseif (isset($_GET['tab2']) && $_GET['tab2'] == "terms") {
    include('terms.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "categories") {
    include('categories.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "news") {
    include('news.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "items") {
    include('items.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "stock-alert") {
    include('stock_alert.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "promo-codes") {
    include('promo.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "location-alert") {
    include('location_alert.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "users") {
    include('users.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "fcm") {
    include('fcm.php');
} elseif (isset($_GET['tab2']) && $_GET['tab2'] == "clients") {
    include('clients.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "worker") {
    include('worker.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "delivery") {
    include('delivery.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "delivery-tracking") {
    include('delivery-tracking.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "reports") {
    include('report.php');
} else if (isset($_GET['tab2']) && $_GET['tab2'] == "logout") {
    include('logout.php');
} else {
    if ($sellerLogged) {
        $themeData['footer_jquery_bc'] = '<script type="text/javascript">
		$("#home_sp").addClass( "active" );
		</script>';
        $themeData['page_modals'] = \iCms\UI::view('backend/orders/view-modal');
        $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
        $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
	<script src="' . $config['theme_url'] . '/backend/vendor/printThis.js"></script>
	<script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
        $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/orders/view.js"></script>
	<script src="' . $config['theme_url'] . '/backend/jquery/orders/delivery.js"></script>
	<script src="' . $config['theme_url'] . '/backend/jquery/orders/worker.js"></script>';
        $themeData['page_modals'] .= \iCms\UI::view('backend/orders/assign-delv-modal');
        $themeData['page_modals'] .= \iCms\UI::view('backend/orders/edit-delv-modal');
        $themeData['page_modals'] .= \iCms\UI::view('backend/orders/assign-wrk-modal');
        $themeData['page_modals'] .= \iCms\UI::view('backend/orders/edit-wrk-modal');
        $themeData['page_modals'] .= \iCms\UI::view('backend/orders/refuse-modal');
        $themeData['page_content'] = \iCms\UI::view('backend/seller/home/content');
    } else if (!$isLogged) {
        header('Location: ' . smoothLink('index.php?tab1=admin&tab2=login'));
    } else if ($isLogged) {
        header('Location: ' . smoothLink('index.php?tab1=home'));
    }
}