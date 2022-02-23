<?php
$itemID = (int)$_POST['item_id'];
if ($itemID != 0 && !empty($_POST['title'])) {
    if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
    $itemObj = new \iCmsSeller\Item();
    if ($register = $itemObj->registerFeature($_POST)) {
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}