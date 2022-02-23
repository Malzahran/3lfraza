<?php
sellerOnly();
if (!empty($_POST['title'])) {
    if (empty($_POST['title']['en'])) {
        $_POST['title']['en'] = $_POST['title']['ar'];
    }
    if ($register = $catObj->registerCategory($_POST)) {
        $catID = $register;
        if (isset($_FILES['fimage']['tmp_name'])) {
            $image = $_FILES['fimage'];
            $fimage = registerMedia($image);
            if (isset($fimage['id'])) {
                $query = $conn->query("UPDATE " . DB_STORE_CATS . " SET image_id=" . $fimage['id'] . " WHERE category_id=" . $catID);
            }
        }
        $data = array(
            'status' => 200
        );

    }
}