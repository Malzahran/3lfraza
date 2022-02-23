<?php
sellerOnly();
if (!empty($_POST['title'])) {
    if (empty($_POST['title']['en'])) {
        $_POST['title']['en'] = $_POST['title']['ar'];
    }
    if (empty($_POST['desc']['en'])) {
        $_POST['desc']['en'] = $_POST['desc']['ar'];
    }
    if ($register = $itemObj->registerItem($_POST)) {
        $itemID = $register;
        if (isset($_FILES['fimage']['tmp_name'])) {
            $image = $_FILES['fimage'];
            $fimage = registerMedia($image);
            if (isset($fimage['id'])) {
                $query = $conn->query("UPDATE " . DB_NEWS_ITEMS . " SET featured_image=" . $fimage['id'] . " WHERE id=" . $itemID);
            }
        }
        $data = array(
            'status' => 200
        );

    }
}