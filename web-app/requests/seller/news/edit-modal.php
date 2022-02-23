<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $mediaObj = new \iCms\Media();
    $itemFetch = $utiObj->getNews($itemID);
    $itemFetchAR = $utiObj->getNewsInfo($itemID, 'ar');
    $itemFetchEN = $utiObj->getNewsInfo($itemID, 'en');
    $themeData['item_main_img'] = '';
    if (!empty($itemFetch)) {
        if ($itemFetch['featured_image'] != 0) {
            $fimage = $mediaObj->getById($itemFetch['featured_image']);
            $themeData['item_main_img'] = '<div class="col-sm-12"><img width="100%" src=' . $fimage['each'][0]['rec_url'] . ' class="img-rounded"></div>';
        }
        $themeData['item_id'] = $itemID;
        $themeData['item_active'] = '';
        if ($itemFetch['active'] == 1) {
            $themeData['item_active'] = 'checked';
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
        $themeData['more_edit_modal'] = '';
        $html = \iCms\UI::view('backend/seller/news/edit-content');
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}