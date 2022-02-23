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
$filterDT['active'] = array(1,0);
$filterDT['utype'] = array('delivery');
$filterDT['delv_type'] = 1;
$search = $data->misc->searchq;
if (!empty($search)) $filterDT['search'] = array('value' => $search);
$allowbuttons = 0;
$buttonst = array();
$sAdmin = false;
if (@$seller['system'] || @$seller['moderator']) {
    $sAdmin = true;
    $allowbuttons = 1;
    $intentdata = array('intent' => 6, 'reqtype' => 'seller_delivery', 'reqstype' => 'add_form', 'reqptype' => 'add');
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
        $datetimeFormat = 'Y-m-d h:i a';
        $date = new \DateTime();
        $date->setTimestamp($timestamp);
        $buttons = array();
        $active = $itemInfo['active'] ? 1 : 2;
        if ($sAdmin) {
            $intentdata = array('intent' => 2, 'reqtype' => 'seller_delivery', 'reqstype' => 'view_info', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'text' => Lang('view_info_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_delivery', 'reqstype' => 'edit_form', 'reqptype' => 'edit', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 2,
                'text' => Lang('edit_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_delivery', 'reqstype' => 'delete_confirm', 'reqptype' => 'delete', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 1,
                'text' => Lang('delete_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_workers', 'reqstype' => 'active_confirm', 'reqptype' => 'active', 'reqid' => $id, 'reqmid' => $active);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => $active == 1 ? 2 : 0,
                'text' => $active == 1 ? Lang('deactiviate_btn') : Lang('activiate_btn'),
                'intentData' => $intentdata
            );
        }
        $state = '';
        switch ($active) {
            case 1:
                $state = Lang('active_label');
                break;
            case 2:
                $state = Lang('unactive_label');
                break;
        }
        $items[] = array(
            'title' => $itemInfo['name'],
            'id' => $id,
            'buttons' => $buttons,
            'state' => $state,
            'desc' => '<h4>' . $itemInfo['username'] . '</h4>',
            'contact_title' => Lang('contact_delivery_label'),
            'phone' => $itemInfo['phone'],
            'sms' => $itemInfo['phone'],
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
    'bartitle' => 1,
    'abtitle' => Lang('delivery_agent_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;
$response["layout"]["buttons"] = $buttonst;