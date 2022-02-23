<?php
$items = array();
$itemID = (int)$data->misc->itemid;
$allowbuttons = 0;
$buttonst = array();
$sAdmin = false;
if (@$seller['system'] || @$seller['moderator']) {
    $sAdmin = true;
    $allowbuttons = 1;
    $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'features', 'reqatype' => 'add_form', 'reqptype' => 'add_feature', 'reqid' => $itemID);
    $buttonst[] = array(
        'id' => $btnid += 1,
        'place' => 2,
        'color' => 2,
        'text' => Lang('add_features_btn'),
        'intentData' => $intentdata
    );
}
$fetch = $utiObj->getItemFeatures($itemID);
if (!empty($fetch)) {
    foreach ($fetch as $k => $v) {
        $id = $v['id'];
        $itemData = $utiObj->getItemFeaturesInfo($id);
        $catInfo = $utiObj->getStoreCats($v['category']);
        $buttons = array();
        $price = round($v['price'], 2) . ' ' . Lang('currency_1');
        $dscPrice = $v['dsc_price'] != 0 ? Lang('price_dsc_label') . ' : ' . round($v['dsc_price'], 2) . ' ' . Lang('currency_1') : '';
        if ($sAdmin) {
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'features', 'reqatype' => 'edit_form', 'reqptype' => 'edit_feature', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 2,
                'text' => Lang('edit_btn'),
                'intentData' => $intentdata
            );
            $intentdata = array('intent' => 6, 'reqtype' => 'seller_items', 'reqstype' => 'features', 'reqatype' => 'delete_confirm', 'reqptype' => 'delete_feature', 'reqid' => $id);
            $buttons[] = array(
                'id' => $btnid += 1,
                'color' => 1,
                'text' => Lang('delete_btn'),
                'intentData' => $intentdata
            );
        }
        $items[] = array(
            'title' => $itemData['title'],
            'id' => $id,
            'buttons' => $buttons,
            'minfo2' => isset($catInfo['category_id']) ? Lang($catInfo['category_name'], 3) : '',
            'minfo' => $dscPrice,
            'state' => $price,
            'click' => 0);
    }
}
$layout = array(
    'layout' => 2,
    'indvclk' => 1,
    'allowback' => 1,
    'barback' => 1,
    'allowmore' => 0,
    'allowbuttons' => $allowbuttons,
    'searchtype' => 2,
    'allowsearch' => 0,
    'orientation' => 0,
    'refresh' => 1,
    'showicon' => 1,
    'bartitle' => 1,
    'abtitle' => Lang('features_label')
);
$response["result"] = "success";
$response["items"] = $items;
$response["layout"] = $layout;
$response["layout"]["buttons"] = $buttonst;