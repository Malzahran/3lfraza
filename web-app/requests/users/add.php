<?php
$data = array();
if (!empty($_POST['name']) && !empty($_POST['email']) && !empty($_POST['username']) && !empty($_POST['password'])) {
    $done = false;
    $type = (int)$_POST['accounttype'];
    $s_type = 0;
    switch ($type) {
        case 1:
            $utype = 'moderator';
            break;
        case 2:
            $utype = 'user';
            break;
        case 3:
            $utype = 'agent';
            break;
        case 4:
            $utype = 'delivery';
            break;
        case 5:
            $utype = 'worker';
            break;
        case 8:
            $s_type = 1;
            $utype = 'seller';
            break;
        case 9:
            $s_type = 2;
            $utype = 'seller';
            break;
        case 10:
            $s_type = 3;
            $utype = 'seller';
            break;
        case 1601:
            $utype = 'admin';
            break;
    }
    $smartPH = (isset($_POST['smartph']) && (int)$_POST['smartph'] == 1) ? 1 : 0;
    $usersObj->setAuth('web');
    $usersObj->setUsertype($utype);
    $usersObj->setName($_POST['name']);
    $usersObj->setEmail($_POST['email']);
    if ($sellerLogged) {
        $usersObj->setStore($user['store_id']);
        if ($s_type) $usersObj->setStoreAdmin($s_type);
        if ($utype == 'delivery') $usersObj->setDeliveryType(1);
    }
    if (!empty($_POST['phone'])) {
        $usersObj->setPhone($_POST['phone']);
    }
    if (!empty($_POST['nid'])) {
        $usersObj->setNationalID($_POST['nid']);
    }
    $usersObj->setUsername($_POST['username']);
    $usersObj->setPassword($_POST['password']);
    $register = $usersObj->register();
    if ($register) {
        if (isset($_FILES['avatar']['tmp_name'])) {
            $image = $_FILES['avatar'];
            $avatar = registerMedia($image);
            if (isset($avatar['id'])) {
                $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET avatar_id=" . $avatar['id'] . " WHERE id=" . $register);
            }
        }
        if ($type == 1) {
            $usersObj->setUserId($register);
            $usersObj->setPerm($_POST['perm']);
            $addperm = $usersObj->registerPerm();
            if ($addperm) {
                $done = true;
            }
        } else {
            $done = true;
        }
    }
    if ($done) {
        $data = array(
            'status' => 200
        );
    }
}