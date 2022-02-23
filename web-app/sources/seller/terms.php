<?php
// Check For User Login
if (!$sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
// Check For Administration Permissions
$cont = ((@$seller['system'] || @$seller['moderator'])) ? true : false;
if (!$cont) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}
// Set Page BreadCrumbs
$themeData['page_breadcrumb'] .= '<li><span>' . Lang('terms_label') . '</span></li>';

//* Start Set BreadCrumbs And Sidepanel Link Active Arr. (Java)	
$themeData['footer_jquery_bc'] = '<script type="text/javascript">
	$("#terms_sp").addClass( "active" );
	</script>';
// End Set BreadCrumbs And Sidepanel Link Active Arr. (Java) *\\

//* Start Assigning PHP Variables,Jquery And CSS According To Administration Permission 
$themeData['view_title'] = Lang('terms_label');
$themeData['site_title'] .= ' - ' . Lang('terms_label');
$themeData['page_modals'] = '';
if (@$seller['system'] || @$seller['moderator']) {
    $inputLang = getLangcode();
    $themeData['view_inputs'] = '';
    if (!empty($inputLang)) {
        foreach ($inputLang as $key => $value) {
            $fetch = $utiObj->getMiscInfo('terms', $value['langcode']);
            $ItemData = (!empty($fetch)) ? $fetch : array('title' => '', 'content' => '');
            if (count($inputLang) == 1) {
                $themeData['lang_name'] = Lang('terms_label');
            } else {
                $themeData['lang_name'] = Lang($value['langcode'] . '_lang');
            }
            $themeData['input_required'] = 'required';
            $themeData['input_lang_code'] = $value['langcode'];
            $themeData['view_inputs'] .= \iCms\UI::view('backend/global/inputs/legend-lang');
            if (count($inputLang) == 1) {
                $themeData['input_label'] = Lang('des_label');
            } else {
                $themeData['input_label'] = Lang('des_in_lang') . Lang($value['langcode'] . '_lang');
            }
            $themeData['input_value'] = htmlspecialchars_decode(stripslashes($ItemData['content']));
            $themeData['input_name'] = 'desc';
            $themeData['input_class'] = 'ckeditor';
            $themeData['input_rows'] = '5';
            $themeData['view_inputs'] .= \iCms\UI::view('backend/global/inputs/desc-lang');
        }
    }
    $themeData['footer_scripts_vn'] .= '
	<script src="' . $config['theme_url'] . '/backend/vendor/ckeditor/ckeditor.js"></script>
	<script type="text/javascript">	CKEDITOR.config.customConfig = ' . "'" . $config['theme_url'] . '/backend/vendor/ckeditor/basic-config.js' . "'" . '</script>';
    $themeData['save_btn_label'] = Lang('save_changes_btn');
    $themeData['save_button'] = \iCms\UI::view('backend/global/buttons/save-btn');
    $themeData['footer_scripts_p'] .= ' 
		<script src="' . $config['theme_url'] . '/backend/jquery/misc/terms.js" type="text/javascript"></script>';
}
// End Assigning PHP Variables,Jquery And CSS According To Administration Permission *\\  

// Assigning The Main View Template
$themeData['view_content'] = \iCms\UI::view('backend/global/inputs-view/view');
$themeData['page_content'] = \iCms\UI::view('backend/global/inputs-view/content');