<?php
$data = array();
if (!empty($_POST)) {
    $filterDT = array();
    $filterDT['progress'] = array(1, 2, 3, 4, 5, 6);
    $reptype = 0;
    $ordtype = 0;
    if ($adminLogged) {
        $filterDT['type'] = array(10);
        if (!isset($_POST['store']) && isset($_POST['connect']) && $_POST['connect'] == 1) {
            $filterDT['type'] = array(2, 3, 4);
            $ordtype = 1;
        } else if (!isset($_POST['connect']) && isset($_POST['store']) && $_POST['store'] == 1) {
            $filterDT['type'] = array(1);
            $ordtype = 2;
        } else if (isset($_POST['connect']) && $_POST['connect'] == 1 && isset($_POST['store']) && $_POST['store'] == 1) {
            $filterDT['type'] = array(1, 2, 3, 4);
            $ordtype = 3;
        }
    }
    if (isset($_POST['stores']) && $_POST['stores'] == 1) {
        $reptype = 1;
        if ($user['type'] == "seller") {
            $filterDT['store_id'] = $user['store_id'];
        }
    } else if (isset($_POST['delivery']) && $_POST['delivery'] == 1) {
        $reptype = 2;
        if ($adminLogged) {
            $filterDT['delv_type'] = 0;
        } else if ($user['type'] == "delivery") {
            $filterDT['dlvid'] = $user['id'];
        } else if ($user['type'] == "seller") {
            $filterDT['store_id'] = $user['store_id'];
        }
    } else if (isset($_POST['worker']) && $_POST['worker'] == 1) {
        $reptype = 4;
        if ($user['type'] == "worker") {
            $filterDT['workid'] = $user['id'];
        } else if ($user['type'] == "seller") {
            $filterDT['store_id'] = $user['store_id'];
        }
    } else if (isset($_POST['users']) && $_POST['users'] == 1) {
        $reptype = 3;
        if ($user['type'] == "seller") {
            $filterDT['store_id'] = $user['store_id'];
        }
    }
    $fetch = $dataObj->getOrdrep(array_merge($filterDT, $_POST), $reptype);
    $viewdata = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        if ($reptype != 1) $userDObj = new \iCms\User();
        foreach ($fetch['data'] as $v) {
            $n++;
            $id = $v['id'];
            $title = '';
            switch ($reptype) {
                case 1:
                    $info = $utiObj->getStore($id);
                    $title = $info['dir']['title'];
                    break;
                case 2:
                case 3:
                case 4:
                    $info = $userDObj->getById($id);
                    $title = $info['name'];
                    break;
            }
            $themeData['item_id'] = $id;
            $themeData['item_type'] = $reptype;
            $themeData['ord_type'] = $ordtype;

            $moremange = \iCms\UI::view('backend/report/orders/view-btn');

            $viewdata[] = array(
                "DT_RowId" => 'tr_' . $id,
                'id' => $n,
                'title' => $title,
                'norders' => $v['total'],
                'total' => round($v['sum'], 2),
                'discount' => round($v['discount'], 2),
                'delivery' => round($v['dlv'], 2),
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