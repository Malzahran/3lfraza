<?php
$themeData['site_title'] = Lang('reset_password_label') . ' | ' . Lang('system_name');
if (isset($_GET['tab2']) && !empty($_GET['tab2'])) {
    $resetPattern = $escapeObj->stringEscape($_GET['tab2']);
    if (isValidPasswordResetToken($resetPattern)) {
        $themeData['lang_url'] = $config['site_url'] . '/' . $CH_LANG . '/password_reset/' . $resetPattern;
        $themeData['reset_token'] = $resetPattern;
        $themeData['page_content'] = \iCms\UI::view('reset-pass/reset-pass');
        $themeData['footer_scripts_p'] .= '
                <script src="' . $config['theme_url'] . '/js/reset-password.js"></script>';
    }
} else {
    $themeData['lang_url'] = $config['site_url'] . '/' . $CH_LANG . '/password_reset';
    $themeData['page_content'] = \iCms\UI::view('reset-pass/reset-email');
    $themeData['footer_scripts_p'] .= '
            <script src="' . $config['theme_url'] . '/js/reset-email.js"></script>';
}