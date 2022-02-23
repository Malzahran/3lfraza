<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $mediaObj = new \iCms\Media();
    $itemFetch = $utiObj->getStoreCats($itemID);
    $themeData['cat_img'] = '';
    if (!empty($itemFetch)) {
        $themeData['cat_id'] = $itemFetch['category_id'];
        if ($itemFetch['image_id'] != 0) {
            $fimage = $mediaObj->getById($itemFetch['image_id']);
            $themeData['cat_img'] = $fimage['each'][0]['squ_url'];
        }
        $themeData['cat_main_hidden'] = $itemFetch['type'] == 1 ? '' : 'hidden';
        $themeData['cat_sub_hidden'] = 'hidden';
        $themeData['cat_p_select'] = '';
        $themeData['cat_m_select'] = '';
        $themeData['cat_order'] = $itemFetch['cat_order'] != 5000 ? $itemFetch['cat_order'] : '';
        $themeData['more_edit_modal'] = '';

        if ($itemFetch['type'] == 1) {
            $themeData['group_select'] = '<option value=' . $itemFetch['city_group'] . '>' . ($itemFetch['city_group'] == 3 ? Lang('all_label') : $itemFetch['city_group']) . '</option>';
            $groups = array();
            $groups[] = array('id' => 1, 'label' => 1);
            $groups[] = array('id' => 2, 'label' => 2);
            $groups[] = array('id' => 3, 'label' => Lang('all_label'));
            if (!empty($groups)) {
                $themeData['select_items'] = '';
                foreach ($groups as $k => $v) {
                    if (isset($v['id'])) {
                        if ($itemFetch['city_group'] != $v['id']) {
                            $themeData['sel_id'] = $v['id'];
                            $themeData['sel_name'] = $v['label'];
                            $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                        }
                    }
                }
                $themeData['group_select'] .= $themeData['select_items'];
            }
            $themeData['more_edit_modal'] .= \iCms\UI::view('backend/seller/categories/edit-group-sel');
        }

        $themeData['type_select'] = '<option value=' . $itemFetch['type'] . '>' . Lang('seller_cat_type_' . $itemFetch['type']) . '</option>';
        $type = array();
        $type[] = array('id' => 1, 'label' => Lang('seller_cat_type_1'));
        $type[] = array('id' => 4, 'label' => Lang('seller_cat_type_4'));
        if (!empty($type)) {
            $themeData['select_items'] = '';
            foreach ($type as $k => $v) {
                if (isset($v['id'])) {
                    if ($itemFetch['type'] != $v['id']) {
                        $themeData['sel_id'] = $v['id'];
                        $themeData['sel_name'] = $v['label'];
                        $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                    }
                }
            }
            $themeData['type_select'] .= $themeData['select_items'];
        }
        $themeData['more_edit_modal'] .= \iCms\UI::view('backend/seller/categories/edit-type-sel');

        $themeData['cat_title_en'] = Lang($itemFetch['category_name'], 3, 'en');
        $themeData['cat_title_ar'] = Lang($itemFetch['category_name'], 3, 'ar');

        if ($itemFetch['pid'] != 0) {
            $parentcat = $utiObj->getStoreCats($itemFetch['pid']);
            if ($parentcat['type'] == $itemFetch['type']) {
                $themeData['sel_id'] = $parentcat['category_id'];
                $themeData['sel_name'] = Lang($parentcat['category_name'], 3);
                $themeData['cat_p_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                $themeData['cat_sub_hidden'] = '';
            }
            $themeData['cat_p_select'] .= '<option value="-1">' . Lang('no_main_cat') . '</option>';
        } else $themeData['cat_p_select'] .= '<option value="' . ($itemFetch['has_sub'] == 2 ? -2 : -1) . '">' . ($itemFetch['has_sub'] == 2 ? Lang('no_sub_cat') : Lang('no_main_cat')) . '</option>';

        if ($itemFetch['mid'] == 0 && $itemFetch['has_sub'] == 1) {
            $themeData['cat_m_select'] .= '<option value="-1">' . Lang('main_category') . '</option>';
            $themeData['cat_m_select'] .= '<option value="-2">' . Lang('no_sub_cat') . '</option>';
        } else if ($itemFetch['mid'] == 0 && $itemFetch['has_sub'] == 0) {
            $themeData['cat_m_select'] .= '<option value="-2">' . Lang('no_sub_cat') . '</option>';
            $themeData['cat_m_select'] .= '<option value="-1">' . Lang('main_category') . '</option>';
        } else if ($itemFetch['mid'] != 0 && $itemFetch['has_sub'] == 1) {
            $themeData['cat_m_select'] .= '<option value="-1">' . Lang('main_category') . '</option>';
            $themeData['cat_m_select'] .= '<option value="-2">' . Lang('no_sub_cat') . '</option>';
        } else if ($itemFetch['mid'] != 0 && $itemFetch['has_sub'] == 0) {
            $maincat = $utiObj->getStoreCats($itemFetch['mid']);
            $themeData['sel_id'] = $maincat['category_id'];
            $themeData['sel_name'] = Lang($maincat['category_name'], 3);
            $themeData['cat_m_select'] .= \iCms\UI::view('backend/global/select/selectopt');
            $themeData['cat_m_select'] .= '<option value="-2">' . Lang('no_sub_cat') . '</option>';
            $themeData['cat_m_select'] .= '<option value="-1">' . Lang('main_category') . '</option>';
        }
        $parentcats = $utiObj->getStoreCats(0, $itemFetch['store_id'], array($itemFetch['type']), array(), 1);
        if (!empty($parentcats)) {
            foreach ($parentcats as $k => $v) {
                if ($itemFetch['pid'] == 0 && $v['category_id'] != $itemFetch['category_id'] || $itemFetch['pid'] != 0 && $v['category_id'] != $itemFetch['pid']) {
                    $themeData['sel_id'] = $v['category_id'];
                    $themeData['sel_name'] = Lang($v['category_name'], 3);
                    $themeData['cat_p_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                }
            }
        }

        $maincats = $utiObj->getStoreMainCats($itemFetch['pid']);
        if (!empty($maincats)) {
            foreach ($maincats as $k => $v) {
                if ((($itemFetch['mid'] == 0 && $v['category_id'] != $itemFetch['category_id']) || ($itemFetch['mid'] != 0 && $v['category_id'] != $itemFetch['mid'])) && $v['has_sub'] == 1) {
                    $themeData['sel_id'] = $v['category_id'];
                    $themeData['sel_name'] = Lang($v['category_name'], 3);
                    $themeData['cat_m_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                }
            }
        }
        $html = \iCms\UI::view('backend/seller/categories/edit-content');
        $data = array('status' => 200, 'html' => $html);
    }
}