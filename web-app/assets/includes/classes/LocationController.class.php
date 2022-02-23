<?php

namespace iCms;

class LocationController
{

    public $data;
    private $id;
    private $userid;
    private $lon;
    private $lat;
    private $speed;
    private $conn;
    private $escapeObj;

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

    public function getLocations($data = array())
    {
        $get = array();
        $where = '';
        if (isset($data['utype']) && is_array($data['utype'])) {
            $utype = "'" . implode("','", $data['utype']) . "'";
            $where .= "user.type IN (" . $utype . ") ";
        } else {
            $where .= "user.type IN ('delivery')";
        }
        if (isset($data['store_id'])) {
            $storeID = (int)$data['store_id'];
            $where .= " AND uinfo.store_id =" . $storeID;
        }
        if (isset($data['delv_type'])) {
            $delvType = (int)$data['delv_type'];
            $where .= " AND uinfo.delv_type =" . $delvType;
        }
        $query = $this->getConnection()->query("SELECT loc.* FROM " . DB_USERS_LOC . " AS loc INNER JOIN " . DB_ACCOUNTS . " AS user ON (loc.userid = user.id) INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id) WHERE $where");
        if ($query->num_rows >= 1) {
            while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                $get[] = $fetch;
            }
        }
        $this->data = $get;

        if (!empty($this->data)) {
            return $this->data;
        } else {
            return false;
        }

    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function CheckLocation()
    {
        if (!empty($this->userid)) {
            $query1 = $this->getConnection()->query("SELECT loc_id FROM " . DB_USERS_LOC . " WHERE userid=" . $this->userid);
            if ($query1->num_rows == 1) {
                $fetch = $query1->fetch_array(MYSQLI_ASSOC);
                $this->data = $fetch['loc_id'];
                return $this->data;
            }
        }
        return false;
    }

    public function InsertLocation()
    {
        if (!empty($this->userid) && !empty($this->lon) && !empty($this->lat)) {
            $query = $this->getConnection()->query("INSERT INTO " . DB_USERS_LOC . " (userid,lon,lat,speed,time) VALUES (" . $this->userid . ",'" . $this->lon . "','" . $this->lat . "','" . $this->speed . "'," . time() . ")");
            if ($query) {
                return true;
            }
        }
        return false;
    }

    public function UpdateLocation()
    {
        if (!empty($this->id) && !empty($this->lon) && !empty($this->lat)) {
            $query = $this->getConnection()->query("UPDATE " . DB_USERS_LOC . " SET lon='" . $this->lon . "',lat='" . $this->lat . "',speed='" . $this->speed . "',time=" . time() . " WHERE loc_id=" . $this->id);
            if ($query) {
                return true;
            }
        }
        return false;
    }
    
    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function setUserid($urid)
    {
        $this->userid = (int)$urid;
    }

    public function setSpeed($sp)
    {
        $this->speed = (int)$sp;
    }

    public function setLong($long)
    {
        $this->lon = floatval($long);
    }

    public function setLat($latu)
    {
        $this->lat = floatval($latu);
    }

}