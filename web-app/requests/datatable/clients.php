<?php
$data = array();
if (!empty($_POST)) {
    $filterDT = array();
    $storeid = $reptype = $ordtype = 0;
    if ($adminLogged) {
        $filterDT['type'] = array(10);
        if (isset($_POST['connect']) && $_POST['connect'] == 1 && isset($_POST['store']) && $_POST['store'] == 1) {
            $filterDT['type'] = array(1, 2, 3, 4);
            $ordtype = 3;
        }
    }
    if (isset($_POST['users']) && $_POST['users'] == 1) {
        $reptype = 3;
        if ($user['type'] == "seller") {
            $storeid = $user['store_id'];
            $filterDT['store_id'] = $storeid;
        }
    }
    $fetch = $dataObj->getOrdrep(array_merge($filterDT, $_POST), $reptype);
    $viewdata = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        if ($reptype == 2 || $reptype == 3) {
            $userDObj = new \iCms\User();
            $chkObj = new \iCms\CheckUtilities();
        }

        foreach ($fetch['data'] as $v) {
            $n++;
            $id = $v['id'];
            $title = '';
            switch ($reptype) {
                case 2:
                case 3:
                    $info = $userDObj->getById($id);
                    $title = $info['name'];
                    break;
            }
            $state = '<span class="label label-success">' . Lang('un_suspended_label') . '</span>';
            $actvbtn = 'unactive-btn';
            $themeData['state_action'] = Lang('suspend_btn');
            $themeData['state_code'] = 2;
            if (isset($info['active']) && $info['active'] == 0) {
                $state = '<span class="label label-warning">' . Lang('suspended_label') . '</span>';
                $actvbtn = 'active-btn';
                $themeData['state_action'] = Lang('unsuspend_btn');
                $themeData['state_code'] = 1;

            }

            $area = '';
            if (isset($info['current_city']) && $info['current_city'] != 0) {
                $city_info = $utiObj->getCity($info['current_city']);
                $area = isset($city_info['city_id']) ? Lang($city_info['city_name'], 2) : Lang('no_filter_label');
            }
            $themeData['item_id'] = $id;
            $themeData['item_name'] = $title;
            $themeData['more_mange'] = \iCms\UI::view('backend/global/buttons/' . $actvbtn);
            $moremange = \iCms\UI::view('backend/global/buttons/morebtn');
            $viewdata[] = array(
                "DT_RowId" => 'tr_' . $id,
                'id' => $n,
                'title' => $title,
                'state' => $state,
                'area' => $area,
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