<?php

namespace iCmsAPI;

use iCms\Escape;

class LoginControl
{

    public $data;
    private $userid;
    private $username;
    private $password;
    private $usertype;
    private $logintoken;
    private $id;
    private $cityid;
    private $city_group;
    private $lat;
    private $lon;
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

    public function CheckLogin()
    {
        if (!empty($this->username) && !empty($this->password) && !empty($this->usertype)) {
            $query1 = $this->getConnection()->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE  password='" . $this->password . "' AND username='" . $this->username . "' AND (type = 'admin' OR type = 'seller' OR type='" . $this->usertype . "') AND active=1");
            if ($query1->num_rows == 1) {
                $fetch = $query1->fetch_array(MYSQLI_ASSOC);
                $this->data = $fetch['id'];
                return $this->data;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function CheckFacebookLogin()
    {
        if (!empty($this->userid) && !empty($this->password)) {
            $query1 = $this->getConnection()->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE  username='" . $this->userid . "' AND password='" . $this->password . "' AND authmethod='facebook' AND active=1");
            if ($query1->num_rows == 1) {
                $fetch = $query1->fetch_array(MYSQLI_ASSOC);
                $this->data = $fetch['id'];
                return $this->data;
            }
        }
        return false;
    }

    public function CheckGoogleLogin()
    {
        if (!empty($this->userid) && !empty($this->password)) {
            $query1 = $this->getConnection()->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE  username='" . $this->userid . "' AND password='" . $this->password . "' AND authmethod='google' AND active=1");
            if ($query1->num_rows == 1) {
                $fetch = $query1->fetch_array(MYSQLI_ASSOC);
                $this->data = $fetch['id'];
                return $this->data;
            }
        }
        return false;
    }

    public function UpdateLoginHash()
    {
        if (!empty($this->id) && !empty($this->password)) {
            $loginhash = $this->hashSSHA($this->password);
            $query1 = $this->getConnection()->query("UPDATE " . DB_ACCOUNTS . " SET logintoken = '$loginhash' WHERE id=" . $this->id . " AND active=1");
            if ($query1) {
                $this->data = $loginhash;
                return $this->data;
            }
        }
        return false;
    }

    /**
     * Encrypting password
     * returns encrypted password
     * @param string $hash
     * @return string
     */
    public function hashSSHA($hash)
    {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 20);
        $encrypted = base64_encode(sha1($hash . $salt, true) . $salt);
        return $encrypted;
    }

    public function UpdateLastLogin()
    {
        if (!empty($this->id)) {
            $query1 = $this->getConnection()->query("UPDATE " . DB_ACCOUNTS . " SET app_lastlogged=" . time() . " WHERE id=" . $this->id . " AND active=1");
            if ($query1) {
                return true;
            }
        }
        return false;
    }

    public function UpdateUserGeo()
    {
        if (!empty($this->id) && !empty($this->cityid) && !empty($this->city_group) && !empty($this->lat) && !empty($this->lon)) {
            $query1 = $this->getConnection()->query("UPDATE " . DB_USERS . " SET current_city=" . $this->cityid . ",city_group=" . $this->city_group . ",lat=" . $this->lat . ",lon=" . $this->lon . " WHERE id=" . $this->id);
            if ($query1) {
                return true;
            }
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function setCityid($ctid)
    {
        $this->cityid = (int)$ctid;
    }

    public function setCityGroup($ctgrp)
    {
        $this->city_group = (int)$ctgrp;
    }
    
    public function setLat($latitude)
    {
        $this->lat = (double)$latitude;
    }
    
    public function setLon($longitude)
    {
        $this->lon = (double)$longitude;
    }
    
    public function setUsername($uname)
    {
        $this->username = $this->escapeObj->stringEscape($uname);
    }

    public function setUserpass($upass)
    {
        $this->password = $this->escapeObj->stringEscape($upass);
    }

    public function setUsertype($utype)
    {
        $this->usertype = $this->escapeObj->stringEscape($utype);
    }

    public function setLogintoken($logtoken)
    {
        $this->logintoken = $this->escapeObj->stringEscape($logtoken);
    }

    public function setUserid($urid)
    {
        $this->userid = $this->escapeObj->stringEscape($urid);
    }

}
