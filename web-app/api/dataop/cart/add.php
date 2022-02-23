<?php
$itemID = (int)$data->misc->product_id;
$qty = (int)$data->misc->qty;
$ftId = (int)$data->misc->ftid;
$ftList = json_decode(json_encode($data->misc->features), true);
$features = !empty($ftList);
$ft = $checkCart = array();
$itemInfo = $utiObj->getItem($itemID);
if (isset($itemInfo['id']) && !$itemInfo['stock'] && $itemInfo['inventory'] >= $qty) {
    $done = false;
    $cartObj->setId($itemID);
    $cartObj->setUserId($user['id']);
    $cartObj->setStoreId($itemInfo['store_id']);
    if ($features) {
        foreach ($ftList as $k => $v) {
            $ft[] = $v['id'];
        }
        $cartFt = $cartObj->getCartFtIds();
        if ($cartFt) {
            foreach ($cartFt as $cf) {
                $count = 0;
                $cartObj->setFtId($cf['ft_id']);
                $ftCheck = $cartObj->CheckItemByFeature();
                if ($ftCheck) {
                    foreach ($ftCheck as $chk) {
                        if ($cf['ft_id'] == $chk['p_id'])
                            if (in_array($chk['ft_id'], $ft)) $count++;
                    }
                    if ($count == count($ftCheck) && $count == count($ft)) $checkCart['ft_id'] = $cf['ft_id'];
                }
            }
        }
    } else {
        $cartObj->setFtId($ftId);
        $checkCart = $cartObj->CheckItem();
    }
    if (isset($checkCart['ft_id'])) {
        $cartObj->setFtId($checkCart['ft_id']);
        $done = $cartObj->UpdateItem($qty);
    } else {
        $cartObj->setFtId($ftId);
        $cont = $cartObj->InsertItem($qty);
        if ($cont)
            if ($features) {
                $cartObj->setFeatures($ftList);
                $cartObj->setId($cont);
                $cont = $cartObj->InsertFeatures();
                if ($cont) {
                    $cartObj->setFtId($cont);
                    $done = $cartObj->setCartFeature();
                }
            } else $done = true;
    }
    if ($done) $response["result"] = "success";
} else $response["message"] = Lang('out_of_stock');