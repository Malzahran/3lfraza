<?php
$itemID = (int)$_POST['item_id'];
if ($itemID != 0) {
    if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
    if (empty($_POST['desc']['en'])) $_POST['desc']['en'] = $_POST['desc']['ar'];
    $itemInfo = $utiObj->getItem($itemID);
    $activeSET = $itemInfo['active'];
    $itemObj = new \iCmsSeller\Item();
    $itemObj->setId($itemID);
    $editItem = $itemObj->editItem($_POST, $activeSET);
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
                $query = $conn->query("UPDATE " . DB_STORE_ITEMS . " SET featured_image=" . $media['id'] . " WHERE id=" . $itemID);
                if ($query && $itemInfo['featured_image'] != 0) {
                    $delObj = new \iCms\DeleteMedia;
                    $delMedia = $delObj->deleteMedia($itemInfo['featured_image']);
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