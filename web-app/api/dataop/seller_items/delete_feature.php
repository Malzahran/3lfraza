<?php
$itemID = (int)$_POST['item_id'];
if ($itemID != 0 && isset($_POST['confirm']) && (int)$_POST['confirm'] == 1) {
	$itemInfo = $utiObj->getItemFeatures(0, $itemID);
	if (isset($itemInfo['id'])) {
		$itemObj = new \iCmsSeller\Item();
		$itemObj->setId($itemID);
		if ($itemObj->deleteFeature()) {
			$response['message'] = Lang('success_done_msg');
			$response['error'] = false;
		}
	}
}