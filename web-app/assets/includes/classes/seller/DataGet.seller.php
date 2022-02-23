<?php

namespace iCmsSeller;

use iCms\Escape;

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

    public function getNewsIds($data = array())
    {
        $get = array();
        $searchJoin = '';
        $tJoin = "INNER JOIN " . DB_NEWS_ITEMS_DATA . " AS itemdata ON (item.id = itemdata.news_id)";
        $titleJoin = '';
        $where = '';

        if (!empty($data['active']) && is_array($data['active'])) {
            $ids = array_filter($data['active'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " item.active IN (" . $ids . ") ";
        } else {
            $where .= " item.active IN (1)";
        }

        if (isset($data['featured'])) {
            $featured = (int)$data['featured'];
            $where .= " AND item.featured =" . $featured;
        }

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $titleJoin = $tJoin;
            $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
            $search .= " AND (itemdata.title rlike '$sQuery')";
        }
        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_NEWS_ITEMS . " AS item $titleJoin WHERE $where");
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
                $orderCL = "item.id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $orderCL = "itemdata.title " . $dir;
                            $titleJoin = $tJoin;
                            break;
                        case 2 :
                            $orderCL = "item.time " . $dir;
                            break;
                        case 3 :
                            $orderCL = "item.active " . $dir;
                            break;
                        case 4 :
                            $orderCL = "item.featured " . $dir;
                            break;
                        case 12 :
                            $orderCL = "item.time " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "item.id DESC";
            }
            $queryText = "SELECT distinct (item.id) FROM " . DB_NEWS_ITEMS . " AS item $titleJoin $searchJoin WHERE $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_NEWS_ITEMS . " AS item $titleJoin $searchJoin WHERE $where $search");
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

    public function getItemIds($data = array())
    {
        $get = array();
        $sJoin = "INNER JOIN " . DB_LANGUAGE_DATA . " AS lang ON ((item.category = lang.rid AND lang.category = 'scategory'))";
        $searchJoin = '';
        $tJoin = "LEFT OUTER JOIN " . DB_STORE_ITEMS_DATA . " AS itemdata ON (item.id = itemdata.item_id)";
        $titleJoin = '';
        $where = '';

        if (!empty($data['active']) && is_array($data['active'])) {
            $ids = array_filter($data['active'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " item.active IN (" . $ids . ") ";
        } else {
            $where .= " item.active IN (1)";
        }

        if (!empty($data['store_id']) && is_numeric($data['store_id'])) {
            $storeID = (int)$data['store_id'];
            $where .= " AND item.store_id =" . $storeID;
        }

        global $user, $aFeatures;
        if ($user['type'] == 'seller' && !$aFeatures) {
            $where .= " AND item.price != 0";
        }

        if (!empty($data['cat']) && $data['cat'] != 'all') {
            $cat = (int)$data['cat'];
            $where .= " AND item.category =" . $cat;
        }


        if (!empty($data['cats']) && is_array($data['cats'])) {
            $ids = array_filter($data['cats'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND item.category IN (" . $ids . ") ";
        }

        if (isset($data['featured'])) {
            $featured = (int)$data['featured'];
            $where .= " AND item.featured =" . $featured;
        }

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $searchQ = $this->escapeObj->stringEscape($data['search']['value']);
            if (preg_match('/^@/', $searchQ)) {
                $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
                $searchJoin = $sJoin;
                $sQuery = preg_replace('/^@/', '', $sQuery);
                $search .= " AND lang.text rlike '$sQuery'";
            } else if (is_numeric($searchQ)) {
                $searchQ = (int)$searchQ;
                $search .= " AND (item.item_code LIKE '%$searchQ%')";
            } else {
                $titleJoin = $tJoin;
                $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
                $search .= " AND (itemdata.title rlike '$sQuery')";
            }
        }
        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_STORE_ITEMS . " AS item $titleJoin WHERE $where");
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
                $orderCL = "dir.id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    global $aCATS, $aINV;
                    switch ($columnIdx) {
                        case 1 :
                            $orderCL = "item.item_code " . $dir;
                            break;
                        case 2 :
                            $orderCL = "itemdata.title " . $dir;
                            $titleJoin = $tJoin;
                            break;
                        case 3 :
                            if (($aCATS && $aINV) || ($aCATS && !$aINV)) {
                                $orderCL = "item.category " . $dir;
                            } else if (!$aCATS && $aINV) {
                                $orderCL = "item.inventory " . $dir;
                            } else {
                                $orderCL = "item.price " . $dir;
                            }
                            break;
                        case 4 :
                            if ($aCATS && $aINV) {
                                $orderCL = "item.inventory " . $dir;
                            } else if (!$aCATS && $aINV) {
                                $orderCL = "item.price " . $dir;
                            } else if ($aCATS && !$aINV) {
                                $orderCL = "item.category " . $dir;
                            } else {
                                $orderCL = "item.time " . $dir;
                            }
                            break;
                        case 5 :
                            if ($aCATS && $aINV) {
                                $orderCL = "item.price " . $dir;
                            } else if ((!$aCATS && $aINV) || ($aCATS && !$aINV)) {
                                $orderCL = "item.time " . $dir;
                            } else {
                                $orderCL = "item.active " . $dir;
                            }
                            break;
                        case 6 :
                            if ($aCATS && $aINV) {
                                $orderCL = "item.time " . $dir;
                            }
                            break;
                        case 7 :
                            if ($aCATS && $aINV) {
                                $orderCL = "item.active " . $dir;
                            }
                            break;
                        case 12 :
                            $orderCL = "item.time " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "item.id DESC";
            }
            $queryText = "SELECT distinct (item.id) FROM " . DB_STORE_ITEMS . " AS item $titleJoin $searchJoin WHERE $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_STORE_ITEMS . " AS item $titleJoin $searchJoin WHERE $where $search");
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

    public function getPromoIds($data = array())
    {
        $get = array();
        $where = '';

        if (!empty($data['active']) && is_array($data['active'])) {
            $ids = array_filter($data['active'], function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " item.active IN (" . $ids . ") ";
        } else {
            $where .= " item.active IN (1)";
        }

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $searchQ = $this->escapeObj->stringEscape($data['search']['value']);
            $search .= " AND (item.item_code LIKE '%$searchQ%')";
        }
        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_PROMO . " AS item WHERE $where");
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
                $orderCL = "item.id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $orderCL = "item.promo " . $dir;
                            break;
                        case 2 :
                            $orderCL = "item.type " . $dir;
                            break;
                        case 3 :
                            $orderCL = "item.amount " . $dir;
                            break;
                        case 4 :
                            $orderCL = "item.time " . $dir;
                            break;
                        case 5 :
                            $orderCL = "item.active " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "item.id DESC";
            }
            $queryText = "SELECT distinct (item.id) FROM " . DB_PROMO . " AS item WHERE $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_PROMO . " AS item WHERE $where $search");
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

    public function getLocationAlert($data = array())
    {
        $get = array();

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $searchQ = $this->escapeObj->stringEscape($data['search']['value']);
            $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
            $search .= " WHERE (item.name rlike '$sQuery' OR item.role LIKE '%$searchQ%'  OR item.phone LIKE '%$searchQ%' OR item.email LIKE '%$searchQ%')";
        }
        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_LOCATION_ALERT . " AS item");
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
                $orderCL = "item.id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $orderCL = "item.role " . $dir;
                            break;
                        case 2 :
                            $orderCL = "item.name " . $dir;
                            break;
                        case 3 :
                            $orderCL = "item.phone " . $dir;
                            break;
                        case 4 :
                            $orderCL = "item.email " . $dir;
                            break;
                        case 6 :
                            $orderCL = "item.time " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "item.id DESC";
            }
            $queryText = "SELECT * FROM " . DB_LOCATION_ALERT . " AS item $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct item.id) AS count FROM " . DB_LOCATION_ALERT . " AS item $search");
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

    protected function getConnection()
    {
        return $this->conn;
    }

    public function getCatIds($data = array())
    {
        $get = array();
        $sJoin = "INNER JOIN " . DB_LANGUAGE_DATA . " AS lang ON (cats.category_name = lang.keyword AND lang.category='scategory')";
        $searchJoin = '';
        $where = '';
        if (!empty($data['store_id'])) {
            $storeID = (int)$data['store_id'];
            $where .= "WHERE";
            $where .= " cats.store_id =" . $storeID;
        } else {
            return false;
        }

        if (!empty($data['ctype'])) {
            $type = (int)$data['ctype'];
            $where .= " AND";
            if ($type == 1) {
                $where .= " cats.pid = 0";
            } else if ($type == 2) {
                $where .= " cats.pid != 0 AND cats.mid = 0";
            } else if ($type == 3) {
                $where .= " cats.mid != 0";
            }
        }

        if (!empty($data['type'])) {
            $types = array_filter($data['type'], function ($n) {
                return (is_numeric($n));
            });
            $types = implode(', ', $types);
            $where .= " AND cats.type IN (" . $types . ")";
        } else {
            $where .= " AND cats.type IN (1,4)";
        }
        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $searchJoin = $sJoin;
            $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
            $search .= " AND (lang.text rlike '$sQuery')";
        }
        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct cats.category_id) AS count FROM " . DB_STORE_CATS . " AS cats $where");
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
                $orderCL = "cats.category_id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $searchJoin = $sJoin;
                            $orderCL = "lang.lang_code ASC,lang.text " . $dir;
                            break;
                        case 2 :
                            $orderCL = "cats.pid " . $dir;
                            break;
                        case 3 :
                            $orderCL = "cats.mid " . $dir;
                            break;
                        case 4 :
                            $orderCL = "cats.pid $dir,cats.mid $dir,cats.cat_order " . $dir;
                            break;
                        case 5 :
                            $orderCL = "cats.type " . $dir;
                            break;
                        case 7 :
                            $orderCL = "cats.time " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "cats.category_id DESC";
            }
            $queryText = "SELECT distinct cats.category_id FROM " . DB_STORE_CATS . " AS cats $searchJoin $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct cats.category_id) AS count FROM " . DB_STORE_CATS . " AS cats $searchJoin $where $search");
            $fetchfil = $queryfilter->fetch_array(MYSQLI_ASSOC);
            $countfil = $fetchfil['count'];

            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2['category_id'];
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

    public function getCityIds($data = array())
    {
        $get = array();
        $sJoin = "INNER JOIN " . DB_LANGUAGE_DATA . " AS lang ON (city.city_name = lang.keyword AND lang.category='city')";
        $searchJoin = '';
        $where = 'WHERE';
        if (!empty($data['csid'])) {
            $csid = (int)$data['csid'];
            $where .= " city.csid =" . $csid;
        } else $where .= " city.csid =2";

        $search = '';
        if (!empty($data['search']) && $data['search']['value'] != '') {
            $searchJoin = $sJoin;
            $sQuery = $this->escapeObj->arabic_query($data['search']['value']);
            $search .= " AND (lang.text rlike '$sQuery')";
        }
        $querytotal = $this->getConnection()->query("SELECT COUNT(distinct city.city_id) AS count FROM " . DB_CITY . " AS city $where");
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
                $orderCL = "city.city_id DESC";
                for ($i = 0, $ien = count($data['order']); $i < $ien; $i++) {
                    $columnIdx = (int)$data['order'][$i]['column'];
                    $dir = $data['order'][$i]['dir'] === 'asc' ? 'ASC' : 'DESC';
                    switch ($columnIdx) {
                        case 1 :
                            $searchJoin = $sJoin;
                            $orderCL = "lang.lang_code ASC,lang.text " . $dir;
                            break;
                        case 2 :
                            $orderCL = "city.city_group " . $dir;
                            break;
                        case 3 :
                            $orderCL = "city.lat " . $dir;
                            break;
                        case 4 :
                            $orderCL = "city.lon " . $dir;
                            break;
                        case 5 :
                            $orderCL = "city.radius " . $dir;
                            break;
                        case 6 :
                            $orderCL = "city.shipping " . $dir;
                            break;
                        case 7 :
                            $orderCL = "city.time " . $dir;
                            break;
                    }
                }
                $order .= $orderCL;
            } else {
                $order = "ORDER BY ";
                $order .= "city.city_id DESC";
            }
            $queryText = "SELECT distinct city.city_id FROM " . DB_CITY . " AS city $searchJoin $where $search ";
            $queryText .= "$order $limit";

            $queryfilter = $this->getConnection()->query("SELECT COUNT(distinct city.city_id) AS count FROM " . DB_CITY . " AS city $searchJoin $where $search");
            $fetchfil = $queryfilter->fetch_array(MYSQLI_ASSOC);
            $countfil = $fetchfil['count'];

            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2['city_id'];
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