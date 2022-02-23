<?php
$themeData['year'] = date('Y');
$themeData['footer'] = ($isLogged) ? \iCms\UI::view('backend/footer/content') : '';