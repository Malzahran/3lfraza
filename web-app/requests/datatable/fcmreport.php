<?php
$data = array();
if (!empty($_POST)) {
    $filterdata = array();
    $filterdata['rptype'] = array(1, 2, 3, 7);
    $fetch = $dataObj->getRepIds(array_merge($_POST, $filterdata));
    $viewdata = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $n++;
            $repinfo = $utiObj->getReport($v);
            $timestamp = $repinfo['time'];
            $datetimeFormat = 'Y-m-d h:i a';
            $date = new \DateTime();
            $date->setTimestamp($timestamp);
            $total = $repinfo['totalcount'];
            $success = $repinfo['success'];
            $failure = $repinfo['failure'];
            $city = $repinfo['cityid'];
            $desc = $state = $notftype = null;
            switch ($repinfo['notf_type']) {
                case 1:
                    $notftype = Lang('normal_notf');
                    break;
                case 2:
                    $notftype = Lang('msg_notf');
                    break;
                case 3:
                    $notftype = Lang('url_notf');
                    break;
                case 7:
                    $notftype = Lang('view_item_label');
                    break;
            }
            if ($total == $success && $total == 1 && $city == 0) {
                $state = '<span class="label label-success">' . Lang('notf_success_label') . '</span>';
            } else if ($total == $failure && $total == 1 && $city == 0) {
                $state = '<span class="label label-danger">' . Lang('notf_fail_label') . '</span>';
            } else if ($total == $success && $total >= 1 && $city != 0) {
                $state = '<span class="label label-primary">' . Lang('total_label') . $total . '</span>';
                $state .= '<span class="label label-success">' . Lang('notf_success_label') . '</span>';
            } else if ($total > $success && $total >= 1 && $city != 0) {
                $state = '<span class="label label-primary">' . Lang('total_label') . $total . '</span>';
                $state .= '<span class="label label-success">' . Lang('notf_success_label') . ' : ' . $success . '</span>';
                $state .= '<span class="label label-danger">' . Lang('notf_fail_label') . ' : ' . $failure . '</span>';
                $state .= '<br><span class="label label-warning">Error : ' . $repinfo['error'] . '</span>';
            }

            if ($city == 5000) {
                $cityinfo = Lang('no_filter_label');
            } else if ($city == 8000) {
                $cityinfo = Lang('all_label');
            } else if ($city != 0 && $city != 5000 && $city != 8000) {
                $cityd = $utiObj->getCity($city);
                $cityinfo = Lang($cityd['city_name'], 2);
            }

            if ($total == 1 && $city == 0) {
                $UserDObj = new \iCms\User();
                $uinfo = $UserDObj->getById($repinfo['user_id']);
                $desc = Lang('single_notf') . ' ' . $uinfo['name'];
            } else {
                $desc = Lang('group_notf') . (isset($cityinfo) ? ' (' . $cityinfo . ')' : '');
            }


            $viewdata[] = array(
                "DT_RowId" => 'tr_' . $repinfo['id'],
                'id' => $n,
                'desc' => $desc,
                'msgtitle' => $repinfo['notf_title'],
                'msg' => $repinfo['notf_msg'],
                'pros' => $notftype,
                'state' => $state,
                'time' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>');
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewdata);
}