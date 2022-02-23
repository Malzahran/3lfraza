<?php
$gps = $reqphone = 0;
$loginObj = new \iCmsAPI\LoginControl();
$userObj = new \iCms\User();
include('api/login/' . $parenttype . '.php');