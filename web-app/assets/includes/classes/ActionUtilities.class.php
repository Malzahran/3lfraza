<?php

namespace iCms;

use PDOException;

class ActionUtilities
{
    private $conn;
    private $escapeObj;
    private $reviewtitle = null;
    private $reviewtext = null;
    private $id;
    private $uid;
    private $actype;
    private $rating = 0;


    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new Escape();
        return $this;
    }

    public function setConnection(\mysqli $conn)
    {
        $this->conn = $conn;
        return $this;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function setDirectoryAction($type = 1, $ids = array())
    {
        if (!empty($this->id)) {
            $id = "id=" . $this->id;
        } else if (!empty($ids)) {
            $ids = implode(', ', $ids);
            $id = " id IN (" . $ids . ") ";
        } else {
            return false;
        }

        $action = '';
        switch ($type) {
            case 1:
                $action = "views = views +1";
                break;
            case 2:
                $action = "clicks=clicks+1";
                break;
            case 3:
                $action = "phclicks=phclicks+1";
                break;
        }
        try {
            $stmt = $this->getPDO()->prepare("UPDATE " . DB_DIR . " SET $action WHERE $id");
            $stmt->execute();
            return true;
        } catch (PDOException $ex) {
            return false;
        }
    }

    protected function getPDO()
    {
        global $db;
        return $db;
    }

    public function setOrderProgress()
    {
        if (isset($this->id) && isset($this->actype)) {
            try {
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_ORDERS . " SET progress=:prg WHERE order_id=:ord");
                $stmt->bindParam("prg", $this->actype);
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrderWorker()
    {
        if (isset($this->id) && isset($this->actype)) {
            try {
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_ORDERS . " SET progress=3,worker_id=:worker WHERE order_id=:ord");
                $stmt->bindParam("worker", $this->actype);
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrderDelivery()
    {
        if (isset($this->id) && isset($this->actype)) {
            try {
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_ORDERS . " SET delv_id=:dlv WHERE order_id=:ord");
                $stmt->bindParam("dlv", $this->actype);
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrderPrepared()
    {
        if (isset($this->id) && isset($this->actype)) {
            try {
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_ORDERS . " SET progress=4 WHERE order_id=:ord");
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                if ($stmt->rowCount() > 0) {
                    $ntfObj = new \iCms\Notifications();
                    $notarray = array('rid' => $this->id, 'usid' => $this->actype, 'type' => 5, 'state' => 4);
                    if ($ntfObj->registerNotif($notarray)) return true;
                }
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrderOnWay()
    {
        if (isset($this->id) && isset($this->actype)) {
            try {
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_ORDERS . " SET progress=5 WHERE order_id=:ord");
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                if ($stmt->rowCount() > 0) {
                    $ntfObj = new \iCms\Notifications();
                    $notarray = array('rid' => $this->id, 'usid' => $this->actype, 'type' => 5, 'state' => 5);
                    if ($ntfObj->registerNotif($notarray)) return true;
                }
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setOrderDelivered()
    {
        if (isset($this->id) && isset($this->actype)) {
            try {
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_ORDERS . " SET progress=6 WHERE order_id=:ord");
                $stmt->bindParam("ord", $this->id);
                $stmt->execute();
                if ($stmt->rowCount() > 0) {
                    $ntfObj = new \iCms\Notifications();
                    $notarray = array('rid' => $this->id, 'usid' => $this->actype, 'type' => 5, 'state' => 6);
                    if ($ntfObj->registerNotif($notarray)) return true;
                }
                return $stmt->rowCount();
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    public function setItemAction($type = 1, $ids = array())
    {
        if (!empty($this->id)) {
            $id = "id=" . $this->id;
        } else if (!empty($ids)) {
            $ids = implode(', ', $ids);
            $id = " id IN (" . $ids . ") ";
        } else {
            return false;
        }

        $action = '';
        switch ($type) {
            case 1:
                $action = "views = views +1";
                break;
            case 2:
                $action = "clicks=clicks+1";
                break;
            case 3:
                $action = "orders=orders+1";
                break;
        }
        try {
            $stmt = $this->getPDO()->prepare("UPDATE " . DB_STORE_ITEMS . " SET $action WHERE $id");
            $stmt->execute();
            return true;
        } catch (PDOException $ex) {
            return false;
        }
    }

    public function setNewsAction($type = 1, $ids = array())
    {
        if (!empty($this->id)) {
            $id = "id=" . $this->id;
        } else if (!empty($ids)) {
            $ids = implode(', ', $ids);
            $id = " id IN (" . $ids . ") ";
        } else {
            return false;
        }

        $action = '';
        switch ($type) {
            case 1:
                $action = "views = views +1";
                break;
            case 2:
                $action = "clicks=clicks+1";
                break;
        }
        try {
            $stmt = $this->getPDO()->prepare("UPDATE " . DB_NEWS_ITEMS . " SET $action WHERE $id");
            $stmt->execute();
            return true;
        } catch (PDOException $ex) {
            return false;
        }
    }

    public function registerRating()
    {
        if (isset($this->id) && isset($this->uid) && isset($this->actype)) {
            $query = $this->getConnection()->query("INSERT INTO " . DB_RATE . " (item_id,review_title,review_content,userid,type,rating,time) VALUES (" . $this->id . ",'" . $this->reviewtitle . "','" . $this->reviewtext . "'," . $this->uid . "," . $this->actype . ",'" . $this->rating . "'," . time() . ")");

            if ($query) {
                $this->id = $this->getConnection()->insert_id;
                return $this->id;
            }
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function setUserId($uid)
    {
        $this->uid = (int)$uid;
    }

    public function setAcType($atype)
    {
        $this->actype = (int)$atype;
    }

    public function setRating($rating)
    {
        $this->rating = (float)$rating;
    }

    public function setReviewtitle($rvtitle)
    {
        $this->reviewtitle = $this->escapeObj->stringEscape($rvtitle);
    }

    public function setReviewtext($rvtext)
    {
        $this->reviewtext = $this->escapeObj->postEscape($rvtext);
    }

}