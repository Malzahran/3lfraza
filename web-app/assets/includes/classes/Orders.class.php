<?php

namespace iCms;

use PDOException;

class Orders
{
    private $escapeObj;
    private $id;
    private $itemID;
    private $fid;
    private $features;
    private $uid;
    private $store_id;


    function __construct()
    {
        $this->escapeObj = new Escape();
        return $this;
    }

    public function registerOrder($data = array(), $userID = 0)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }
        if (isset($data['order_type'])) {
            try {
                $userID = (int)$userID;
                $type = (int)$data['order_type'];
                $content = (isset($data['order_content'])) ? $this->escapeObj->postEscape($data['order_content']) : '';
                $progress = (isset($data['order_progress'])) ? (int)$data['order_progress'] : 1;
                $store_id = (isset($data['order_store'])) ? (int)$data['order_store'] : 0;
                $notes = (isset($data['order_notes'])) ? $this->escapeObj->postEscape($data['order_notes']) : '';
                $name = (isset($data['name'])) ? $this->escapeObj->stringEscape($data['name']) : '';
                $phone = (isset($data['phone'])) ? $this->escapeObj->stringEscape($data['phone']) : '';
                $street = (isset($data['street'])) ? $this->escapeObj->stringEscape($data['street']) : '';
                $building = (isset($data['building'])) ? $this->escapeObj->stringEscape($data['building']) : '';
                $floor = (isset($data['floor'])) ? (int)$data['floor'] : 0;
                $apartment = (isset($data['apartment'])) ? $this->escapeObj->stringEscape($data['apartment']) : '';
                $additional = (isset($data['additional'])) ? $this->escapeObj->stringEscape($data['additional']) : '';
                $promo = (isset($data['promo'])) ? $this->escapeObj->stringEscape($data['promo']) : '';
                $date = (isset($data['date'])) ? $this->escapeObj->stringEscape($data['date']) : '';
                $arrival_time = (isset($data['time'])) ? $this->escapeObj->stringEscape($data['time']) : '';
                $time_slot = isset($data['time_slot']) ? (int)$data['time_slot'] : 0;
                $price = (isset($data['order_price'])) ? (float)$data['order_price'] : 0;
                $delivery_cost = (isset($data['order_delivery'])) ? (float)$data['order_delivery'] : 0;
                $added_by = (isset($data['order_added_by'])) ? (int)$data['order_added_by'] : 0;
                $lat = (isset($data['latitude'])) ? (double)$data['latitude'] : null;
                $lon = (isset($data['longitude'])) ? (double)$data['longitude'] : null;
                $city = (isset($data['city'])) ? (int)$data['city'] : 0;
                $city_group = (isset($data['city_group'])) ? (int)$data['city_group'] : 0;
                $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_ORDERS . "(type, progress, store_id, user_id, content, notes, user_name, user_phone, street, building, floor, apartment, additional, arrival_date, arrival_time, time_slot, promo_code, price, delivery, lat, lon, city, city_group, added_by, time) VALUES 
      (:type, :prg, :sid, :uid, :cnt, :nts, :unm, :uph, :ustr, :ubd, :ufl, :uaprt, :uadd, :date, :tm, :slot, :pcode, :pr, :dc, :lat, :lon, :ct, :cg, :ab, :time)");
                $time = time();
                $stmt->bindParam("type", $type);
                $stmt->bindParam("prg", $progress);
                $stmt->bindParam("sid", $store_id);
                $stmt->bindParam("uid", $userID);
                $stmt->bindParam("cnt", $content);
                $stmt->bindParam("nts", $notes);
                $stmt->bindParam("unm", $name);
                $stmt->bindParam("uph", $phone);
                $stmt->bindParam("ustr", $street);
                $stmt->bindParam("ubd", $building);
                $stmt->bindParam("ufl", $floor);
                $stmt->bindParam("uaprt", $apartment);
                $stmt->bindParam("uadd", $additional);
                $stmt->bindParam("date", $date);
                $stmt->bindParam("tm", $arrival_time);
                $stmt->bindParam("slot", $time_slot);
                $stmt->bindParam("pcode", $promo);
                $stmt->bindParam("pr", $price);
                $stmt->bindParam("dc", $delivery_cost);
                $stmt->bindParam("lon", $lon);
                $stmt->bindParam("lat", $lat);
                $stmt->bindParam("ct", $city);
                $stmt->bindParam("cg", $city_group);
                $stmt->bindParam("ab", $added_by);
                $stmt->bindParam("time", $time);
                $stmt->execute();
                return $this->getConnection()->lastInsertId();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        global $db;
        return $db;
    }

    public function editOrder($data = array())
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }
        if (isset($this->id)) {
            $content = (isset($data['order_content'])) ? $this->escapeObj->postEscape($data['order_content']) : '';
            $date = (isset($data['date'])) ? $this->escapeObj->stringEscape($data['date']) : '';
            $time = (isset($data['time'])) ? $this->escapeObj->stringEscape($data['time']) : '';
            $price = (isset($data['order_price'])) ? (float)$data['order_price'] : 0;
            $delivery_cost = (isset($data['order_delivery'])) ? (float)$data['order_delivery'] : 0;
            $notes = (isset($data['order_notes'])) ? $this->escapeObj->postEscape($data['order_notes']) : '';
            try {
                $stmt = $this->getConnection()->prepare("UPDATE " . DB_ORDERS . " SET content=:cnt,notes=:nts,arrival_date=:date,arrival_time=:tm,price=:pr,delivery=:dc WHERE order_id=:ord");
                $stmt->bindParam("cnt", $content);
                $stmt->bindParam("nts", $notes);
                $stmt->bindParam("date", $date);
                $stmt->bindParam("tm", $time);
                $stmt->bindParam("pr", $price);
                $stmt->bindParam("dc", $delivery_cost);
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function refuseOrder($data = array())
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }
        if (isset($this->id) && !empty($data['reason'])) {
            $reason = $this->escapeObj->postEscape($data['reason']);
            try {
                $stmt = $this->getConnection()->prepare("UPDATE " . DB_ORDERS . " SET progress=7,refuse_reasons=:reason WHERE order_id=:ord");
                $stmt->bindParam("reason", $reason);
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function deleteOrder()
    {
        $done = false;
        if (isset($this->id)) {
            $utiObj = new Utilities();
            $itemFetch = $utiObj->getOrder($this->id);
            if (isset($itemFetch['order_id'])) {
                try {
                    $stmt = $this->getConnection()->prepare("DELETE FROM " . DB_ORDERS . " WHERE order_id=:ord");
                    $stmt->bindParam("ord", $this->id);
                    $stmt->execute();
                    $cont = ($stmt->rowCount() > 0) ? true : false;
                } catch (PDOException $ex) {
                    $cont = false;
                }
                if ($cont && ($itemFetch['type'] == 1 || $itemFetch['type'] == 4)) {
                    $itemsFetch = $utiObj->getOrderItems($this->id);
                    if (!empty($itemsFetch)) {
                        try {
                            foreach ($itemsFetch as $k => $v) {
                                if ($v['ft_id'] != 0) {
                                    $stmt = $this->getConnection()->prepare("DELETE FROM " . DB_ORDER_ITEMS_FT . " WHERE id=:fid OR p_id=:fid");
                                    $stmt->bindParam("fid", $v['ft_id']);
                                    $stmt->execute();
                                    $cont = ($stmt->rowCount() > 0) ? true : false;
                                } else $cont = true;
                            }
                            if ($cont) {
                                $stmt = $this->getConnection()->prepare("DELETE FROM " . DB_ORDER_ITEMS . " WHERE order_id=:ord");
                                $stmt->bindParam("ord", $this->id);
                                $stmt->execute();
                                $done = ($stmt->rowCount() > 0) ? true : false;
                            }
                        } catch (PDOException $ex) {
                            $done = false;
                        }
                    }
                }
                if ($done) return true;
            }
        }
        return false;
    }

    public function registerOrderItems($data = array())
    {
        global $isLogged;
        if (!$isLogged) return false;
        if (isset($data['order_id']) && isset($data['item_id']) && isset($data['qty'])) {
            try {
                $order_id = (int)$data['order_id'];
                $item_id = (int)$data['item_id'];
                $ft_id = (int)$data['ft_id'];
                $qty = (int)$data['qty'];
                $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_ORDER_ITEMS . "(order_id, item_id, ft_id, qty) VALUES (:ord, :iid, :fid, :qty)");
                $stmt->bindParam("ord", $order_id);
                $stmt->bindParam("iid", $item_id);
                $stmt->bindParam("fid", $ft_id);
                $stmt->bindParam("qty", $qty);
                $stmt->execute();
                return $stmt ? $this->getConnection()->lastInsertId() : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function updateOrdTotal($ordID = 0, $total = 0, $discount = 0)
    {
        if ($ordID != 0 && $total != 0) {
            $ordID = (int)$ordID;
            $total = (float)$total;
            try {
                $stmt = $this->getConnection()->prepare("UPDATE " . DB_ORDERS . " SET price=:pr,discount=:dsc WHERE order_id = :ord");
                $stmt->bindParam("pr", $total);
                $stmt->bindParam("dsc", $discount);
                $stmt->bindParam("ord", $ordID);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function InsertFeatures()
    {
        if (!empty($this->features)) {
            try {
                $done = false;
                $pID = 0;
                $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_ORDER_ITEMS_FT . "(p_id,ft_id) VALUES (0 ,0)");
                $stmt->execute();
                if ($stmt) {
                    $pID = $this->getConnection()->lastInsertId();
                    foreach ($this->features as $k => $v) {
                        $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_ORDER_ITEMS_FT . "(p_id,ft_id) VALUES (:pid ,:fid)");
                        $stmt->bindParam("pid", $pID);
                        $stmt->bindParam("fid", $v['id']);
                        $stmt->execute();
                        $done = $stmt;
                    }
                }
                return $done ? $pID : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrderItemFeature()
    {
        if (!empty($this->id) && !empty($this->fid)) {
            try {
                $stmt = $this->getConnection()->prepare("UPDATE " . DB_ORDER_ITEMS . " SET ft_id=:fid WHERE id=:id");
                $stmt->bindParam("fid", $this->fid);
                $stmt->bindParam("id", $this->id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrdDelivery($ordID = 0, $delvid = 0)
    {
        if ($ordID != 0 && $delvid != 0) {
            $ordID = (int)$ordID;
            $delvid = (int)$delvid;
            $userDObj = new User();
            $userInfo = $userDObj->getById($delvid);
            if (isset($userInfo['id'])) {
                $more = "";
                try {
                    $stmt = $this->getConnection()->prepare("UPDATE " . DB_ORDERS . " SET delv_id=:dlv $more WHERE order_id = :ord");
                    $stmt->bindParam("dlv", $delvid);
                    $stmt->bindParam("ord", $ordID);
                    $stmt->execute();
                    if ($stmt) {
                        $ntfObj = new \iCms\Notifications();
                        $notarray = array('rid' => $ordID, 'usid' => $delvid, 'type' => 5, 'state' => 1);
                        if ($ntfObj->registerNotif($notarray)) return true;
                    }
                } catch (PDOException $ex) {
                    return false;
                }
            }
        }
        return false;
    }

    public function setOrdWorker($ordID = 0, $workerid = 0)
    {
        if ($ordID != 0 && $workerid != 0) {
            $ordID = (int)$ordID;
            $workerid = (int)$workerid;
            $userDObj = new User();
            $userInfo = $userDObj->getById($workerid);
            if (isset($userInfo['id'])) {
                try {
                    $stmt = $this->getConnection()->prepare("UPDATE " . DB_ORDERS . " SET worker_id=:wrk,progress=3 WHERE order_id = :ord");
                    $stmt->bindParam("wrk", $workerid);
                    $stmt->bindParam("ord", $ordID);
                    $stmt->execute();
                    if ($stmt) {
                        $ntfObj = new \iCms\Notifications();
                        $notarray = array('rid' => $ordID, 'usid' => $workerid, 'type' => 5, 'state' => 1);
                        if ($ntfObj->registerNotif($notarray)) return true;
                    }
                } catch (PDOException $ex) {
                    return false;
                }
            }
        }
        return false;
    }

    public function setItemsInventory($data = array())
    {
        $done = false;
        if (!empty($data)) {
            foreach ($data as $v) {
                try {
                    $stmt = $this->getConnection()->prepare("UPDATE " . DB_STORE_ITEMS . " SET inventory = inventory - (:qty) WHERE id=:id");
                    $stmt->bindParam("qty", $v['qty']);
                    $stmt->bindParam("id", $v['id']);
                    $stmt->execute();
                    $done = ($stmt->rowCount() > 0) ? true : false;
                } catch (PDOException $ex) {
                    $done = false;
                }
            }
            if ($done) {
                return true;
            }
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function setItemId($iid)
    {
        $this->itemID = (int)$iid;
    }

    public function setFtId($ft_id)
    {
        $this->fid = (int)$ft_id;
    }

    public function setFeatures($ft)
    {
        if (!empty($ft) && is_array($ft)) $this->features = $ft;
    }

    public function setUserId($uid)
    {
        $this->uid = (int)$uid;
    }

    public function setStoreId($sid)
    {
        $this->store_id = (int)$sid;
    }
}