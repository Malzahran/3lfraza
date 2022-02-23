<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $itemInfo = $utiObj->getNews($itemID);
    $activeSET = 0;
    if (isset($_POST['active_state']) && $_POST['active_state'] == 1) $activeSET = 1;
    $itemObj->setId($itemID);
    $editItem = $itemObj->editItem($_POST, $activeSET);
    if ($editItem) {
        if (!empty($_FILES['fimage']['tmp_name'])) {
            $image = $_FILES['fimage'];
            $registerMedia = registerMedia($image);
            if (isset($registerMedia['id'])) {
                $query = $conn->query("UPDATE " . DB_NEWS_ITEMS . " SET featured_image=" . $registerMedia['id'] . " WHERE id=" . $editItem);
                if ($itemInfo['featured_image'] != 0) {
                    $delObj = new \iCms\DeleteMedia;
                    $delMedia = $delObj->deleteMedia($itemInfo['featured_image']);
                    if ($delMedia) {
                        $done = true;
                    }
                } else {
                    $done = true;
                }
            }
        } else {
            $done = true;
        }

        if ($done) {
            $data = array(
                'status' => 200,
            );
        }
    }
}