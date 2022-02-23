<?php

namespace iCms;

use PDO;
use PDOException;

class Cart
{
    private $escapeObj;
    private $id;
    private $fid;
    private $features;
    private $uid;
    private $store_id;


    function __construct()
    {
        $this->escapeObj = new Escape();
        return $this;
    }

    public function getCartGroup()
    {
        if (isset($this->uid)) {
            try {
                $stmt = $this->getConnection()->prepare("SELECT DISTINCT store_id FROM " . DB_STORE_CART . " WHERE user_id = :uid");
                $stmt->bindParam("uid", $this->uid);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
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

    public function getCartByStoreId($count = false)
    {
        if (isset($this->uid) && isset($this->store_id)) {
            $select = $count ? "count(item_id) as count" : "item_id,ft_id,qty";
            try {
                $stmt = $this->getConnection()->prepare("SELECT $select FROM " . DB_STORE_CART . " WHERE store_id = :sid AND user_id = :uid");
                $stmt->bindParam("sid", $this->store_id);
                $stmt->bindParam("uid", $this->uid);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function getCartFeatures()
    {
        if (isset($this->fid)) {
            try {
                $stmt = $this->getConnection()->prepare("SELECT ft_id FROM " . DB_STORE_CART_FT . " WHERE p_id = :fid");
                $stmt->bindParam("fid", $this->fid);
                $stmt->execute();
                return $stmt->rowCount() > 0 ? $stmt->fetchAll(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function CheckItem()
    {
        if (isset($this->id) && isset($this->uid) && isset($this->store_id) && isset($this->fid)) {
            try {
                $stmt = $this->getConnection()->prepare("SELECT id,qty,ft_id FROM " . DB_STORE_CART . " WHERE store_id = :sid AND item_id = :id AND ft_id = :fid AND user_id = :uid");
                $stmt->bindParam("sid", $this->store_id);
                $stmt->bindParam("id", $this->id);
                $stmt->bindParam("fid", $this->fid);
                $stmt->bindParam("uid", $this->uid);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function getCartFtIds()
    {
        if (isset($this->id) && isset($this->uid) && isset($this->store_id)) {
            try {
                $stmt = $this->getConnection()->prepare("SELECT ft_id FROM " . DB_STORE_CART . " WHERE store_id = :sid AND item_id = :id AND ft_id != :fid AND user_id = :uid");
                $stmt->execute(array(':sid' => $this->store_id, ':id' => $this->id, ':fid' => 0, ':uid' => $this->uid));
                return $stmt->rowCount() > 0 ? $stmt->fetchAll(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function CheckItemByFeature()
    {
        if (isset($this->fid)) {
            try {
                $stmt = $this->getConnection()->prepare("SELECT p_id,ft_id FROM " . DB_STORE_CART_FT . " WHERE p_id = :id");
                $stmt->execute(array(':id' => $this->fid));
                return $stmt->rowCount() > 0 ? $stmt->fetchAll(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function CheckWholeItem()
    {
        if (isset($this->id) && isset($this->uid) && isset($this->store_id)) {
            try {
                $stmt = $this->getConnection()->prepare("SELECT id,qty FROM " . DB_STORE_CART . " WHERE store_id = :sid AND item_id = :id AND user_id = :uid");
                $stmt->bindParam("sid", $this->store_id);
                $stmt->bindParam("id", $this->id);
                $stmt->bindParam("uid", $this->uid);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function RemoveItem()
    {
        if (isset($this->id) && isset($this->fid) && isset($this->uid) && isset($this->store_id)) {
            try {
                $stmt = $this->getConnection()->prepare("DELETE FROM " . DB_STORE_CART . " WHERE store_id = :sid AND item_id = :id  AND ft_id = :fid AND user_id = :uid");
                $stmt->bindParam("sid", $this->store_id);
                $stmt->bindParam("id", $this->id);
                $stmt->bindParam("fid", $this->fid);
                $stmt->bindParam("uid", $this->uid);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function ClearCart()
    {
        if (isset($this->uid)) {
            try {
                $stmt = $this->getConnection()->prepare("DELETE FROM " . DB_STORE_CART . " WHERE user_id = :uid");
                $stmt->bindParam("uid", $this->uid);
                $stmt->execute();
                return $stmt ? true : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function UpdateItem($qty = 1, $addQty = true)
    {
        if (isset($this->id) && isset($this->fid) && isset($this->uid) && isset($this->store_id)) {
            $qty = (int)$qty;
            $qtyVAL = ($addQty) ? "qty = qty+$qty" : "qty = " . $qty;
            try {
                $stmt = $this->getConnection()->prepare("UPDATE " . DB_STORE_CART . " SET $qtyVAL WHERE store_id = :sid AND item_id = :id AND ft_id = :fid AND user_id = :uid");
                $stmt->bindParam("id", $this->id);
                $stmt->bindParam("fid", $this->fid);
                $stmt->bindParam("uid", $this->uid);
                $stmt->bindParam("sid", $this->store_id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function InsertItem($qty = 1)
    {
        if (isset($this->id) && isset($this->fid) && isset($this->uid) && isset($this->store_id)) {
            $qty = (int)$qty;
            try {
                $time = time();
                $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_STORE_CART . "(store_id, item_id,ft_id, user_id, qty, time) VALUES (:sid , :id ,:fid, :uid, :qty, :time)");
                $stmt->bindParam("sid", $this->store_id);
                $stmt->bindParam("id", $this->id);
                $stmt->bindParam("fid", $this->fid);
                $stmt->bindParam("uid", $this->uid);
                $stmt->bindParam("qty", $qty);
                $stmt->bindParam("time", $time);
                $stmt->execute();
                return $this->getConnection()->lastInsertId();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function InsertFeatures()
    {
        if (!empty($this->id) && !empty($this->features)) {
            try {
                $done = false;
                $pID = 0;
                $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_STORE_CART_FT . "(p_id, cart_id,ft_id) VALUES (0 , :id ,0)");
                $stmt->execute(array(':id' => $this->id));
                if ($stmt) {
                    $pID = $this->getConnection()->lastInsertId();
                    foreach ($this->features as $k => $v) {
                        $stmt = $this->getConnection()->prepare("INSERT INTO " . DB_STORE_CART_FT . "(p_id, cart_id,ft_id) VALUES (:pid , :id ,:fid)");
                        $stmt->bindParam("pid", $pID);
                        $stmt->bindParam("id", $this->id);
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

    public function setCartFeature()
    {
        if (!empty($this->id) && !empty($this->fid)) {
            try {
                $stmt = $this->getConnection()->prepare("UPDATE " . DB_STORE_CART . " SET ft_id=:fid WHERE id = :id");
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

    public function setId($item_id)
    {
        $this->id = (int)$item_id;
    }

    public function setFtId($ft_id)
    {
        $this->fid = (int)$ft_id;
    }

    public function setFeatures($ft)
    {
        if (!empty($ft) && is_array($ft)) $this->features = $ft;
    }

    public function setUserId($userID)
    {
        $this->uid = (int)$userID;
    }

    public function setStoreId($sid)
    {
        $this->store_id = (int)$sid;
    }

}