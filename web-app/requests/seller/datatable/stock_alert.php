<?php
$data = array();
if (!empty($_POST)) {
    $fetch = $utiObj->getStockAlertReport();
    $viewData = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch)) {
        foreach ($fetch as $v) {
            $itemInfo = $utiObj->getItemInfo($v['item_id']);
            if (isset($itemInfo['item_id'])) {
                $report_info = $utiObj->getStockAlertReport($v['item_id']);
                if (isset($report_info[0]['count'])) {
                    $report_info = $report_info[0];
                    $n++;
                    $timestamp = $report_info['time'];
                    $datetimeFormat = 'Y-m-d h:i a';
                    $date = new \DateTime();
                    $date->setTimestamp($timestamp);
                    
                    $themeData['item_id'] = $v['item_id'];
                    $alert_button = \iCms\UI::view('backend/seller/stock_alert/notify-btn');
                    $viewData[] = array(
                        "DT_RowId" => 'tr_' . $v['item_id'],
                        'id' => $n,
                        'title' => $itemInfo['title'],
                        'count' => $report_info['count'],
                        'date' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                        'more' => $alert_button);
                }
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