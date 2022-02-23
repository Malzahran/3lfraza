<?php
$itemID = (int)$_POST['item_id'];
if ($itemID != 0) {
    if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
    $itemInfo = $utiObj->getItemFeatures(0 ,$itemID);
	if (isset($itemInfo['id'])) {
		$itemObj = new \iCmsSeller\Item();
		$itemObj->setId($itemID);
		$editItem = $itemObj->editFeature($_POST);
		if ($editItem) {
			$response['message'] = Lang('success_done_msg');
			$response['error'] = false;
		}
	}
}