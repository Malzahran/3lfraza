<?php
if (!empty($_POST['title']) && (!$aCATS || $aCATS && $_POST['cat'] != 0)) {
    if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
    if (empty($_POST['desc']['en'])) $_POST['desc']['en'] = $_POST['desc']['ar'];
    $itemObj = new \iCmsSeller\Item();
    if ($register = $itemObj->registerItem($_POST)) {
        $itemID = $register;
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
            if (isset($media['id'])) $query = $conn->query("UPDATE " . DB_STORE_ITEMS . " SET featured_image=" . $media['id'] . " WHERE id=" . $itemID);
        }
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}