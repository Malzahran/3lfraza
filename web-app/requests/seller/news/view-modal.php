<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $itemFetch = $utiObj->getNews($itemID);
    $itemFetchAR = $utiObj->getNewsInfo($itemID, 'ar');
    $itemFetchEN = $utiObj->getNewsInfo($itemID, 'en');
    $themeData['item_main_img'] = '';
    if (!empty($itemFetch)) {
        if ($itemFetch['featured_image'] != 0) {
            $mediaObj = new \iCms\Media();
            $fimage = $mediaObj->getById($itemFetch['featured_image']);
            $themeData['item_main_img'] = '<img width="100%" src=' . $fimage['each'][0]['rec_url'] . ' class="img-rounded"><hr>';
        }
        $themeData['item_active'] = '<span class="label label-warning">' . Lang('unactive_label') . '</span>';
        if ($itemFetch['active'] == 1) {
            $themeData['item_active'] = '<span class="label label-success">' . Lang('active_label') . '</span>';
        }
        $breaks = array("<br />", "<br>", "<br/>");
        if (!empty($itemFetchAR)) {
            $themeData['item_title_ar'] = $itemFetchAR['title'];
            $themeData['item_desc_ar'] = str_ireplace($breaks, "\r\n", $itemFetchAR['content']);
        }
        if (!empty($itemFetchEN)) {
            $themeData['item_title_en'] = $itemFetchEN['title'];
            $themeData['item_desc_en'] = str_ireplace($breaks, "\r\n", $itemFetchEN['content']);
        }
        $html = \iCms\UI::view('backend/seller/news/view-content');
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}