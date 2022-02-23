<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $mediaObj = new \iCms\Media();
    $itemFetch = $utiObj->getItem($itemID);
    $catInfo = $utiObj->getStoreCats($itemFetch['category']);
    $catName = '';
    if (isset($catInfo['category_id'])) {
        $pCat = $catInfo['pid'] != 0 ? $utiObj->getStoreCats($catInfo['pid']) : '';
        $mCat = $catInfo['mid'] != 0 ? $utiObj->getStoreCats($catInfo['mid']) : '';
        $catName = Lang($catInfo['category_name'], 3);
        $catName .= isset($pCat['category_id']) ? ' / ' . Lang($pCat['category_name'], 3) : '';
        $catName .= isset($mCat['category_id']) ? ' / ' . Lang($mCat['category_name'], 3) : '';
    }
    $itemFetchAR = $utiObj->getItemInfo($itemID, 'ar');
    $itemFetchEN = $utiObj->getItemInfo($itemID, 'en');
    $themeData['item_main_img'] = '';
    if (!empty($itemFetch)) {
        if ($itemFetch['featured_image'] != 0) {
            $fimage = $mediaObj->getById($itemFetch['featured_image']);
            $themeData['item_main_img'] = '<div class="col-sm-12"><img width="100%" src=' . $fimage['each'][0]['squ_url'] . ' class="img-rounded"></div>';
        }
        $themeData['item_id'] = $itemID;
        $themeData['item_active'] = '';
        if ($itemFetch['active'] == 1) {
            $themeData['item_active'] = 'checked';
        }
        $themeData['item_cart'] = 'checked';
        if ($itemFetch['cart'] == 1) {
            $themeData['item_cart'] = '';
        }
        $themeData['item_stock'] = 'checked';
        if ($itemFetch['stock'] == 1) {
            $themeData['item_stock'] = '';
        }
        $themeData['item_soon'] = '';
        if ($itemFetch['soon'] == 1) {
            $themeData['item_soon'] = 'checked';
        }
        $themeData['item_code'] = $itemFetch['item_code'];
        $themeData['item_order'] = $itemFetch['itemsorder'] != 9000 ? $itemFetch['itemsorder'] : '';
        $themeData['item_price'] = $itemFetch['price'] != 0 ? round($itemFetch['price'], 2) : '';
        $themeData['item_dsc_price'] = $itemFetch['dsc_price'] != 0 ? round($itemFetch['dsc_price'], 2) : '';
        $breaks = array("<br />", "<br>", "<br/>");
        if (!empty($itemFetchAR)) {
            $themeData['item_title_ar'] = $itemFetchAR['title'];
            $themeData['item_desc_ar'] = str_ireplace($breaks, "\r\n", $itemFetchAR['content']);
            $themeData['item_notes_ar'] = str_ireplace($breaks, "\r\n", $itemFetchAR['notes']);
        }
        if (!empty($itemFetchEN)) {
            $themeData['item_title_en'] = $itemFetchEN['title'];
            $themeData['item_desc_en'] = str_ireplace($breaks, "\r\n", $itemFetchEN['content']);
            $themeData['item_notes_en'] = str_ireplace($breaks, "\r\n", $itemFetchEN['notes']);
        }
        $themeData['pr_required'] = $aFeatures ? '' : 'required';
        $themeData['more_edit_modal'] = '';
        if ($aCATS) {
            $storeID = $user['store_id'];
            $themeData['cat_select'] = '';
            $themeData['cat_select'] .= '<option value=' . $itemFetch['category'] . '>' . $catName . '</option>';
            $pcats = $utiObj->getStoreCats(0, $storeID);
            if (!empty($pcats)) {
                foreach ($pcats as $k => $v) {
                    $mcat = $utiObj->getStoreMainCats($v['category_id']);
                    $themeData['select_items'] = '';
                    if (!empty($mcat)) {
                        foreach ($mcat as $key => $value) {
                            if ($itemFetch['category'] != $value['category_id']) {
                                if ($value['has_sub'] == 1) {
                                    $scat = $utiObj->getStoreSubCat($value['category_id']);
                                    if (!empty($scat)) {
                                        foreach ($scat as $ke => $val) {
                                            if ($itemFetch['category'] != $val['category_id']) {
                                                $themeData['sel_id'] = $val['category_id'];
                                                $themeData['sel_name'] = Lang($val['category_name'], 3) . ' / ' . Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3);
                                                $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                                            }
                                        }
                                    }
                                } else {
                                    $themeData['sel_id'] = $value['category_id'];
                                    $themeData['sel_name'] = Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3);
                                    $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                                }
                            }
                        }
                    } else if ($v['has_sub'] == 2) {
                        if ($itemFetch['category'] != $v['category_id']) {
                            $themeData['sel_id'] = $v['category_id'];
                            $themeData['sel_name'] = Lang($v['category_name'], 3);
                            $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                        }
                    }
                    $themeData['cat_select'] .= $themeData['select_items'];
                }
            }
            $themeData['more_edit_modal'] .= \iCms\UI::view('backend/seller/items/cats-sel');
        }
        if ($aINV) {
            $themeData['item_inventory'] = $itemFetch['inventory'];
            $themeData['more_edit_modal'] .= \iCms\UI::view('backend/seller/items/inv-input');
        }
        $html = \iCms\UI::view('backend/seller/items/edit-content');
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }
}