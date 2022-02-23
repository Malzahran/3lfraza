<?php
$mediaObj = new \iCms\Media();
$cats = array();
$catType = array(1);
$catID = (int)$data->misc->catid;
$city_group = (int)$data->user->city_group;
$substate = 0;
if ($catID == 8000) {
    $substate = 1;
    $catget = $utiObj->getStoreCats(0, $storeID, $catType, array(3, $city_group));
} else {
    $catinfo = $utiObj->getStoreCats($catID);
    if (isset ($catinfo['category_id'])) {
        if ($catinfo['has_sub'] == 0) $catget = $utiObj->getStoreMainCats($catID, $storeID, $catType, array(3, $city_group));
        else $catget = $utiObj->getStoreSubCat($catID, $storeID, $catType, array(3, $city_group));
    }
}
if (!empty($catget)) {
    foreach ($catget as $k => $v) {
        $catid = $v['category_id'];
        $catname = Lang($v['category_name'], 3);
        $sub = $substate;
        if ($v['has_sub'] == 1) $sub = 1;
        if ($v['has_sub'] == 2) $sub = 0;
        $catimg = $site_url . '/media/logo.png';
        if (!empty($v['image_id'])) {
            $catimg = $mediaObj->getById($v['image_id']);
            $catimg = $catimg['each'][0]['rec_url'];
        }
        $cats[] = array('show_title' => 0, 'name' => $catname, 'color' => $v['color'], 'brief' => $v['pid'] != 0 ? Lang($catinfo['category_name'], 3) : Lang('app_name'), 'id' => $catid, 'image_url' => $catimg, 'hassub' => $sub);
    }
}
$response["result"] = "success";
$response["cats"] = $cats;