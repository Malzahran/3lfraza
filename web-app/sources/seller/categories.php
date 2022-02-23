<?php
if (!$sellerLogged && !$aCATS && (!@!$seller['system'] || !@$seller['moderator'])) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
$themeData['page_header_title'] = Lang('seller_cats_label');
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('seller_cats_label') . '</span></li>';
$themeData['page_header'] = \iCms\UI::view('backend/header/page_header');
$themeData['site_title'] .= ' - ' . Lang('seller_items_label');
$themeData['footer_jquery_bc'] = '<script type="text/javascript">$("#seller_cats_sp").addClass( "active" );</script>';
if (@$seller['system'] || @$seller['moderator']) {
    $themeData['more_add_modal'] = '';
    $themeData['group_select'] = '';
    $groups = array();
    $groups[] = array('id' => 1, 'label' => 1);
    $groups[] = array('id' => 2, 'label' => 2);
    $groups[] = array('id' => 3, 'label' => Lang('all_label'));
    if (!empty($groups)) {
        $themeData['select_items'] = '';
        foreach ($groups as $k => $v) {
            if (isset($v['id'])) {
                $themeData['sel_id'] = $v['id'];
                $themeData['sel_name'] = $v['label'];
                $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
            }
        }
        $themeData['group_select'] .= $themeData['select_items'];
    }
    $themeData['more_add_modal'] .= \iCms\UI::view('backend/seller/categories/group-sel');

    $themeData['type_select'] = '';
    $type = array();
    $type[] = array('id' => 1, 'label' => Lang('seller_cat_type_1'));
    $type[] = array('id' => 4, 'label' => Lang('seller_cat_type_4'));
    if (!empty($type)) {
        $themeData['select_items'] = '';
        foreach ($type as $k => $v) {
            if (isset($v['id'])) {
                $themeData['sel_id'] = $v['id'];
                $themeData['sel_name'] = $v['label'];
                $themeData['select_items'] .= \iCms\UI::view('backend/global/select/selectopt');
            }
        }
        $themeData['type_select'] .= $themeData['select_items'];
    }
    $themeData['more_add_modal'] .= \iCms\UI::view('backend/seller/categories/type-sel');
    $themeData['add_new_btn'] = \iCms\UI::view('backend/global/buttons/add-btn');
    $themeData['page_content'] = \iCms\UI::view('backend/seller/categories/content');
    $themeData['page_modals'] = \iCms\UI::view('backend/seller/categories/view-modal');
    $themeData['header_stylesheet_page'] = '
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/css/jquery.dataTables.min.css" rel="stylesheet" media="screen">
    <link href="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/responsive.dataTables.min.css" rel="stylesheet" media="screen">';
    $themeData['footer_scripts_p'] = '
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/js/jquery.dataTables.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/DataTable/responsive/dataTables.responsive.min.js"></script>
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/categories/view.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/categories/add.js"></script>
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/categories/edit.js"></script>';
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/categories/add-modal');
    $themeData['page_modals'] .= \iCms\UI::view('backend/seller/categories/edit-modal');
}
if (@$seller['system']) {
    $themeData['page_modals'] .= \iCms\UI::view('backend/global/modals/delete-modal');
    $themeData['footer_jquery_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/jquery/seller/categories/del.js"></script>';
}