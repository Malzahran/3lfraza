<?php
$data = array();
if (!empty($_POST)) {
    $UserDObj = new \iCms\User();
    $type = array();
    if (isset($_POST['web']) && $_POST['web'] == 1) {
        $type[] = 'web';
    }
    if (isset($_POST['app']) && $_POST['app'] == 1) {
        $type[] = 'form';
    }
    if (isset($_POST['facebook']) && $_POST['facebook'] == 1) {
        $type[] = 'facebook';
    }
    if (isset($_POST['google']) && $_POST['google'] == 1) {
        $type[] = 'google';
    }
    $filterdata = array();
    $filterdata['actype'] = $type;
    $filterdata['utype'] = array('user', 'admin', 'moderator', 'agent', 'seller');
    $filterdata['active'] = array(0, 1);
    $fetch = $dataObj->getUserIds(array_merge($_POST, $filterdata));
    $viewdata = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
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
            switch ($uinfo['authmethod']) {
                case 'web':
                    $regfrom = '<span class="label label-inverse"><i class="fa fa-globe"></i> ' . Lang('form_web') . '</span>';
                    break;
                case 'form':
                    $regfrom = '<span class="label label-success"><i class="fa fa-mobile"></i> ' . Lang('form_mobile') . '</span>';
                    break;
                case 'google':
                    $regfrom = '<span class="label label-danger"><i class="fa fa-google"></i> ' . Lang('google_reg') . '</span>';
                    break;
                case 'facebook':
                    $regfrom = '<span class="label label-default"><i class="fa fa-facebook"></i> ' . Lang('facebook_reg') . '</span>';
                    break;
            }
            $area = '';
            if ($uinfo['current_city'] != 0) {
                $city_info = $utiObj->getCity($uinfo['current_city']);
                $area = isset($city_info['city_id']) ? Lang($city_info['city_name'], 2) : Lang('no_filter_label');
            }
            $themeData['more_info'] = null;
            if ($uinfo['havetoken'] == true) {
                $actvst .= '<span class="label label-warning"><i class="fa fa-bell"></i></span>';
                if (@$seller['system'] || @$seller['moderator']) {
                    $themeData['more_info'] = \iCms\UI::view('backend/global/buttons/send-notf-btn');
                }
            }
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/' . $actvbtn);
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/edit-btn');
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/delete-btn');
            $moremange = \iCms\UI::view('backend/global/buttons/morebtn');


            $viewdata[] = array(
                "DT_RowId" => 'tr_' . $uinfo['id'],
                'id' => $n,
                'name' => $uinfo['name'],
                'regfrom' => $regfrom,
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
echo json_encode($data);
$conn->close();
exit();