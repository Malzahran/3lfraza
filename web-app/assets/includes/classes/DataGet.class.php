<?php

namespace iCms;

use PDO;
use PDOException;

class DataGet
{
    private $conn;
    private $escapeObj;
    private $data;

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

    protected function getPDO()
    {
        global $db;
        return $db;
    }

    public function getUserIds($data = array())
    {
        $get = array();
        global $user;
        $where = '';
        if (!empty($data['utype']) && is_array($data['utype'])) {
            $utype = "'" . implode("','", $data['utype']) . "'";
            $where .= " user.type IN (" . $utype . ") ";
        } else {
            $where .= " user.type IN ('user','agent')";
        }
        
        if (isset($user['id'])) $where .= " AND user.id !=" . $user['id'];

        if (!empty($data['active']) && is_array($data['active'])) {
            $ids = array_filter($data['active'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND user.active IN (" . $ids . ") ";
        } else {
            $where .= " AND user.active IN (1)";
        }

        if (!empty($data['store_id']) && $data['store_id'] != 0) {
            $storeID = (int)$data['store_id'];
            $where .= " AND uinfo.store_id =" . $storeID;
        }

        if (!empty($data['app_last_logged'])) {
            $lastLogged = (int)$data['app_last_logged'];
            $where .= " AND user.app_lastlogged <" . (time() - $lastLogged);
        }

        if (!empty($data['last_logged'])) {
            $lastLogged = (int)$data['last_logged'];
            $where .= " AND user.last_logged <" . (time() - $lastLogged);
        }

        if (isset($data['delv_type'])) {
            $delvType = (int)$data['delv_type'];
            $where .= " AND uinfo.delv_type =" . $delvType;
        }

        if (isset($data['is_ready'])) {
            $ready = (int)$data['is_ready'];
            $where .= " AND uinfo.ready =" . $ready;
        }

        if (!empty($data['actype']) && is_array($data['actype'])) {
            $actype = "'" . implode("','", $data['actype']) . "'";
            $where .= " AND user.authmethod IN (" . $actype . ") ";
        } else {
            $where .= " AND user.authmethod IN ('web','facebook','google','form')";
        }

        if (!empty($data['geo_info'])) {
            $where .= " AND (uinfo.lat BETWEEN " . number_format($data['geo_info']['latitudeMin'], 12, '.', '') . "
                AND " . number_format($data['geo_info']['latitudeMax'], 12, '.', '') . ")
                AND (uinfo.lon BETWEEN " . number_format($data['geo_info']['longitudeMin'], 12, '.', '') . "
                AND " . number_format($data['geo_info']['longitudeMax'], 12, '.', '') . ")";
        }

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            if (is_numeric($data['search']['value'])) {
                $sQuery = $this->escapeObj->stringEscape($data['search']['value']);
                $search .= " AND uinfo.phone LIKE '%$sQuery%'";
            } else {
                $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
                $search .= " AND (user.name rlike '$sQuery' OR user.username rlike '$sQuery' OR user.email rlike '$sQuery')";
            }
        }

        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct user.id) AS count FROM " . DB_ACCOUNTS . " AS user INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id) WHERE $where");
        if ($querytotal->num_rows >= 1) {
            $fetch = $querytotal->fetch_array(MYSQLI_ASSOC);
            $counttotal = $fetch['count'];
        } else {
            return false;
        }
        if (empty($data['count'])) {

            $limit = '';

            if (!empty($data['length']) && $data['length'] != -1) {
                $start = 0;
                $len = (int)$data['length'];
                if (!empty($data['start'])) {
                    $start = (int)$data['start'];
                }
                $limit = "LIMIT $start,$len";
            }

            if (isset($data['order']) && count($data['order'])) {
                $order = "ORDER BY ";
                $orderCL = "user.id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $orderCL = "user.name " . $dir;
                            break;
                        case 2 :
                            if (isset($data['delivery']) || isset($data['worker'])) {
                                $orderCL = "user.time " . $dir;
                            } else {
                                $orderCL = "user.authmethod " . $dir;
                            }
                            break;
                        case 3 :
                            $orderCL = "user.time " . $dir;
                            break;
                        case 4 :
                            $orderCL = "user.active " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "user.id DESC";
            }
            $queryText = "SELECT distinct user.id FROM " . DB_ACCOUNTS . " AS user INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id) WHERE $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct user.id) AS count FROM " . DB_ACCOUNTS . " AS user INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id) WHERE $where");
            $fetchfil = $queryfilter->fetch_array(MYSQLI_ASSOC);
            $countfil = $fetchfil['count'];


            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2['id'];
                }
            }
            $this->data = array('count' => $counttotal, 'filter' => $countfil, 'data' => $get);
        } else {
            $this->data = $counttotal;
        }
        if (!empty($this->data)) {
            return $this->data;
        } else {
            return false;
        }


    }

    public function getRepIds($data = array())
    {
        $get = array();
        $where = null;
        if (!empty($data['rptype']) && is_array($data['rptype'])) {
            $ids = array_filter($data['rptype'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " rep.notf_type IN (" . $ids . ") ";
        } else {
            return false;
        }

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
            $search .= " AND (rep.notf_title rlike '$sQuery' OR rep.notf_msg rlike '$sQuery')";
        }

        $querytotal = $this->getConnection()->query("SELECT COUNT(rep.id) AS count FROM " . DB_FCM_REP . " AS rep WHERE $where $search");
        if ($querytotal->num_rows >= 1) {
            $fetch = $querytotal->fetch_array(MYSQLI_ASSOC);
            $counttotal = $fetch['count'];
        } else {
            return false;
        }
        if (empty($data['count'])) {

            $limit = '';

            if (!empty($data['length']) && $data['length'] != -1) {
                $start = 0;
                $len = (int)$data['length'];
                if (!empty($data['start'])) {
                    $start = (int)$data['start'];
                }
                $limit = "LIMIT $start,$len";
            }

            if (isset($data['order']) && count($data['order'])) {
                $order = "ORDER BY ";
                $ordercl = "rep.id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 4 :
                            $ordercl = "rep.notf_type " . $dir;
                            break;
                        case 6 :
                            $ordercl = "rep.time " . $dir;
                            break;
                    }
                }
                $order .= $ordercl;
            } else {
                $order = "ORDER BY ";
                $order .= "rep.id ASC";
            }
            $queryText = "SELECT rep.id FROM " . DB_FCM_REP . " AS rep WHERE $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(rep.id) AS count FROM " . DB_FCM_REP . " AS rep WHERE $where $search");
            $fetchfil = $queryfilter->fetch_array(MYSQLI_ASSOC);
            $countfil = $fetchfil['count'];


            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2['id'];
                }
            }
            $this->data = array('count' => $counttotal, 'filter' => $countfil, 'data' => $get);
        } else {
            $this->data = $counttotal;
        }
        if (!empty($this->data)) {
            return $this->data;
        } else {
            return false;
        }
    }

    public function getOrdIds($data = array())
    {
        $get = array();
        $where = null;
        if (!empty($data['active']) && is_array($data['active'])) {
            $ids = array_filter($data['active'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " ord.active IN (" . $ids . ") ";
        } else {
            $where .= " ord.active = 1 ";
        }

        if (!empty($data['type']) && is_array($data['type'])) {
            $ids = array_filter($data['type'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND ord.type IN (" . $ids . ") ";
        }

        if (!empty($data['progress']) && is_array($data['progress'])) {
            $ids = array_filter($data['progress'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND ord.progress IN (" . $ids . ") ";
        }

        if (!empty($data['store_id']) && $data['store_id'] != 0) {
            $storeID = (int)$data['store_id'];
            $where .= " AND ord.store_id =" . $storeID;
        }

        if (!empty($data['userid']) && $data['userid'] != 0) {
            $userid = (int)$data['userid'];
            $where .= " AND ord.user_id =" . $userid;
        }

        if (!empty($data['dlvid']) && $data['dlvid'] != 0) {
            $dlvid = (int)$data['dlvid'];
            $where .= " AND ord.delv_id =" . $dlvid;
        }

        if (!empty($data['workid']) && $data['workid'] != 0) {
            $workid = (int)$data['workid'];
            $where .= " AND ord.worker_id =" . $workid;
        }

        if (!empty($data['geo_info'])) {
            $where .= "AND (ord.lat BETWEEN " . number_format($data['geo_info']['latitudeMin'], 12, '.', '') . "
                AND " . number_format($data['geo_info']['latitudeMax'], 12, '.', '') . ")
                AND (ord.lon BETWEEN " . number_format($data['geo_info']['longitudeMin'], 12, '.', '') . "
                AND " . number_format($data['geo_info']['longitudeMax'], 12, '.', '') . ")";
        }

        if (!empty($data['from_date'])) {
            $datestart = strtotime($this->escapeObj->stringEscape($data['from_date'] . ' 00:00:00'));
            $where .= " AND $datestart <= ord.time ";
        }

        if (!empty($data['to_date'])) {
            $dateend = strtotime($this->escapeObj->stringEscape($data['to_date'] . ' 23:59:00'));
            $where .= " AND ord.time <= $dateend ";
        }

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            if (is_numeric($data['search']['value'])) {
                $sQuery = (int)$data['search']['value'];
                $search .= " AND ord.order_id LIKE '%$sQuery%'";
            } else {
                $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
                $search .= " AND (ord.content rlike '$sQuery')";
            }
        }

        $querytotal = $this->getConnection()->query("SELECT COUNT(ord.order_id) AS count FROM " . DB_ORDERS . " AS ord WHERE $where");
        if ($querytotal->num_rows >= 1) {
            $fetch = $querytotal->fetch_array(MYSQLI_ASSOC);
            $counttotal = $fetch['count'];
        } else {
            return false;
        }
        if (empty($data['count'])) {

            $limit = '';

            if (!empty($data['length']) && $data['length'] != -1) {
                $start = 0;
                $len = (int)$data['length'];
                if (!empty($data['start'])) {
                    $start = (int)$data['start'];
                }
                $limit = "LIMIT $start,$len";
            }

            if (isset($data['order']) && count($data['order'])) {
                $order = "ORDER BY ";
                $ordercl = "ord.order_id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $ordercl = "ord.id " . $dir;
                            break;
                        case 2 :
                            $ordercl = "ord.price " . $dir;
                            break;
                        case 3 :
                            $ordercl = "ord.discount " . $dir;
                            break;
                        case 4 :
                            $ordercl = "ord.delivery " . $dir;
                            break;
                        case 6 :
                            $ordercl = "ord.delv_id " . $dir;
                            break;
                        case 7 :
                            $ordercl = "ord.worker_id " . $dir;
                            break;
                        case 8 :
                            $ordercl = "ord.time " . $dir;
                            break;
                        case 9 :
                            $ordercl = "ord.city " . $dir;
                            break;
                        case 10 :
                            $ordercl = "ord.progress " . $dir;
                            break;
                    }
                }
                $order .= $ordercl;
            } else {
                $order = "ORDER BY ";
                $order .= "ord.order_id DESC";
            }
            $queryText = "SELECT ord.order_id FROM " . DB_ORDERS . " AS ord WHERE $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(ord.order_id) AS count FROM " . DB_ORDERS . " AS ord WHERE $where $search");
            $fetchfil = $queryfilter->fetch_array(MYSQLI_ASSOC);
            $countfil = $fetchfil['count'];


            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2['order_id'];
                }
            }
            $this->data = array('count' => $counttotal, 'filter' => $countfil, 'data' => $get);
        } else {
            $this->data = $counttotal;
        }
        if (!empty($this->data)) {
            return $this->data;
        } else {
            return false;
        }
    }

    public function getOrdrep($data = array(), $type = 1)
    {
        $get = array();
        $where = '';
        $select = '';
        $iJoin = '';
        $search = '';
        $group = '';
        $count = '';
        if (!empty($data['active']) && is_array($data['active'])) {
            $ids = array_filter($data['active'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " ord.active IN (" . $ids . ") ";
        } else {
            $where .= " ord.active = 1 ";
        }

        if ($type == 1) {
            $select = "COUNT(distinct ord.order_id) AS total,SUM(ord.price) AS sum,SUM(ord.discount) AS discount,SUM(ord.delivery) AS dlv,store.store_id AS id";
            $group = "ord.store_id";
            $iJoin = "INNER JOIN " . DB_STORE . " AS store ON (ord.store_id = store.store_id)";
            $count = "distinct ord.store_id";
        } else if ($type == 2) {
            $select = "COUNT(distinct ord.order_id) AS total,SUM(ord.price) AS sum,SUM(ord.discount) AS discount,SUM(ord.delivery) AS dlv,user.id AS id";
            $group = "ord.delv_id";
            $iJoin = "INNER JOIN " . DB_ACCOUNTS . " AS user ON (ord.delv_id = user.id) INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id)";
            $count = "distinct ord.delv_id";
        } else if ($type == 4) {
            $select = "COUNT(distinct ord.order_id) AS total,SUM(ord.price) AS sum,SUM(ord.discount) AS discount,SUM(ord.delivery) AS dlv,user.id AS id";
            $group = "ord.worker_id";
            $iJoin = "INNER JOIN " . DB_ACCOUNTS . " AS user ON (ord.worker_id = user.id) INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id)";
            $count = "distinct ord.worker_id";
        } else if ($type == 3) {
            $select = "COUNT(distinct ord.order_id) AS total,SUM(ord.price) AS sum,SUM(ord.discount) AS discount,SUM(ord.delivery) AS dlv,user.id AS id";
            $group = "ord.user_id";
            $iJoin = "INNER JOIN " . DB_ACCOUNTS . " AS user ON (ord.user_id = user.id) INNER JOIN " . DB_USERS . " AS uinfo ON (user.id = uinfo.id)";
            $count = "distinct ord.user_id";
        }


        if (!empty($data['type']) && is_array($data['type'])) {
            $ids = array_filter($data['type'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND ord.type IN (" . $ids . ") ";
        }

        if (!empty($data['progress']) && is_array($data['progress'])) {
            $ids = array_filter($data['progress'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND ord.progress IN (" . $ids . ") ";
        }

        if (isset($data['store_id']) && $data['store_id'] != 0) {
            $storeID = (int)$data['store_id'];
            $where .= " AND ord.store_id =" . $storeID;
        }

        if (isset($data['delv_type'])) {
            $delvType = (int)$data['delv_type'];
            $where .= " AND uinfo.delv_type =" . $delvType;
        }

        if (!empty($data['userid']) && $data['userid'] != 0) {
            $userid = (int)$data['userid'];
            $where .= " AND ord.user_id =" . $userid;
        }

        if (!empty($data['dlvid']) && $data['dlvid'] != 0) {
            $dlvid = (int)$data['dlvid'];
            $where .= " AND ord.delv_id =" . $dlvid;
        }

        if (!empty($data['workid']) && $data['workid'] != 0) {
            $workid = (int)$data['workid'];
            $where .= " AND ord.worker_id =" . $workid;
        }

        if (!empty($data['from_date'])) {
            $datestart = strtotime($this->escapeObj->stringEscape($data['from_date'] . ' 00:00:00'));
            $where .= " AND $datestart <= ord.time ";
        }

        if (!empty($data['to_date'])) {
            $dateend = strtotime($this->escapeObj->stringEscape($data['to_date'] . ' 23:59:00'));
            $where .= " AND ord.time <= $dateend ";
        }


        if (!empty($data['search']) && $data['search']['value'] != '') {
            if ($type == 1) {
                $iJoin .= "INNER JOIN " . DB_DIR . " AS dir ON (store.dir_id = dir.parent_id)";
                $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
                $search .= " AND (dir.title rlike '$sQuery')";
            } else if ($type == 2 || $type == 3 || $type == 4) {
                if (preg_match('/^01/', $data['search']['value'])) {
                    $sQuery = $this->escapeObj->stringEscape($data['search']['value']);
                    $search .= " AND uinfo.phone LIKE '%$sQuery%'";
                } else {
                    $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
                    $search .= " AND (user.name rlike '$sQuery' OR user.username rlike '$sQuery' OR user.email rlike '$sQuery')";
                }
            }
        }

        $querytotal = $this->getConnection()->query("SELECT COUNT($count) AS count FROM " . DB_ORDERS . " AS ord $iJoin WHERE $where");
        if ($querytotal->num_rows >= 1) {
            $fetch = $querytotal->fetch_array(MYSQLI_ASSOC);
            $counttotal = $fetch['count'];
        } else {
            return false;
        }
        if (empty($data['count'])) {

            $limit = '';

            if (!empty($data['length']) && $data['length'] != -1) {
                $start = 0;
                $len = (int)$data['length'];
                if (!empty($data['start'])) {
                    $start = (int)$data['start'];
                }
                $limit = "LIMIT $start,$len";
            }

            if (isset($data['order']) && count($data['order'])) {
                $order = "ORDER BY ";
                $ordercl = "ord.order_id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 2 :
                            $ordercl = "ord.price " . $dir;
                            break;
                        case 3 :
                            $ordercl = "ord.delivery " . $dir;
                            break;
                        case 6 :
                            $ordercl = "ord.time " . $dir;
                            break;
                    }
                }
                $order .= $ordercl;
            } else {
                $order = "ORDER BY ";
                $order .= "ord.order_id DESC";
            }
            $queryText = "SELECT $select FROM " . DB_ORDERS . " AS ord $iJoin WHERE $where $search ";
            $queryText .= "GROUP BY $group $order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT($count) AS count FROM " . DB_ORDERS . " AS ord $iJoin WHERE $where $search");
            $fetchfil = $queryfilter->fetch_array(MYSQLI_ASSOC);
            $countfil = $fetchfil['count'];


            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2;
                }
            }
            $this->data = array('count' => $counttotal, 'filter' => $countfil, 'data' => $get);
        } else {
            $this->data = $counttotal;
        }
        if (!empty($this->data)) {
            return $this->data;
        } else {
            return false;
        }
    }
}