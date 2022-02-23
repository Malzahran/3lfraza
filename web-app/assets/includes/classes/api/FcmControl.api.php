<?php

namespace iCmsAPI;

class FcmControl
{

    public $data;
    private $userid = 0;
    private $fcmtoken;
    private $id;
    private $conn;
    private $escapeObj;

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new \iCms\Escape();
        return $this;
    }

    public function setConnection(\mysqli $conn)
    {
        $this->conn = $conn;
        return $this;
    }

    public function CheckToken()
    {
        if (!empty($this->fcmtoken)) {
            $query1 = $this->getConnection()->query("SELECT id FROM " . DB_FCM . " WHERE  token='" . $this->fcmtoken . "'");
            if ($query1->num_rows == 1) {
                $fetch = $query1->fetch_array(MYSQLI_ASSOC);
                $this->data = $fetch['id'];
                return $this->data;
            } else {
                return false;
            }
        }
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function InsertToken()
    {
        if (!empty($this->fcmtoken)) {
            $query1 = $this->getConnection()->query("INSERT INTO " . DB_FCM . " (user_id,token,time) VALUES (" . $this->userid . ",'" . $this->fcmtoken . "'," . time() . ")");
            if ($query1) {
                $this->data = true;
                return $this->data;
            }
        }
        return false;
    }

    public function DeleteToken()
    {
        if (!empty($this->id)) {
            $query1 = $this->getConnection()->query("DELETE FROM " . DB_FCM . " WHERE id = " . $this->id);
            if ($query1) {
                $this->data = true;
                return $this->data;
            }
        }
        return false;
    }

    public function DeleteUserTokens()
    {
        if (!empty($this->userid)) {
            $query1 = $this->getConnection()->query("DELETE FROM " . DB_FCM . " WHERE user_id=" . $this->userid);
            if ($query1) {
                $this->data = true;
                return $this->data;
            }
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function setFcmtoken($fctoken)
    {
        $this->fcmtoken = $this->escapeObj->stringEscape($fctoken);
    }

    public function setUserid($urid)
    {
        $this->userid = (int)$urid;
    }

}
