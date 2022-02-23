<?php

if (!isset($_GET['tab1'])) $_GET['tab1'] = 'home';
$backendTab = array('admin');
require_once('assets/includes/core.php');
$CH_LANG = ($L_CODE == 'ar') ? 'en' : 'ar';
ini_set('display_errors', 'On');
error_reporting(E_ALL);
foreach ($_GET as $key => $value) {
    $themeData['get_' . $escapeObj->stringEscape(strtolower($key))] = $escapeObj->stringEscape($value);
}
if (isset($_GET['tab1']) && in_array($_GET['tab1'], $backendTab)) {
    require_once('index/backend/header_tags.php');
    require_once('index/backend/footer_tags.php');
    require_once('index/backend/sidebar.php');
    require_once('pages.php');
    require_once('index/backend/header.php');
    require_once('index/backend/footer.php');
} else {
    require_once('index/frontend/header_tags.php');
    require_once('index/frontend/footer_tags.php');
    require_once('pages.php');
    require_once('index/frontend/header.php');
    require_once('index/frontend/footer.php');
}
if (isset($_GET['tab1']) && in_array($_GET['tab1'], $backendTab) && isset($_GET['tab2']) && $_GET['tab2'] == "login")
    echo \iCms\UI::view('backend/mainframe-login');
else if (isset($_GET['tab1']) && in_array($_GET['tab1'], $backendTab)) echo \iCms\UI::view('backend/mainframe');
else echo \iCms\UI::view('mainframe');
$conn->close();
$db = null;