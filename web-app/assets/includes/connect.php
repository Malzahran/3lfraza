<?php
$config = array();
$config['spf'] = $session_pfx;
if (!isset($_SESSION[$config['spf'] . 'config'])) {
    $confQuery = $conn->query("SELECT * FROM " . DB_CONFIGURATIONS);
    $config = $confQuery->fetch_array(MYSQLI_ASSOC);
    $config['spf'] = $session_pfx;
    $config['site_url'] = $site_url;
    $config['theme_url'] = $site_url . '/themes/' . $config['theme'];
    $config['script_path'] = str_replace('index.php', '', $_SERVER['PHP_SELF']);
    $config['ajax_path'] = $config['script_path'] . 'request.php';
    $config['page_path'] = $config['script_path'] . 'page.php';
    $_SESSION[$config['spf'] . 'config'] = $config;
} else $config = $_SESSION[$config['spf'] . 'config'];

if (!isset($_SESSION[$config['spf'] . 'language'])) $_SESSION[$config['spf'] . 'language'] = $config['language'];

foreach ($config as $cnm => $cfg) {
    define(strtoupper($cnm), $cfg);
    $themeData['config_' . $cnm] = $cfg;
}

if (!isset($_SESSION[$config['spf'] . 'lang_data'])) {
    $setlang = LangSet($_SESSION[$config['spf'] . 'language'], 1);
    $lang = $setlang;
    $L_CODE = $lang['lang_code'];
} else {
    $lang = $_SESSION[$config['spf'] . 'lang_data'];
    $L_CODE = $_SESSION[$config['spf'] . 'lang_code'];
}

// Login verification and user stats update
$user = '';
$shiftID = 0;
$isLogged = false;
$adminLogged = false;
$sellerLogged = false;
$workerLogged = false;
$admin = array();
$seller = array();
$aFeatures = $aCATS = $aINV = $aDLV = false;
$userTypes = array('user', 'admin', 'moderator', 'seller', 'delivery', 'agent', 'worker');
if (isLogged()) {
    $userObj = new \iCms\User();
    $isLogged = true;
    $userObj->setId($_SESSION[$config['spf'] . 'user_id']);
    $user = $userObj->getRows();
    if (isset($user['id']) && in_array($user['type'], $userTypes)) {
        $admin = $userObj->isSysAdmin();
        $conn->query("UPDATE " . DB_ACCOUNTS . " SET last_logged=" . time() . " WHERE id=" . $user['id']);
        if ($user['type'] == "admin" || $user['type'] == "moderator" || $user['type'] == "delivery") $adminLogged = true;
        if ($user['type'] == "seller") {
            $sellerLogged = $user['seller']['logged'];
            $seller = $user['seller'];
            if ($sellerLogged) {
                $aFeatures = ($seller['store']['features'] == 1) ? true : false;
                $aCATS = ($seller['store']['cats'] == 1) ? true : false;
                $aINV = ($seller['store']['inventory'] == 1) ? true : false;
                $aDLV = ($seller['store']['delivery'] == 1) ? true : false;
            }
        }
        if ($user['type'] == "worker") {
            $workerLogged = true;
        }
        if (!empty($user['language'])) $_SESSION[$config['spf'] . 'language'] = $user['language'];
        foreach ($user as $key => $value) {
            if (!is_array($value)) {
                $key = str_replace('current_city', 'location', $key);
                $themeData['user_' . $key] = $value;
            }
        }
    }
}

// Fetch preferred language
if (!empty($_GET['lang'])) $L_CODE = $_GET['lang'];

if ($L_CODE != $lang['lang_code']) {
    $langSet = __GET__('lang');
    $setLang = LangSet($langSet, 1);
    if ($setLang) {
        if ($isLogged) $conn->query("UPDATE " . DB_ACCOUNTS . " SET language='$langSet' WHERE id=" . $user['id']);
        $lang = $setLang;
        $L_CODE = $lang['lang_code'];
    }
}

// Removes session and unnecessary variables if user verification fails
if (!$isLogged) {
    unset($_SESSION[$config['spf'] . 'user_id']);
    unset($_SESSION[$config['spf'] . 'user_pass']);
    unset($user);
}