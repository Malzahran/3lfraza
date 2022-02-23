<?php

switch ($_GET['tab1']) {
    // Email verification source
    case 'email-verification':
        include('sources/email_verification.php');
        break;

    // Home page source
    case 'home':
        include('sources/home.php');
        break;
    // Login source
    case 'password_reset':
        include('sources/passReset.php');
        break;
    // Sellers page source
    case 'admin':
        include('sources/seller/seller.php');
        break;
    // Terms page source
    case 'terms-and-conditions':
        include('sources/terms.php');
        break;

    // Privacy page source
    case 'privacy-policy':
        include('sources/privacy.php');
        break;
}

// If no sources found
if (empty($themeData['page_content']) && in_array($_GET['tab1'], $backendTab)) {
    $themeData['page_content'] = \iCms\UI::view('backend/global/error');
} else if (empty($themeData['page_content']) && !in_array($_GET['tab1'], $backendTab)) {
    $themeData['site_title'] .= ' - ' . Lang('error_menu');
    $themeData['page_content'] = \iCms\UI::view('global/error');
}