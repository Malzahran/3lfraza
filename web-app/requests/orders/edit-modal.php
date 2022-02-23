<?php
$data = array();
if (isset($_POST['item_id'])) {
    $itemID = (int)$_POST['item_id'];
    $itemFetch = $utiObj->getOrder($itemID);
    if (!empty($itemFetch)) {
        $themeData['order_id'] = $itemFetch['order_id'];
        $themeData['order_date'] = $itemFetch['arrival_date'];
        $themeData['order_time'] = $itemFetch['arrival_time'];
        $themeData['order_cost'] = round($itemFetch['price'], 2);
        $themeData['order_delivery'] = round($itemFetch['delivery'], 2);
        $breaks = array("<br />", "<br>", "<br/>");
        $content = str_ireplace($breaks, "\r\n", $itemFetch['content']);
        $themeData['order_desc'] = $content;

        $html = \iCms\UI::view('backend/orders/edit-content');
        $data = array(
            'status' => 200,
            'html' => $html
        );
    }

}