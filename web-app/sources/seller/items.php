<?php
if (!$sellerLogged && (!@$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['allowed_inventory'] = $aINV ? 1 : 0;
$themeData['allowed_category'] = $aCATS ? 1 : 0;
$themeData['inventory_th'] = $aINV ? '<th>' . Lang('inventory_label') . '</th>' : '';
$themeData['category_th'] = $aCATS ? '<th>' . Lang('category_label') . '</th>' : '';
$themeData['page_header_title'] = Lang('seller_items_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('seller_items_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('seller_items_label');
$themeData['footer_jquery_bc'] = '<script type="text/javascript">$("#seller_items_sp").addClass( "active" );</script>';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['more_add_modal'] = '';
    if ($aCATS) {
        $storeID = $user['store_id'];
        $themeData['cat_select'] = '<option value="">' . Lang('select_category') . '</option>';
        $pcats = $utiObj->getStoreCats(0, $storeID);
        if (!empty($pcats)) {
            foreach ($pcats as $k => $v) {
                $mcat = $utiObj->getStoreMainCats($v['category_id']);
                $themeData['select_items'] = '';
                if (!empty($mcat)) {
                    foreach ($mcat as $key => $value) {
                        if ($value['has_sub'] == 1) {
                            $scat = $utiObj->getStoreSubCat($value['category_id']);
                            if (!empty($scat)) {
                                foreach ($scat as $ke => $val) {
                                    $themeData['sel_id'] = $val['category_id'];
                                    $themeData['sel_name'] = Lang($val['category_name'], 3) . ' / ' . Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3);
                                    $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                                }
                            }
                        } else {
                            $themeData['sel_id'] = $value['category_id'];
                            $themeData['sel_name'] = Lang($value['category_name'], 3) . ' / ' . Lang($v['category_name'], 3);
                            $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                        }
                    }

                } else if ($v['has_sub'] == 2) {
                    $themeData['sel_id'] = $v['category_id'];
                    $themeData['sel_name'] = Lang($v['category_name'], 3);
                    $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
                }
                $themeData['cat_select'] .= $themeData['select_items'];
            }
        }
        $themeData['more_add_modal'] .= \iCms\UI::view('backend/seller/items/cats-sel');
    }
    if ($aFeatures) {
        $themeData['feat_select'] = '<option value="">' . Lang('select_category') . '</option>';
        $featureCats = $utiObj->getStoreCats(0, $storeID, array(4));
        if (!empty($featureCats)) {
            foreach ($featureCats as $k => $v) {
                $themeData['sel_id'] = $v['category_id'];
                $themeData['sel_name'] = Lang($v['category_name'], 3);
                $themeData['feat_select'] .= \iCms\UI::view('backend/global/select/selectopt');
            }
        }
    }
    $themeData['pr_required'] = $aFeatures ? '' : 'required';
    if ($aINV == 1) {
        $themeData['more_add_modal'] .= \iCms\UI::view('backend/seller/items/inv-input');
    }
    $themeData['page_content'] = \iCms\UI::view('backend/seller/items/content');
    $themeData['page_modals'] = \iCms\UI::view('backend/seller/items/view-modal');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/items/view.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/items/add.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/items/edit.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/items/active.js"></script>';
    $themeData['footer_jquery_p'] .= ($aFeatures) ? '<script src="' . $config['theme_url'] . '/backend/jquery/seller/items/features.js"></script>' : '';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/items/add-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/items/edit-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/activate-modal');
    $themeData['page_modals'] .= ($aFeatures) ? \iCms\UI::view('backend/seller/items/features-modal') : '';
}
if (@$seller['system']) {
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/delete-modal');
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/items/del.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/feat-modal');
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/items/feat.js"></script>';
}