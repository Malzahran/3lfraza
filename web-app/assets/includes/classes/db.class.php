<?php

namespace iCms;
class db
{
    private $dbConn;

    function __construct()
    {
        global $sql_host, $sql_name, $sql_user, $sql_pass;
        try {
            $this->dbConn = new \PDO("mysql:host=" . $sql_host . ";dbname=" . $sql_name . ";charset=utf8", $sql_user, $sql_pass, array(\PDO::ATTR_PERSISTENT => true));
            $this->dbConn->setAttribute(\PDO::ATTR_ERRMODE, \PDO::ERRMODE_EXCEPTION);
        } catch (\PDOException $e) {
            die("Can not connect database");
        }
    }

    public function getConnection()
    {
        return $this->dbConn;
    }
}