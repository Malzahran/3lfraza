<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $catInfo = $utiObj->getStoreCats($itemID);
    $catObj->setId($itemID);
    $editCat = $catObj->editCategory($_POST);
    if ($editCat) {
        if (!empty($_FILES['image']['tmp_name'])) {
            $image = $_FILES['image'];
            $registerMedia = registerMedia($image);
            if (isset($registerMedia['id'])) {
                $query = $conn->query("UPDATE " . DB_STORE_CATS . " SET image_id=" . $registerMedia['id'] . " WHERE category_id=" . $editCat);
                if ($catInfo['image_id'] != 0) {
                    $delObj = new \iCms\DeleteMedia;
                    $delMedia = $delObj->deleteMedia($catInfo['image_id']);
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