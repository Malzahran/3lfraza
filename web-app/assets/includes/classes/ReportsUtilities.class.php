<?php

namespace iCms;

class ReportsUtilities
{
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

    public function getOnlines($tp = 1)
    {
        $tp = (int)$tp;
        $coloumn = null;

        switch ($tp) {
            case 1:
                $coloumn = "app_lastlogged";
                break;
            case 2:
                $coloumn = "last_logged";
                break;
        }
        $query = $this->getConnection()->query("SELECT COUNT(id) AS count FROM " . DB_ACCOUNTS . " WHERE $coloumn >" . (time() - 15) . "");

        if ($query->num_rows >= 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            return $fetch['count'];
        } else {
            return 0;
        }
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function getURegstate($tp = 1)
    {
        $tp = (int)$tp;
        $coloumn = null;

        switch ($tp) {
            case 1:
                $coloumn = "facebookmob";
                break;
            case 2:
                $coloumn = "form";
                break;
            case 3:
                $coloumn = "web";
                break;
            case 4:
                $coloumn = "facebook";
                break;
        }
        $query = $this->getConnection()->query("SELECT COUNT(id) AS count FROM " . DB_ACCOUNTS . " WHERE authmethod='$coloumn'");

        if ($query->num_rows >= 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            return $fetch['count'];
        } else {
            return 0;
        }
    }

    public function getDirCount($tp = 1)
    {
        $tp = (int)$tp;
        $query = $this->getConnection()->query("SELECT COUNT(DISTINCT parent_id) AS count FROM " . DB_DIR . " WHERE parent_id!= 0 AND active=" . $tp);

        if ($query->num_rows >= 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            return $fetch['count'];
        } else {
            return 0;
        }
    }

    public function getDirAcSum($tp = 1)
    {
        $tp = (int)$tp;
        $coloumn = null;
        switch ($tp) {
            case 1:
                $coloumn = "views";
                break;
            case 2:
                $coloumn = "clicks";
                break;
            case 3:
                $coloumn = "phclicks";
                break;
        }
        $query = $this->getConnection()->query("SELECT SUM($coloumn) as sum FROM " . DB_DIR . "");

        if ($query->num_rows >= 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            return $fetch['sum'];
        } else {
            return 0;
        }
    }
}