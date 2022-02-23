<?php
$data = array();
if (!empty($_POST['userid']) && !empty($_POST['notf']['title']) && !empty($_POST['notf']['msg']) && !empty($_POST['notf']['type'])) {
    $cont = false;
    $fcmObj->setUserid($_POST['userid']);
    $token = $fcmObj->getSingleToken();
    if ($token) {
        $fcmObj->setTokens(array($token));
        $cont = true;
    }
    $send = false;
    if ($cont == true) {
        $fcmObj->setNTitle($_POST['notf']['title']);
        $fcmObj->setNMsg($_POST['notf']['msg']);
        $fcmObj->setNType($_POST['notf']['type']);
        $fcmObj->setNAction($_POST['notf']['action']);
        $fcmObj->setSenderid($user['id']);
        $fcmObj->setTCount(1);
        $send = $fcmObj->SendNotification();
    }
    if ($send) {
        $data = array(
            'status' => 200
        );
    }
}