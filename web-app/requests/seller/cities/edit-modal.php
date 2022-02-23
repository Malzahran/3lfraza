<?php
sellerOnly();
if (!empty($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $itemFetch = $utiObj->getCity($itemID);
    if (!empty($itemFetch)) {
        $themeData['city_id'] = $itemFetch['city_id'];
        $themeData['group_select'] = '<option value=' . $itemFetch['city_group'] . '>' . $itemFetch['city_group'] . '</option>';
        $themeData['city_lat'] = $itemFetch['lat'];
        $themeData['city_lon'] = $itemFetch['lon'];
        $themeData['city_radius'] = $itemFetch['radius'];
        $themeData['city_shipping'] = $itemFetch['shipping'] != 0 ? round($itemFetch['shipping'], 2) : '';

        $groups = array();
        $groups[] = array('id' => 1, 'label' => 1);
        $groups[] = array('id' => 2, 'label' => 2);
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

        $themeData['city_title_en'] = Lang($itemFetch['city_name'], 2, 'en');
        $themeData['city_title_ar'] = Lang($itemFetch['city_name'], 2, 'ar');

        $html = \iCms\UI::view('backend/seller/cities/edit-content');
        $data = array('status' => 200, 'html' => $html);
    }
}