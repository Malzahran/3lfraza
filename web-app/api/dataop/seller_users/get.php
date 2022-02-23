<?php
$items = array();
$dataObj = new \iCms\DataGet();
$filterDT = array();
$limit = 10;
$btnid = 0;
$datapage = (int)$data->misc->page;
$page = ($datapage == 1) ? 1 : $datapage;
$start = ($datapage - 1) * $limit;
$filterDT['length'] = $limit;
$filterDT['start'] = $start;
$filterDT['store_id'] = $user['store_id'];
$filterDT['utype'] = array('seller');
$search = $data->misc->searchq;
if (!empty($search)) $filterDT['search'] = array('value' => $search);
$allowbuttons = 0;
$buttonst = array();
$sAdmin = false;
if (@$seller['system'] || @$seller['moderator']) {
    $sAdmin = true;
    $allowbuttons = 1;
    $intentdata = array('intent' => 6, 'reqtype' => 'seller_users', 'reqstype' => 'add_form', 'reqptype' => 'add');
    $buttonst[] = array(
        'id' => $btnid += 1,
        'place' => 2,
        'color' => 9,
        'text' => Lang('add_new_btn'),
        'intentData' => $intentdata
    );
}
$fetch = $dataObj->getUserIds($filterDT);
if (!empty($fetch['data'])) {
    $UserDObj = new \iCms\User();
    foreach ($fetch['data'] as $v) {
        $id = $v;
        $itemInfo = $UserDObj->getById($id);
        $timestamp = $itemInfo['time'];
        $datetimeFormat = 'Y-m-d';
        $date = new \DateTime();
        $date->setTimestamp($timestamp);
        $buttons = array();
        if ($sAdmin) {
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_users', 'reqstype' => 'edit_form', 'reqptype' => 'edit', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 2,
                'text' => Lang('edit_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_users', 'reqstype' => 'delete_confirm', 'reqptype' => 'delete', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 1,
                'text' => Lang('delete_btn'),
                'intentData' => $intentdata
            );
        }
        $uType = null;
        switch ($itemInfo['store_admin']) {
            case 1:
                $uType = Lang('sadmin_type_label');
                break;
            case 2:
                $uType = Lang('admin_type_label');
                break;
            case 3:
                $uType = Lang('seller_type');
                break;
        }
        $items[] = array(
            'title' => $itemInfo['name'],
            'id' => $id,
            'buttons' => $buttons,
            'state' => $itemInfo['username'],
            'desc' => '<h5>' . $uType . '</h5>',
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
    'abtitle' => Lang('users_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;
$response["layout"]["buttons"] = $buttonst;