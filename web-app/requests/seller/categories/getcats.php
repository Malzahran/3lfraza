<?php
sellerOnly();
if (!empty($_POST['type'])) {
    $type = (int)$_POST['type'];
    if ($type == 1) {
        $html = null;
        $html .= '<option value="-1">' . Lang('no_main_cat') . '</option>';
        $html .= '<option value="-2">' . Lang('no_sub_cat') . '</option>';
        $pcats = $utiObj->getStoreCats(0, $user['store_id'], array(1), array(), 1);
        if (!empty($pcats)) {
            foreach ($pcats as $k => $v) {
                $themeData['sel_id'] = $v['category_id'];
                $themeData['sel_name'] = Lang($v['category_name'], 3);
                $html .= \iCms\UI::view('backend/global/select/selectopt');
            }
        }
    } else if ($type == 2) {
        $pcat = !empty($_POST['pcatid']) ? (int)$_POST['pcatid'] : 0;
        $html = '';
        $html .= '<option value="-1">' . Lang('main_category') . '</option>';
        $html .= '<option value="-2">' . Lang('no_sub_cat') . '</option>';
        $mcats = $utiObj->getStoreMainCats(0, $user['store_id'], array(1));
        if (!empty($mcats)) {
            foreach ($mcats as $k => $v) {
                if ($v['has_sub'] == 1) {
                    $themeData['sel_id'] = $v['category_id'];
                    $themeData['sel_name'] = Lang($v['category_name'], 3);
                    $html .= \iCms\UI::view('backend/global/select/selectopt');
                }
            }
        }
    }
    if (!empty($html)) $data = array('status' => 200, 'html' => $html);
}