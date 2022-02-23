<?php
if (!empty($_POST['title'])) {
    if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
    $catObj = new \iCmsSeller\Category();
    if ($register = $catObj->registerCategory($_POST)) {
        $catID = $register;
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
            if (isset($media['id'])) $query = $conn->query("UPDATE " . DB_STORE_CATS . " SET image_id=" . $media['id'] . " WHERE category_id=" . $catID);
        }
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}