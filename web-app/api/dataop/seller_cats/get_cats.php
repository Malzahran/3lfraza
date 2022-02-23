<?php
$mId = (int)$data->misc->aid;
$id = 0;
$storeID = $user['store_id'];
$form = array();
$exclude = array(-1);
if (!in_array($mId, $exclude)) {
    $spindata = array();
    $spindata[] = array('id' => -1, 'name' => Lang('main_category'));
    $spindata[] = array('id' => -2, 'name' => Lang('no_sub_cat'));
    $mcats = $utiObj->getStoreMainCats($mId, $storeID, array(1));
    if (!empty($mcats))
        foreach ($mcats as $k => $v) {
            if ($v['has_sub'] == 1) $spindata[] = array('id' => $v['category_id'], 'name' => Lang($v['category_name'], 3));
        }
    $form[] = array(
        'id' => $mId . ($id++),
        'place' => 1,
        'required' => 1,
        'icon' => 17,
        'type' => 2,
        'name' => 'subcat',
        'title' => Lang('select_sub_cat'),
        'spinner' => $spindata);
}
$response["result"] = "success";
$response["form"] = array();
$response["form"]["formdata"] = $form;