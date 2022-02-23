<?php
$data = array();
if (!empty($_POST)) {
    $state = array();
    if (isset($_POST['active']) && $_POST['active'] == 1) {
        $state[] = 1;
    }
    if (isset($_POST['unactive']) && $_POST['unactive'] == 1) {
        $state[] = 0;
    }
    $filterdata = array();
    $filterdata['active'] = $state;
    $filterdata['utype'] = array('worker');
    $filterdata['worker'] = 1;
    if ($sellerLogged) {
        $filterdata['store_id'] = $user['store_id'];
    }
    $fetch = $dataObj->getUserIds(array_merge($_POST, $filterdata));
    $viewdata = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        $UserDObj = new \iCms\User();
        foreach ($fetch['data'] as $v) {
            $n++;
            $uinfo = $UserDObj->getById($v);
            $timestamp = $uinfo['time'];
            $datetimeFormat = 'Y-m-d h:i a';
            $date = new \DateTime();
            $date->setTimestamp($timestamp);
            $themeData['item_id'] = $uinfo['id'];
            $themeData['item_name'] = $uinfo['name'];
            $themeData['more_mange'] = '';
            if ($uinfo['active'] != true) {
                $actvst = '<span class="label label-warning">' . Lang('suspend_user') . '</span>';
                $actvbtn = 'active-btn';
                $themeData['state_action'] = Lang('activiate_btn');
                $themeData['state_code'] = 1;
            } else {
                $actvst = '<span class="label label-success">' . Lang('active_user') . '</span>';
                $actvbtn = 'unactive-btn';
                $themeData['state_action'] = Lang('deactiviate_btn');
                $themeData['state_code'] = 2;
            }

            $area = '';
            if ($uinfo['current_city'] != 0) {
                $city_info = $utiObj->getCity($uinfo['current_city']);
                $area = isset($city_info['city_id']) ? Lang($city_info['city_name'], 2) : Lang('no_filter_label');
            }
            $themeData['more_info'] = null;
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/' . $actvbtn);
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/edit-btn');
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/delete-btn');
            $moremange = \iCms\UI::view('backend/global/buttons/morebtn');


            $viewdata[] = array(
                "DT_RowId" => 'tr_' . $uinfo['id'],
                'id' => $n,
                'name' => $uinfo['name'],
                'date' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                'area' => $area,
                'active' => $actvst,
                'more' => $moremange);
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewdata);
}