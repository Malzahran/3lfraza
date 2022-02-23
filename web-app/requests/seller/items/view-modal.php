<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $itemFetch = $utiObj->getItem($itemID);
    $itemFetchAR = $utiObj->getItemInfo($itemID, 'ar');
    $itemFetchEN = $utiObj->getItemInfo($itemID, 'en');
    $themeData['item_main_img'] = '';
    if (!empty($itemFetch)) {
        if ($itemFetch['featured_image'] != 0) {
            $mediaObj = new \iCms\Media();
            $fimage = $mediaObj->getById($itemFetch['featured_image']);
            $themeData['item_main_img'] = '<img width="100%" src=' . $fimage['each'][0]['squ_url'] . ' class="img-rounded"><hr>';
        }
        $themeData['item_active'] = '<span class="label label-warning">' . Lang('unactive_label') . '</span>';
        if ($itemFetch['active'] == 1) {
            $themeData['item_active'] = '<span class="label label-success">' . Lang('active_label') . '</span>';
        }
        $themeData['item_code'] = $itemFetch['item_code'];
        $themeData['item_price'] = $itemFetch['price'] != 0 ? Lang('price_label') . ' : ' . round($itemFetch['price'], 2) . ' ' . Lang('currency_1') . '<hr>' : '';
        $breaks = array("<br />", "<br>", "<br/>");
        if (!empty($itemFetchAR)) {
            $themeData['item_title_ar'] = $itemFetchAR['title'];
            $themeData['item_desc_ar'] = str_ireplace($breaks, "\r\n", $itemFetchAR['content']);
        }
        if (!empty($itemFetchEN)) {
            $themeData['item_title_en'] = $itemFetchEN['title'];
            $themeData['item_desc_en'] = str_ireplace($breaks, "\r\n", $itemFetchEN['content']);
        }
        $themeData['cat_info'] = '';
        if ($aCATS) {
            $catInfo = $utiObj->getStoreCats($itemFetch['category']);
            if (isset($catInfo['category_id'])) {
                $mcat = $pcat = '';
                if ($catInfo['pid'] != 0) {
                    $pcatinfo = $utiObj->getStoreCats($catInfo['pid']);
                    $pcat = isset($pcatinfo['category_id']) ? ' / ' . Lang($pcatinfo['category_name'], 3) : '';
                }
                if ($catInfo['mid'] != 0) {
                    $mcatinfo = $utiObj->getStoreCats($catInfo['mid']);
                    $mcat = isset($mcatinfo['category_id']) ? ' / ' . Lang($mcatinfo['category_name'], 3) : '';
                }
                $themeData['cat_info'] = Lang('category_label') . ' : ' . Lang($catInfo['category_name'], 3) . $mcat . $pcat . '<br>';
            }
        }
        $themeData['item_inventory'] = '';
        if ($aINV) {
            $themeData['item_inventory'] = Lang('inventory_label') . ' : ' . $itemFetch['inventory'] . '<br>';
        }
        $html = \iCms\UI::view('backend/seller/items/view-content');
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}