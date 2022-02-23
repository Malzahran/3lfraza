<?php

namespace iCms;

class CheckUtilities
{
    private $conn;
    private $escapeObj;
    private $id;
    private $uid;
    private $actype;


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

    protected function getConnection()
    {
        return $this->conn;
    }

    public function CheckRate()
    {
        if (!empty($this->id) && !empty($this->uid) && isset($this->actype)) {
            $query = $this->getConnection()->query("SELECT rate_id FROM " . DB_RATE . " WHERE item_id=" . $this->id . " AND userid=" . $this->uid . " AND type=" . $this->actype);

            if ($query->num_rows == 1) {
                return true;
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

}