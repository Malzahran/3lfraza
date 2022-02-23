<?php
$items = array();
$filterData = array();
$dataObj = new iCmsSeller\DataGet();
$mediaObj = new \iCms\Media();
$total_items = 0;
$limit = 10;
$datapage = isset($data->misc->page) ? (int)$data->misc->page : 0;
$page = ($datapage == 1) ? 1 : $datapage;
$start = ($datapage - 1) * $limit;
$filterData['length'] = $limit;
$filterData['start'] = $start;
$fetch = $dataObj->getNewsIds($filterData);
if (!empty($fetch)) {
    $total_items = $fetch['filter'];
    foreach ($fetch['data'] as $v) {
        $id = $v['id'];
        $ids[] = $id;
        $itemInfo = $utiObj->getNews($id);
        $itemData = $utiObj->getNewsInfo($id);
        $itemimg = $site_url . '/media/rec_logo.png';
        if ($itemInfo['featured_image'] != 0) {
            $itemimg = $mediaObj->getById($itemInfo['featured_image']);
            $itemimg = $itemimg['each'][0]['thumb_url'];
        }
        $items[] = array(
            'title' => $itemData['title'],
            'status' => $itemInfo['featured'] == 1 ? 'FEATURED' : '',
            'brief_content' => htmlspecialchars_decode(stripslashes($itemData['content'])),
            'id' => $itemInfo['id'],
            'image' => $itemimg);
    }
    $actionObj = new \iCms\ActionUtilities();
    if (!empty($ids)) $actionObj->setNewsAction(1, $ids);
}
$response["result"] = "success";
$response["news_infos"] = $items;
$response['total'] = $total_items;