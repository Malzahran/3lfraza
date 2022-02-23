<?php
$items = array();
$id = 0;
$intentData = array('intent' => 1, 'reqtype' => 'orders', 'reqstype' => 'getorders');
$items[] = array(
    'title2' => Lang('orders_label'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/store_orders.jpg',
    'intentData' => $intentData);
if ($aCATS) {
    $intentData = array('intent' => 1, 'reqtype' => 'seller_cats', 'reqstype' => 'get');
    $items[] = array(
        'title2' => Lang('seller_cats_label'),
        'id' => $id += 1,
        'image_url' => $site_url . '/media/cats.jpg',
        'intentData' => $intentData);
}
$intentData = array('intent' => 1, 'reqtype' => 'seller_items', 'reqstype' => 'get');
$items[] = array(
    'title2' => Lang('seller_items_label'),
    'id' => $id += 1,
    'image_url' => $site_url . '/media/items.jpg',
    'intentData' => $intentData);
if (@$seller['system'] || @$seller['moderator']) {
    if ($aDLV) {
        $intentData = array('intent' => 1, 'reqtype' => 'seller_delivery', 'reqstype' => 'get');
        $items[] = array(
            'title2' => Lang('delivery_agent_label'),
            'id' => $id += 1,
            'image_url' => $site_url . '/media/orders.jpg',
            'intentData' => $intentData);
        $intentData = array('intent' => 3, 'reqtype' => 'maps', 'reqstype' => 'getall');
        $items[] = array(
            'title2' => Lang('delivery_tracking_label'),
            'id' => $id += 1,
            'image_url' => $site_url . '/media/dir.jpg',
            'intentData' => $intentData);
    }
    $intentData = array('intent' => 1, 'reqtype' => 'seller_users', 'reqstype' => 'get');
    $items[] = array(
        'title2' => Lang('users_label'),
        'id' => $id += 1,
        'image_url' => $site_url . '/media/users.jpg',
        'intentData' => $intentData);
    $intentData = array('intent' => 1, 'reqtype' => 'seller_workers', 'reqstype' => 'get');
    $items[] = array(
        'title2' => Lang('workers_label'),
        'id' => $id += 1,
        'image_url' => $site_url . '/media/workers.png',
        'intentData' => $intentData);
}
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
$layout = array('layout' => 1, 'coloumn' => 3, 'cimg' => 1, 'indvclk' => 1);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;