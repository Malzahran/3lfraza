<?php
$data = array();
if (!empty($_POST['item_id'])) {
    $itemid = (int)$_POST['item_id'];
    $itemfetch = $utiObj->getStoreCats($itemid);
    if (isset($itemfetch['category_id'])) {
        $mcat = $pcat = '';
        $itemfetchar = Lang($itemfetch['category_name'], 3, 'ar');
        $itemfetchen = Lang($itemfetch['category_name'], 3, 'en');
        $type = ' (' . Lang('seller_cat_type_' . $itemfetch['type']) . ')';
        if ($itemfetch['image_id'] != 0) {
            $MediaObj = new \iCms\Media();
            $fimage = $MediaObj->getById($itemfetch['image_id']);
            $themeData['cat_main_img'] = '<img width="100%" src="' . $fimage['each'][0]['squ_url'] . '" class="img-rounded"><hr>';
        }

        if ($itemfetch['pid'] != 0) {
            $pcatinfo = $utiObj->getStoreCats($itemfetch['pid']);
            $pcat = isset($pcatinfo['category_id']) ? ' / ' . Lang($pcatinfo['category_name'], 3) : '';
        }
        if ($itemfetch['mid'] != 0) {
            $mcatinfo = $utiObj->getStoreCats($itemfetch['mid']);
            $mcat = isset($mcatinfo['category_id']) ? ' / ' . Lang($mcatinfo['category_name'], 3) : '';
        }
        if (!empty($itemfetchar)) $themeData['cat_title_ar'] = $itemfetchar;
        if (!empty($itemfetchen)) $themeData['cat_title_en'] = $itemfetchen;
        $themeData['catname'] = Lang($itemfetch['category_name'], 3) . $mcat . $pcat . $type;
        $html = \iCms\UI::view('backend/seller/categories/view-content');
        $data = array('status' => 200, 'html' => $html);
    }
}