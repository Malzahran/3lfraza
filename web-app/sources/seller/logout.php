<?php
if (!$sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin&tab2=login'));
}
if (isset($_SESSION[$config['spf'] . 'user_id'])) {
    $conn->query("UPDATE " . DB_ACCOUNTS . " SET last_logout=" . time() . " WHERE id=" . $user['id']);
    unset($_SESSION[$config['spf'] . 'user_id']);
}

if (isset($_SESSION[$config['spf'] . 'user_pass'])) {
    unset($_SESSION[$config['spf'] . 'user_pass']);
}

if (isset($_SESSION[$config['spf'] . 'seller_profile'])) {
    unset($_SESSION[$config['spf'] . 'seller_profile']);
}

setcookie('sk_u_i', 0, time() - 60);
setcookie('sk_u_p', 0, time() - 60);

header('Location: ' . smoothLink('index.php?tab1=admin'));
