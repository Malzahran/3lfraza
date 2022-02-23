<?php
$items = array();
$dataObj = new \iCmsSeller\DataGet();
$mediaObj = new \iCms\Media();
$filterDT = array();
$limit = 10;
$btnid = 0;
$datapage = (int)$data->misc->page;
$page = ($datapage == 1) ? 1 : $datapage;
$start = ($datapage - 1) * $limit;
$filterDT['length'] = $limit;
$filterDT['start'] = $start;
$filterDT['store_id'] = $user['store_id'];
$filterDT['order'][] = array('column' => '12', 'dir' => 'desc');
$search = $data->misc->searchq;
if (!empty($search)) $filterDT['search'] = array('value' => $search);
$allowbuttons = 0;
$buttonst = array();
$sAdmin = false;
if (@$seller['system'] || @$seller['moderator']) {
    $sAdmin = true;
    $allowbuttons = 1;
    $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'add_form', 'reqptype' => 'add');
    $buttonst[] = array(
        'id' => $btnid += 1,
        'place' => 2,
        'color' => 9,
        'text' => Lang('add_new_btn'),
        'intentData' => $intentdata
    );
}
$fetch = $dataObj->getItemIds($filterDT);
if (!empty($fetch['data'])) {
    foreach ($fetch['data'] as $v) {
        $id = $v['id'];
        $itemInfo = $utiObj->getItem($id);
        $itemData = $utiObj->getItemInfo($id);
        $inventory = $category = $actionIntent = $action = null;
        if ($aCATS) {
            $catinfo = $utiObj->getStoreCats($itemInfo['category']);
            $category = Lang($catinfo['category_name'], 3);
        }
        $timestamp = $itemInfo['time'];
        $datetimeFormat = 'Y-m-d';
        $date = new \DateTime();
        $date->setTimestamp($timestamp);
        $buttons = array();
        $desc = '<h3><font color="red">' . Lang('view_info_btn') . '</font></h3>';
        $desc .= '<h5>' . htmlspecialchars_decode(stripslashes($itemData['content'])) . '</h5>';
        $img = $site_url . '/media/logo.png';
        if ($itemInfo['featured_image'] != 0) {
            $imgGet = $mediaObj->getById($itemInfo['featured_image']);
            $img = $imgGet['each'][0]['thumb_url'];
        }
        $price = $itemInfo['price'] != 0 ? round($itemInfo['price'], 2) . ' ' . Lang('currency_1') : '';
        if ($aINV && $sAdmin) $inventory = Lang('inventory_label') . ' : ' . $itemInfo['inventory'];
        if ($sAdmin) {
            if ($itemInfo['price'] == 0 && $aFeatures && (!$aCATS || ($aCATS && isset($catinfo['type']) && $catinfo['type'] == 1))) {
                $intentdata = array('intent' => 1, 'reqtype' => 'seller_items', 'reqstype' => 'features', 'reqatype' => 'get', 'reqid' => $id);
                $buttons[] = array(
                    'id' => $btnid += 1,
                    'color' => 3,
                    'text' => Lang('features_label'),
                    'intentData' => $intentdata
                );
            }
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'edit_form', 'reqptype' => 'edit', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 2,
                'text' => Lang('edit_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'delete_confirm', 'reqptype' => 'delete', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 1,
                'text' => Lang('delete_btn'),
                'intentData' => $intentdata
            );
            // Feat Item Button
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'feat_confirm', 'reqptype' => 'feat', 'reqid' => $id);
            $fAction = $itemInfo['featured'] == 0 ? Lang('feature_btn') : Lang('unfeature_btn');
            $fColor = $itemInfo['featured'] == 0 ? 3 : 2;
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => $fColor,
                'text' => $fAction,
                'intentData' => $intentdata
            );
        }
        $items[] = array(
            'title' => $itemInfo['item_code'] . ' - ' . $itemData['title'],
            'id' => $itemInfo['id'],
            'actionintent' => $actionIntent,
            'action' => $action,
            'buttons' => $buttons,
            'minfo' => $category,
            'minfo2' => $inventory,
            'desc' => $desc,
            'image_url' => $img,
            'state' => $price,
            'time' => Lang('added_time_label') . ' : ' . $date->format($datetimeFormat),
            'click' => 0);
    }
}
$layout = array(
    'layout' => 2,
    'indvclk' => 1,
    'allowback' => 1,
    'barback' => 1,
    'allowmore' => 1,
    'allowbuttons' => $allowbuttons,
    'searchtype' => 2,
    'allowsearch' => 1,
    'refresh' => 1,
    'orientation' => 0,
    'showicon' => 1,
    'bartitle' => 1,
    'abtitle' => Lang('seller_items_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;
$response["layout"]["buttons"] = $buttonst;