<?php
if (isset($data->buyer) && isset($data->buyer->name) && isset($data->buyer->email) && isset($data->buyer->phone) && isset($data->buyer->street) && isset($data->buyer->building) && isset($data->buyer->floor) && isset($data->buyer->apartment) && isset($data->buyer->time_slot)) {
    $cont = false;
    $street = $data->buyer->street;
    $building = $data->buyer->building;
    $floor = $data->buyer->floor;
    $apartment = $data->buyer->apartment;
    $additional = $data->buyer->additional;
    $slot_id = $data->buyer->time_slot;
    $current_slot = $utiObj->getTimeSlot(date('H:i', time()));
    if (isset($current_slot[0]['id'])) {
        $current_slot = $current_slot[0];
        $get_slots = $utiObj->getTimeSlot();
        if (!empty($get_slots)) {
            foreach ($get_slots as $slot) {
                if ($slot['type'] == 1 && $slot_id == $slot['id']) {
                    $day = strtotime(date("Y-m-d") . "+ " . ($current_slot['type'] == 2 || $current_slot['id'] > $slot['id'] ? 2 : 1) . " days");
                    $day = date("Y-m-d", $day);
                    $arrival_time = $slot['start'];
                }
            }
        }
    }
    $promo = $escapeObj->stringEscape($data->buyer->promo_code);
    $promoInfo = $utiObj->getPromoCode($promo);
    $phone = $data->buyer->phone;
    $usersObj = new \iCms\Users();
    $usersObj->setUserId($user['id']);
    $usersObj->setName($user['name']);
    $usersObj->setUsername($user['username']);
    $usersObj->setPhone($phone);
    $usersObj->setEmail($user['email']);
    $usersObj->setStreet($street);
    $usersObj->setBuilding($building);
    $usersObj->setFloor($floor);
    $usersObj->setApartment($apartment);
    $usersObj->setAdditional($additional);
    if ($usersObj->edit($user['active'])) $cont = true;
    if ($cont && isset($day) && isset($arrival_time)) {
        $cartObj->setUserId($user['id']);
        $cartGroup = $cartObj->getCartGroup();
        if (!empty($cartGroup)) {
            $ordObj = new \iCms\Orders();
            $ntfObj = new \iCms\Notifications();
            $qtyIDS = array();
            foreach ($cartGroup as $k => $v) {
                $storeInfo = $utiObj->getStore($v['store_id']);
                if (isset($storeInfo['store']['store_id'])) {
                    $storeID = $v['store_id'];
                    $discount = $totalCost = $delvCost = 0;
                    $type = ($storeInfo['store']['delivery'] == 1) ? 1 : 4;
                    $inv = ($storeInfo['store']['inventory'] == 1) ? true : false;
                    $current_city = $utiObj->getCity($user['current_city']);
                    if (isset($current_city['city_id'])) $delvCost = $current_city['shipping'];
                    $cartObj->setStoreId($storeID);
                    $cartFetch = $cartObj->getCartByStoreId();
                    if (!empty($cartFetch)) {
                        $content = array();
                        $content['name'] = $data->buyer->name;
                        $content['email'] = $data->buyer->email;
                        $content['promo'] = $promo;
                        $content['street'] = $street;
                        $content['building'] = $building;
                        $content['floor'] = $floor;
                        $content['apartment'] = $apartment;
                        $content['additional'] = $additional;
                        $content['phone'] = $phone;
                        $content['date'] = $day;
                        $content['time'] = $arrival_time;
                        $content['time_slot'] = $slot_id;
                        $content['latitude'] = $data->user->latitude;
                        $content['longitude'] = $data->user->longitude;
                        $content['city'] = $data->user->city;
                        $content['city_group'] = $data->user->city_group;
                        $content['order_type'] = $type;
                        $content['order_store'] = $storeID;
                        $content['order_delivery'] = $delvCost;
                        $content['order_notes'] = (isset($data->buyer->comment)) ? $data->buyer->comment : '';
                        $ordID = $ordObj->registerOrder($content, $user['id']);
                        if ($ordID) {
                            foreach ($cartFetch as $ke => $va) {
                                $itemInfo = $utiObj->getItem($va['item_id']);
                                if (isset($itemInfo['id'])) {
                                    $features = array();
                                    $id = $itemInfo['id'];
                                    $qty = $va['qty'];
                                    if ($va['ft_id'] != 0) {
                                        $cartObj->setFtId($va['ft_id']);
                                        $cartFtFetch = $cartObj->getCartFeatures();
                                        if (!empty($cartFtFetch))
                                            foreach ($cartFtFetch as $cft) {
                                                $ftFetch = $utiObj->getItemFeatures(0, $cft['ft_id']);
                                                if (isset($ftFetch['id'])) {
                                                    $itemInfo['price'] += isset($ftFetch['price']) ? $ftFetch['price'] : 0;
                                                    $features[] = array('id' => $ftFetch['id']);
                                                }
                                            }
                                    }
                                    if ($inv && !$itemInfo['stock']) $qtyIDS[] = array('id' => $id, 'qty' => $qty);
                                    $totalCost += $itemInfo['price'] * $qty;
                                    $orderitem = array(
                                        'order_id' => $ordID,
                                        'item_id' => $id,
                                        'ft_id' => 0,
                                        'qty' => $qty);
                                    $reg = $ordObj->registerOrderItems($orderitem);
                                    $cont = false;
                                    if ($reg && !empty($features)) {
                                        $ordObj->setFeatures($features);
                                        $regFT = $ordObj->InsertFeatures();
                                        if ($regFT) {
                                            $ordObj->setId($reg);
                                            $ordObj->setFtId($regFT);
                                            $cont = $ordObj->setOrderItemFeature();
                                        }
                                    } else if ($reg && empty($features)) $cont = true;
                                }
                            }
                            if ($cont) {
                                if ($promoInfo) {
                                    if ($promoInfo['type'] == 1)
                                        $discount = ($totalCost * $promoInfo['value']) / 100;
                                    else $discount = $promoInfo['value'];
                                }
                                $cont = ($ordObj->updateOrdTotal($ordID, $totalCost, $discount)) ? true : false;
                                if ($cont) {
                                    $notarray = array('rid' => $ordID, 'stid' => $storeID, 'type' => 5, 'state' => 1);
                                    $cont = ($ntfObj->registerNotif($notarray)) ? true : false;
                                }
                            }
                        }
                    }

                }
            }
            if ($cont) {
                if (!empty($qtyIDS)) $done = ($ordObj->setItemsInventory($qtyIDS)) ? true : false;
                else $done = true;
            }
        }
        if ($done) {
            $clearCart = ($cartObj->ClearCart()) ? true : false;
            if ($clearCart) {
                $response['notes'] = Lang('success_order_msg') . ' 🙂';
                $response['message'] = isset($ordID) ? $ordID : '';
                $response['result'] = 'success';
            }
        }
    }
}