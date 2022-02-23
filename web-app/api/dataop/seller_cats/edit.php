<?php
$itemID = (int)$_POST['item_id'];
if ($itemID != 0) {
    if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
    $itemInfo = $utiObj->getStoreCats($itemID);
    $catObj = new \iCmsSeller\Category();
    $catObj->setId($itemID);
    $editItem = $catObj->editCategory($_POST);
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
                $query = $conn->query("UPDATE " . DB_STORE_CATS . " SET image_id=" . $media['id'] . " WHERE category_id=" . $itemID);
                if ($query && $itemInfo['image_id'] != 0) {
                    $delObj = new \iCms\DeleteMedia;
                    $delMedia = $delObj->deleteMedia($itemInfo['image_id']);
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