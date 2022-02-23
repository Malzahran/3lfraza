<?php
$data = array();
if (!empty($_POST['notf']['group']) && !empty($_POST['notf']['title']) && !empty($_POST['notf']['msg']) && !empty($_POST['notf']['type'])) {
    if ($_POST['notf']['group'] != 'all') {
        $group = (int)$_POST['notf']['group'];
        $fcmObj->setCityid($group);
    } else {
        $group = 'all';
        $fcmObj->setCityid(8000);
    }
    $totalcount = $fcmObj->getTokens(array('fgroup' => $group, 'count' => 1));
    if ($totalcount != 0) {
        $tokens = $fcmObj->getTokens(array('fgroup' => $group));
        if (!empty($tokens['data'])) {
            $fcmObj->setNTitle($_POST['notf']['title']);
            $fcmObj->setNMsg($_POST['notf']['msg']);
            $fcmObj->setNType($_POST['notf']['type']);
            $fcmObj->setNAction($_POST['notf']['action']);
            $fcmObj->setSenderid($user['id']);
            if (sizeof($tokens['data']) > 1000) {
                $newId = array_chunk($tokens['data'], 1000);
                foreach ($newId as $inner_id) {
                    $fcmObj->setTCount(count($inner_id));
                    $fcmObj->setTokens($inner_id);
                    $send = $fcmObj->SendNotification();
                }
            } else {
                $fcmObj->setTCount(count($tokens['data']));
                $fcmObj->setTokens($tokens['data']);
                $send = $fcmObj->SendNotification();
            }
        }
    }

    if ($send) {
        $data = array(
            'status' => 200
        );
    }
}