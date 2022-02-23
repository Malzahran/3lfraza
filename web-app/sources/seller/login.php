<?php
if ($sellerLogged) {
    header('Location: ' . smoothLink('index.php?tab1=admin'));
}

$themeData['body_class'] = 'login';
$themeData['site_title'] .= ' - ' . Lang('login_label');
$themeData['page_content'] = \iCms\UI::view('backend/login/content');
$themeData['footer_scripts_p'] .= '
    <script src="' . $config['theme_url'] . '/backend/vendor/jquery-validation/jquery.validate.min.js"></script>';
$themeData['footer_jquery_p'] .= '
<script src="' . $config['theme_url'] . '/js/login.js"></script>
		<script>
			jQuery(document).ready(function() {
				Main.init();
			});
		</script>';
$_SESSION[$config['spf'] . 'redirect_url'] = smoothLink('index.php?tab1=admin');
unset($_SESSION[$config['spf'] . 'admin_login']);
$_SESSION[$config['spf'] . 'seller_login'] = true;