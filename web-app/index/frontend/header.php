<?php
$themeData['header'] = isset($_GET['tab1']) && $_GET['tab1'] == 'home' ? \iCms\UI::view('header/content-home') :  \iCms\UI::view('header/content');