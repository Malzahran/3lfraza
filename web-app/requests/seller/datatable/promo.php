<?php
$data = array();
if (!empty($_POST)) {
    $state = array();
    if (isset($_POST['active']) && $_POST['active'] == 1) $state[] = 1;
    if (isset($_POST['unactive']) && $_POST['unactive'] == 1) $state[] = 0;
    $filterData = array();
    $filterData['active'] = $state;
    $fetch = $dataObj->getPromoIds(array_merge($_POST, $filterData));
    $viewData = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $n++;
            $itemInfo = $utiObj->getPromoCode('', $v['id']);
            if (isset($itemInfo['id'])) {
                $timestamp = $itemInfo['time'];
                $datetimeFormat = 'Y-m-d h:i a';
                $date = new \DateTime();
                $date->setTimestamp($timestamp);
                $themeData['item_id'] = $itemInfo['id'];
                $themeData['item_name'] = $itemInfo['promo'];
                $themeData['more_mange'] = '';
                $actvst = $itemInfo['active'] != 0 ? '<span class="label label-success">' . Lang('active_label') . '</span>' : '<span class="label label-warning">' . Lang('unactive_label') . '</span>';
                $actvbtn = $itemInfo['active'] != 0 ? 'unactive-btn' : 'active-btn';
                $themeData['state_action'] = $itemInfo['active'] != 0 ? Lang('deactiviate_btn') : Lang('activiate_btn');
                $themeData['state_code'] = $itemInfo['active'] != 0 ? 2 : 1;
                $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/' . $actvbtn);
                $moremange = \iCms\UI::view('backend/global/buttons/more-btn');


                $viewData[] = array(
                    "DT_RowId" => 'tr_' . $itemInfo['id'],
                    'id' => $n,
                    'code' => $itemInfo['promo'],
                    'type' => $itemInfo['type'] == 1 ? Lang('percentage_label') : Lang('amount_label'),
                    'price' => round($itemInfo['value'], 2),
                    'date' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                    'active' => $actvst,
                    'more' => $moremange);
            }
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewData);
}