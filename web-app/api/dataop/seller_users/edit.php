<?php
$itemID = (int)$_POST['item_id'];
if ($itemID != 0 && isset($_POST['u_name']) && isset($_POST['u_phone'])) {
    $uType = (int)$_POST['ac_type'];
    $userDObj = new \iCms\User();
    $itemInfo = $userDObj->getById($itemID);
    $activeSET = $itemInfo['active'];
    $usersObj = new \iCms\Users();
    $usersObj->setUserId($itemID);
    $usersObj->setName($_POST['u_name']);
    $usersObj->setPhone($_POST['u_phone']);
    $usersObj->setUsername($itemInfo['username']);
    $usersObj->setEmail($itemInfo['email']);
    $usersObj->setStoreAdmin($uType);
    $editItem = $usersObj->edit($activeSET);
    if ($editItem) {
        if (isset($_FILES['photos']['name'])) {
            $photos = $_FILES['photos'];
            $count = count($photos['name']);
            if ($count == 1) {
                $params = array(
                    'tmp_name' => $photos['tmp_name'][0],
                    'name' => $photos['name'][0],
                    'size' => $photos['size'][0]
                );
                $media = registerMedia($params);
            }
            if (isset($media['id'])) {
                $query = $conn->query("UPDATE " . DB_ACCOUNTS . " SET avatar_id=" . $media['id'] . " WHERE id=" . $itemID);
                if ($query && $itemInfo['avatar_id'] != 0) {
                    $delObj = new \iCms\DeleteMedia;
                    $delMedia = $delObj->deleteMedia($itemInfo['avatar_id']);
                    if ($delMedia) $done = true;
                } else $done = true;
            }
        } else $done = true;
        if ($done) {
            $response['message'] = Lang('success_done_msg');
            $response['error'] = false;
        }
    }
}