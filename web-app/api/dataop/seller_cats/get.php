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
$filterDT['type'] = array(1);
$filterDT['store_id'] = $user['store_id'];
$search = $data->misc->searchq;
if (!empty($search)) $filterDT['search'] = array('value' => $search);
$allowbuttons = 0;
$buttonst = array();
$sAdmin = false;
if (@$seller['system'] || @$seller['moderator']) {
    $sAdmin = true;
    $allowbuttons = 1;
    $intentdata = array('intent' => 6, 'reqtype' => 'seller_cats', 'reqstype' => 'add_form', 'reqptype' => 'add');
    $buttonst[] = array(
        'id' => $btnid += 1,
        'place' => 2,
        'color' => 9,
        'text' => Lang('add_new_btn'),
        'intentData' => $intentdata
    );
}
$fetch = $dataObj->getCatIds($filterDT);
if (!empty($fetch['data'])) {
    foreach ($fetch['data'] as $v) {
        $id = $v;
        $itemInfo = $utiObj->getStoreCats($id);
        $catorder = null;
        if ($itemInfo['cat_order'] != 5000) $catorder = Lang('cat_order_label') . ' ' . $itemInfo['cat_order'];
        $timestamp = $itemInfo['time'];
        $datetimeFormat = 'Y-m-d';
        $date = new \DateTime();
        $date->setTimestamp($timestamp);
        $buttons = array();
        $img = $site_url . '/media/logo.png';
        if ($itemInfo['image_id'] != 0) {
            $imgGet = $mediaObj->getById($itemInfo['image_id']);
            $img = $imgGet['each'][0]['thumb_url'];
        }
        if ($sAdmin) {
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_cats', 'reqstype' => 'edit_form', 'reqptype' => 'edit', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 2,
                'text' => Lang('edit_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_cats', 'reqstype' => 'delete_confirm', 'reqptype' => 'delete', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 1,
                'text' => Lang('delete_btn'),
                'intentData' => $intentdata
            );
        }
        $pcat = $mcat = '';
        if ($itemInfo['pid'] != 0) {
            $pcatinfo = $utiObj->getStoreCats($itemInfo['pid']);
            $pcat = isset($pcatinfo['category_id']) ? ' / ' . Lang($pcatinfo['category_name'], 3) : '';
        }
        if ($itemInfo['mid'] != 0) {
            $mcatinfo = $utiObj->getStoreCats($itemInfo['mid']);
            $mcat = isset($mcatinfo['category_id']) ? ' / ' . Lang($mcatinfo['category_name'], 3) : '';
        }
        $items[] = array(
            'title' => Lang($itemInfo['category_name'], 3) . $mcat . $pcat,
            'id' => $id,
            'buttons' => $buttons,
            'minfo2' => $catorder,
            'image_url' => $img,
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
    'orientation' => 0,
    'refresh' => 1,
    'showicon' => 1,
    'bartitle' => 1,
    'abtitle' => Lang('seller_cats_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;
$response["layout"]["buttons"] = $buttonst;