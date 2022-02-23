<?php
$items = array();
$id = 0;
$intentData = array('intent' => 1, 'reqtype' => 'orders', 'reqstype' => 'find_orders');
$items[] = array(
    'title2' => Lang('find_orders_label'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/find_orders.png',
    'intentData' => $intentData);
$intentData = array('intent' => 1, 'reqtype' => 'orders', 'reqstype' => 'getorders');
$items[] = array(
    'title2' => Lang('orders_label'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/orders.jpg',
    'intentData' => $intentData);
$intentData = array('intent' => 9);
$items[] = array(
    'title2' => Lang('my_profile_label'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/profile.jpg',
    'intentData' => $intentData);
$intentData = array('intent' => 55);
$items[] = array(
    'title2' => Lang('logout_label'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/logout.jpg',
    'intentData' => $intentData);
$intentData = array('intent' => 54);
$items[] = array(
    'title2' => Lang('change_language'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/language.jpg',
    'intentData' => $intentData);
$layout = array('layout' => 1, 'coloumn' => 3, 'cimg' => 0, 'indvclk' => 1);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;