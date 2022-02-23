<?php
$id = 0;
$items = array();
$intentData = array('intent' => 9);
$items[] = array(
    'title2' => Lang('my_profile_label'),
    'id' => $id++,
    'image_url' => $site_url . '/media/profile.jpg',
    'intentData' => $intentData);
$intentData = array('intent' => 55);
$items[] = array(
    'title2' => Lang('logout_label'),
    'id' => $id++,
    'image_url' => $site_url . '/media/logout.jpg',
    'intentData' => $intentData);
$layout = array('layout' => 1, 'coloumn' => 3, 'cimg' => 1, 'indvclk' => 1);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;