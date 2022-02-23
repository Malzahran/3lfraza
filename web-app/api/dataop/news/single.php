<?php
$item = array();
$itemid = (int)$data->misc->item_id;
$itemInfo = $utiObj->getNews($itemid);
if (isset($itemInfo['id'])) {
    $itemData = $utiObj->getNewsInfo($itemid);
    $itemimg = $site_url . '/media/logo.png';
    if ($itemInfo['featured_image'] != 0) {
        $mediaObj = new \iCms\Media();
        $imgFetch = $mediaObj->getById($itemInfo['featured_image']);
        $itemimg = $imgFetch['each'][0]['rec_url'];
        $itemFullImg = $imgFetch['each'][0]['rec_url'];
    }
    $date = new \DateTime();
    $date->setTimestamp($itemInfo['time']);

    $minfo = null;
    $item = array(
        'title' => $itemData['title'],
        'id' => $itemid,
        'image' => $itemimg,
        'status' => $itemInfo['featured'] == 1 ? 'FEATURED' : '',
        'last_update' => $date->format('Y-m-d h:i a'),
        'full_content' => htmlspecialchars_decode(stripslashes($itemData['content'])));
    $response["result"] = "success";
    $response["news_info"] = $item;
}