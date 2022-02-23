<?php
$items = array();
$filterData = array();
$dataObj = new iCmsSeller\DataGet();
$mediaObj = new \iCms\Media();
$filterData['length'] = 5;
$filterData['featured'] = 1;
$fetch = $dataObj->getNewsIds($filterData);
if (!empty($fetch)) {
    foreach ($fetch['data'] as $v) {
        $id = $v['id'];
        $ids[] = $id;
        $itemInfo = $utiObj->getNews($id);
        $itemData = $utiObj->getNewsInfo($id);
        $itemimg = $site_url . '/media/logo.png';
        if ($itemInfo['featured_image'] != 0) {
            $itemimg = $mediaObj->getById($itemInfo['featured_image']);
            $itemimg = $itemimg['each'][0]['rec_url'];
        }
        $items[] = array(
            'title' => $itemData['title'],
            'id' => $itemInfo['id'],
            'image' => $itemimg);
    }
    $actionObj = new \iCms\ActionUtilities();
    if (!empty($ids)) $actionObj->setNewsAction(1, $ids);
}
$response["result"] = "success";
$response["news_infos"] = $items;