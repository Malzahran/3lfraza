<?php
if (isset($_POST)) {
    $cont = false;
    $progress = 2;
    $usersObj = new \iCms\Users();
    $userID = $_POST['user_id'];
    $phone = $escapeObj->stringEscape($_POST['phone']);
    $address = $escapeObj->stringEscape($_POST['address']);
    $name = $escapeObj->stringEscape($_POST['name']);
    $usersObj->setAddress($address);
    if (is_numeric($userID)) {
        $userID = (int)$userID;
        $userDObj = new \iCms\User();
        $userFetch = $userDObj->getById($userID);
        if (isset($userFetch['id'])) {
            $usersObj->setUserId($userFetch['id']);
            $usersObj->setName($userFetch['name']);
            $usersObj->setUsername($userFetch['username']);
            $usersObj->setEmail($userFetch['email']);
            if ($usersObj->edit($userFetch['active'])) {
                $cont = true;
            }
        }

    } else {
        $gnusername = genrateUsername();
        if ($gnusername['code'] == 200) {
            $usersObj->setUsertype('user');
            $usersObj->setAuth('web');
            $usersObj->setName($name);
            $usersObj->setUsername($gnusername['username']);
            $usersObj->setEmail($gnusername['username'] . '@connme.net');
            $usersObj->setPassword($phone);
            $usersObj->setPhone($phone);
            if ($userID = $usersObj->register()) {
                $cont = true;
            }
        }
    }
    if ($cont) {
        $content = array();
        $content['order_added_by'] = $user['id'];
        $content['order_type'] = 2;
        $content['order_content'] = $_POST['desc'];
        $content['order_progress'] = $progress;
        $regOrder = $ordObj->registerOrder(array_merge($_POST, $content), $userID);
        if ($regOrder) {
            if (isset($_POST['order_delivery_id'])) {
                $dlvID = (int)$_POST['order_delivery_id'];
                if ($ordObj->setOrdDelivery($regOrder, $dlvID)) {
                    $data['status'] = 200;
                }
            } else {
                $data['status'] = 200;
            }
        }
    }
}