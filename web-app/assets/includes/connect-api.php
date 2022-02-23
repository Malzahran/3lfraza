<?php
$config = array();
$confQuery = $conn->query("SELECT * FROM " . DB_CONFIGURATIONS);
$config = $confQuery->fetch_array(MYSQLI_ASSOC);
$config['site_url'] = $site_url;
$config['theme_url'] = $site_url . '/themes/' . $config['theme'];
foreach ($config as $cnm => $cfg) {
    define(strtoupper($cnm), $cfg);
    $themeData['config_' . $cnm] = $cfg;
}
// Login verification and user stats update
$user = null;
$shiftID = 0;
$isLogged = false;
$adminLogged = false;
$sellerLogged = false;
$workerLogged = false;
$deliveryLogged = false;
$admin = array();
$seller = array();
$aFeatures = $aCATS = $aINV = $aDLV = false;
$userTypes = array('user', 'admin', 'moderator', 'seller', 'delivery', 'agent', 'worker');
$logged = isApiLogged($username, $password);
if (isApiLogged($username, $password)) {
    $loginObj = new \iCmsAPI\LoginControl();
    $userObj = new \iCms\User();
    $user = $userObj->getById($logged);
    if (isset($user['id']) && in_array($user['type'], $userTypes)) {
        $admin = $userObj->isSysAdmin();
        $isLogged = true;
        $city = $user['current_city'];
        if ($user['type'] == "admin" || $user['type'] == "moderator") {
            $adminLogged = true;
        }
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
        if ($user['type'] == "delivery") {
            $deliveryLogged = true;
        }
    }
}

// Removes session and unnecessary variables if user verification fails
if (!$isLogged) unset($user);