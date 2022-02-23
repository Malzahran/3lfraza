<?php
administrationOnly();
$usersObj = new \iCms\Users();
$userDObj = new \iCms\User();
include('requests/users/' . $a . '.php');