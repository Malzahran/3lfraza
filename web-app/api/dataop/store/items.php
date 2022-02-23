<?php
$items = array();
$filterData = array();
$dataObj = new iCmsSeller\DataGet();
$mediaObj = new \iCms\Media();
$catID = (int)$data->misc->catid;
$total_items = 0;
$limit = 10;
$datapage = isset($data->misc->page) ? (int)$data->misc->page : 0;
$page = ($datapage == 1) ? 1 : $datapage;
$start = ($datapage - 1) * $limit;
$filterData['length'] = $limit;
$filterData['start'] = $start;
$filterData['store_id'] = $storeID;
if ($catID != 8000) $filterData['cat'] = $catID;
$search = isset($data->misc->searchq) ? $data->misc->searchq : '';
if (!empty($search)) $filterData['search'] = array('value' => $search);
$fetch = $dataObj->getItemIds($filterData);
if (!empty($fetch)) {
    $cartObj = new \iCms\Cart();
    $cartObj->setUserId($user['id']);
    $total_items = $fetch['filter'];
    foreach ($fetch['data'] as $v) {
        $id = $v['id'];
        $ids[] = $id;
        $itemInfo = $utiObj->getItem($id);
        $itemData = $utiObj->getItemInfo($id);
        $itemimg = $site_url . '/media/rec_logo.png';
        if ($itemInfo['featured_image'] != 0) {
            $itemimg = $mediaObj->getById($itemInfo['featured_image']);
            $itemimg = $itemimg['each'][0]['thumb_url'];
        }
        $cartObj->setId($id);
        if ($itemInfo['price'] == 0) {
            $featuresFetch = $utiObj->getItemFeatures($id, 0, true);
            if (isset($featuresFetch[0]['id'])) {
                $itemInfo['price'] = $featuresFetch[0]['price'];
                $itemInfo['dsc_price'] = $featuresFetch[0]['dsc_price'];
            }
        }
        $cartObj->setStoreId($itemInfo['store_id']);
        $checkcart = $cartObj->CheckWholeItem();
        $quantity = isset($checkcart['qty']) ? $checkcart['qty'] : 0;
        $dscprice = $itemInfo['dsc_price'] != 0 ? round($itemInfo['dsc_price'], 2) : null;
        $status = 0;
        if (!$itemInfo['cart'] && !$itemInfo['stock'] && $itemInfo['inventory'] < 1) $status = 2;
        if ($itemInfo['soon']) $status = 3;
        $items[] = array(
            'title' => $itemData['title'],
            'price' => round($itemInfo['price'], 2) . ' ' . Lang('currency_1'),
            'dsc_prc' => $dscprice,
            'quantity' => $quantity,
            'prc' => $itemInfo['price'],
            'desc' => htmlspecialchars_decode(stripslashes($itemData['content'])),
            'status' => $status,
            'id' => $itemInfo['id'],
            'cart' => $itemInfo['cart'],
            'added_cart' => $checkcart ? 1 : 2,
            'minfo2' => Lang('currency_1'),
            'image_url' => $itemimg);
    }
    $actionObj = new \iCms\ActionUtilities();
    if (!empty($ids)) $actionObj->setItemAction(1, $ids);
}
$response["result"] = "success";
$response["store"]["items"] = $items;
$response['total'] = $total_items;