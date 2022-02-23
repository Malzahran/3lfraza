<?php

namespace iCms;

use PDO;
use PDOException;

class Utilities
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

    public function getCats($catID = 0, $type = array())
    {
        $catID = (int)$catID;
        if ($catID != 0) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_CATS . " a WHERE category_id = :id");
                $stmt->bindParam("id", $catID);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else {
            $where = '';
            if (!empty($type)) {
                $types = array_filter($type, function ($n) {
                    return (is_numeric($n));
                });
                $types = implode(', ', $types);
                $where .= " AND type IN (" . $types . ")";
            }
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_CATS . " WHERE pid=0 $where ORDER BY catorder ASC");
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
    }

    /* Get Main Cats Or Cat Info */

    protected function getPDO()
    {
        global $db;
        return $db;
    }

    /* Get Main Cats */

    public function getMainCats($catID = 0, $type = array())
    {
        $this->data = null;
        $get = array();
        $catID = (int)$catID;
        $where = '';
        if (!empty($type)) {
            $types = array_filter($type, function ($n) {
                return (is_numeric($n));
            });
            $types = implode(', ', $types);
            $where .= " AND type IN (" . $types . ")";
        }
        if ($catID != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CATS . " WHERE pid=$catID AND mid=0 $where");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CATS . " WHERE ((pid = 0) OR (mid=0 AND has_sub = 1)) $where ORDER BY catorder ASC");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Sub Cats */

    protected function getConnection()
    {
        return $this->conn;
    }

    /* Get Store Main Cats Or Cat Info */

    public function getSubCat($catID = 0, $type = array())
    {
        $this->data = null;
        $get = array();
        $where = '';
        if (!empty($type)) {
            $types = array_filter($type, function ($n) {
                return (is_numeric($n));
            });
            $types = implode(', ', $types);
            $where .= "AND type IN (" . $types . ")";
        }
        $catID = (int)$catID;
        if ($catID != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CATS . " WHERE mid=$catID $where");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CATS . " WHERE ((mid != 0) OR (pid != 0 AND mid = 0 AND has_sub = 0)) $where ORDER BY pid ASC,catorder ASC");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Store Main Cats */

    public function getStoreCats($catID = 0, $storeID = 0, $type = array(1), $city_group = array(), $has_sub = 0)
    {
        $catID = (int)$catID;
        $storeID = (int)$storeID;
        if ($catID != 0) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_STORE_CATS . " WHERE category_id = :id");
                $stmt->bindParam("id", $catID);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else if ($storeID != 0) {
            $where = '';
            if (!empty($type)) {
                $types = array_filter($type, function ($n) {
                    return (is_numeric($n));
                });
                $types = implode(', ', $types);
                $where .= " AND type IN (" . $types . ")";
            }
            if (!empty($city_group)) {
                $ids = array_filter($city_group, function ($n) {
                    return (is_numeric($n));
                });
                $ids = implode(', ', $ids);
                $where .= " AND city_group IN (" . $ids . ") ";
            }
            try {
                $more = $has_sub ? 'AND has_sub = 0' : '';
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_STORE_CATS . " WHERE pid=0 $more $where AND store_id=$storeID ORDER BY cat_order ASC");
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Get Store Sub Cats */

    public function getStoreMainCats($catID = 0, $storeID = 0, $type = array(), $city_group = array())
    {
        $this->data = null;
        $get = array();
        $catID = (int)$catID;
        $storeID = (int)$storeID;
        $where = '';
        if (!empty($type)) {
            $types = array_filter($type, function ($n) {
                return (is_numeric($n));
            });
            $types = implode(', ', $types);
            $where .= "AND type IN (" . $types . ") ";
        }
        if (!empty($city_group)) {
            $ids = array_filter($city_group, function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= "AND city_group IN (" . $ids . ") ";
        }
        if ($catID != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_CATS . " WHERE pid=$catID AND mid=0 $where ORDER BY cat_order ASC");
            if ($query->num_rows > 0) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else if ($storeID != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_CATS . " WHERE ((pid = 0) OR (mid=0 AND has_sub = 1)) AND store_id=$storeID $where ORDER BY cat_order ASC");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    public function getStoreSubCat($catID = 0, $storeID = 0, $type = array(), $city_group = array())
    {
        $this->data = null;
        $get = array();
        $where = '';
        if (!empty($type)) {
            $types = array_filter($type, function ($n) {
                return (is_numeric($n));
            });
            $types = implode(', ', $types);
            $where .= " AND type IN (" . $types . ")";
        }
        if (!empty($city_group)) {
            $ids = array_filter($city_group, function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where .= " AND city_group IN (" . $ids . ") ";
        }
        $catID = (int)$catID;
        $storeID = (int)$storeID;
        if ($catID != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_CATS . " WHERE mid=$catID $where ORDER BY cat_order ASC");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else if ($storeID != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_CATS . " WHERE ((mid != 0) OR (pid != 0 AND mid = 0 AND has_sub = 0)) AND store_id=$storeID $where ORDER BY pid ASC,cat_order ASC");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get City Info */

    public function getCity($ctId = 0, $parent = array(), $geoInfo = array())
    {
        $this->data = null;
        $get = array();
        $ctId = (int)$ctId;
        $fetch = '';
        if ($ctId != 0 && empty($parent)) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CITY . " WHERE city_id=$ctId");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        } else if (!empty($parent) && $ctId == 0) {
            $ids = array_filter($parent, function ($n) {
                return (is_numeric($n));
            });
            $ids = implode(', ', $ids);
            $where = " csid IN (" . $ids . ") ";
            if (!empty($geoInfo)) {
                $where .= "AND (lat BETWEEN " . number_format($geoInfo['latitudeMin'], 12, '.', '') . "
                AND " . number_format($geoInfo['latitudeMax'], 12, '.', '') . ")
                AND (lon BETWEEN " . number_format($geoInfo['longitudeMin'], 12, '.', '') . "
                AND " . number_format($geoInfo['longitudeMax'], 12, '.', '') . ")";
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CITY . " WHERE $where");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_CITY . " WHERE csid=0");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Rating Info */
    public function getRatingTotal($itemID = 0, $type = 0)
    {
        $itemID = (int)$itemID;
        $type = (int)$type;
        if ($itemID != 0 && $type != 0) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT COUNT(DISTINCT userid) AS count, ROUND(SUM(rating), 2) AS rate FROM " . DB_RATE . " WHERE item_id=:id AND type=:type");
                $stmt->bindParam("id", $itemID);
                $stmt->bindParam("type", $type);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Get Time Slot */
    public function getTimeSlot($current = '', $id = 0)
    {
        if (!empty($current)) {
            global $escapeObj;
            $current = $escapeObj->stringEscape($current);
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_TIME_SLOT . " WHERE (:cur BETWEEN start AND end )");
                $stmt->bindParam("cur", $current);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else if ($id != 0) {
            $id = (int)$id;
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_TIME_SLOT . " WHERE id=:id");
                $stmt->bindParam("id", $id);
                $stmt->execute();
                return $stmt->rowCount() == 1 ? $stmt->fetch(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        } else {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_TIME_SLOT);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Get Promo Code */
    public function getPromoCode($code = '', $item_id = 0)
    {
        $code = $code ? $this->escapeObj->stringEscape($code) : '';
        if ($code) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_PROMO . " WHERE promo=:code AND active=1");
                $stmt->bindParam("code", $code);
                $stmt->execute();
                return $stmt->rowCount() == 1 ? $stmt->fetch(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        } else if ($item_id) {
            $item_id = (int)$item_id;
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_PROMO . " WHERE id=:id");
                $stmt->bindParam("id", $item_id);
                $stmt->execute();
                return $stmt->rowCount() == 1 ? $stmt->fetch(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Check Stock Alert */
    public function chkStockAlert($item_id = 0)
    {
        global $isLogged, $user;
        if (!$isLogged) return false;
        $item_id = (int)$item_id;
        if ($item_id) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_STOCK_ALERT . " WHERE item_id=:id AND user_id=:uid");
                $stmt->bindParam("id", $item_id);
                $stmt->bindParam("uid", $user['id']);
                $stmt->execute();
                return $stmt->rowCount() == 1 ? $stmt->fetch(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Register Stock Alert */
    public function regStockAlert($item_id = 0)
    {
        global $isLogged, $user;
        if (!$isLogged) return false;
        $item_id = (int)$item_id;
        if ($item_id) {
            try {
                $time = time();
                $stmt = $this->getPDO()->prepare("INSERT INTO " . DB_STOCK_ALERT . " (item_id,user_id,time) VALUES (:id,:uid,:tm)");
                $stmt->bindParam("id", $item_id);
                $stmt->bindParam("uid", $user['id']);
                $stmt->bindParam("tm", $time);
                $stmt->execute();
                return $stmt ? $this->getPDO()->lastInsertId() : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }


    /* Update Stock Alert */
    public function upStockAlert($item_id = 0)
    {
        global $isLogged, $user;
        if (!$isLogged) return false;
        $item_id = (int)$item_id;
        if ($item_id) {
            try {
                $time = time();
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_STOCK_ALERT . " SET time=:tm WHERE item_id=:id AND user_id=:uid");
                $stmt->bindParam("id", $item_id);
                $stmt->bindParam("uid", $user['id']);
                $stmt->bindParam("tm", $time);
                $stmt->execute();
                return $stmt ? true : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }
    
    public function getStockAlertReport($item_id = 0, $info = true) {
        $results = array();
        if ($item_id != 0) {
            $item_id = (int)$item_id;
            $select = $info ? 'COUNT(item_id) as count,MAX(time) as time' : '*';
            $query = $this->getConnection()->query("SELECT $select FROM " . DB_STOCK_ALERT . " WHERE item_id=" . $item_id);
            if ($query->num_rows > 0) {
                while($fetch = $query->fetch_array(MYSQLI_ASSOC)){
                    $results[] = $fetch;
                }
                return $results;
            }
        } else {
            $query = $this->getConnection()->query("SELECT distinct item_id FROM " . DB_STOCK_ALERT);
            if ($query->num_rows > 0) {
                while($fetch = $query->fetch_array(MYSQLI_ASSOC)){
                    $results[] = $fetch;
                }
                return $results;
            }
        }
        return false;
    }

    /* Check Location Alert */
    public function chkLocationAlert($role = '', $email = '', $phone = '', $lat = 0, $lon = 0)
    {
        $userId = 0;
        global $isLogged;
        if ($isLogged) {
            global $user;
            $userId = $user['id'];
        }
        $role = $this->escapeObj->stringEscape($role);
        $email = $this->escapeObj->stringEscape($email);
        $phone = $this->escapeObj->stringEscape($phone);
        $lat = (double)$lat;
        $lon = (double)$lon;
        if ($email && $phone && $lat && $lon) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_LOCATION_ALERT . " WHERE user_id=:uid AND role=:rl AND phone=:ph AND email=:em AND lat=:lat AND lon=:lon");
                $stmt->bindParam("uid", $userId);
                $stmt->bindParam("rl", $role);
                $stmt->bindParam("ph", $phone);
                $stmt->bindParam("em", $email);
                $stmt->bindParam("lat", $lat);
                $stmt->bindParam("lon", $lon);
                $stmt->execute();
                return $stmt->rowCount() == 1 ? $stmt->fetch(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Register Location Alert */
    public function regLocationAlert($role = '', $email = '', $phone = '', $name = '', $lat = 0, $lon = 0)
    {
        $userId = 0;
        global $isLogged;
        if ($isLogged) {
            global $user;
            $userId = $user['id'];
        }
        $role = $this->escapeObj->stringEscape($role);
        $email = $this->escapeObj->stringEscape($email);
        $phone = $this->escapeObj->stringEscape($phone);
        $name = $this->escapeObj->stringEscape($name);
        $lat = (double)$lat;
        $lon = (double)$lon;
        if ($email && $phone && $lat && $lon) {
            try {
                $time = time();
                $stmt = $this->getPDO()->prepare("INSERT INTO " . DB_LOCATION_ALERT . " (user_id,role,name,phone,email,lat,lon,time) VALUES (:uid,:rl,:nm,:ph,:em,:lat,:lon,:tm)");
                $stmt->bindParam("uid", $userId);
                $stmt->bindParam("rl", $role);
                $stmt->bindParam("nm", $name);
                $stmt->bindParam("ph", $phone);
                $stmt->bindParam("em", $email);
                $stmt->bindParam("lat", $lat);
                $stmt->bindParam("lon", $lon);
                $stmt->bindParam("tm", $time);
                $stmt->execute();
                return $stmt ? $this->getPDO()->lastInsertId() : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }


    /* Update Location Alert */
    public function upLocationAlert($role = '', $email = '', $phone = '', $lat = 0, $lon = 0)
    {
        $userId = 0;
        global $isLogged;
        if ($isLogged) {
            global $user;
            $userId = $user['id'];
        }
        $role = $this->escapeObj->stringEscape($role);
        $email = $this->escapeObj->stringEscape($email);
        $phone = $this->escapeObj->stringEscape($phone);
        $lat = (double)$lat;
        $lon = (double)$lon;
        if ($email && $phone && $lat && $lon) {
            try {
                $time = time();
                $stmt = $this->getPDO()->prepare("UPDATE " . DB_LOCATION_ALERT . " SET time=:tm WHERE user_id=:uid AND role=:rl AND phone=:ph AND email=:em AND lat=:lat AND lon=:lon");
                $stmt->bindParam("uid", $userId);
                $stmt->bindParam("rl", $role);
                $stmt->bindParam("ph", $phone);
                $stmt->bindParam("em", $email);
                $stmt->bindParam("lat", $lat);
                $stmt->bindParam("lon", $lon);
                $stmt->bindParam("tm", $time);
                $stmt->execute();
                return $stmt ? true : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Get News */
    public function getNews($itmId = 0, $lm = 6, $ft = 0)
    {
        $this->data = null;
        $get = array();
        $itmId = (int)$itmId;
        $ft = (int)$ft;
        $fetch = '';
        if ($itmId != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_NEWS_ITEMS . " WHERE id=" . $itmId);
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        } else {
            $limit = '';
            if ($lm != 0) {
                $lm = (int)$lm;
                $limit = "LIMIT " . $lm;
            }
            $featured = '';
            if ($ft == 1) {
                $featured = "AND featured=$ft";
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_NEWS_ITEMS . " WHERE active=1 $featured ORDER BY id DESC $limit");

            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get News Info */

    public function getNewsInfo($itmId = 0, $gLang = '')
    {
        $this->data = null;
        $itmId = (int)$itmId;
        $fetch = '';
        if ($itmId != 0) {
            if (!empty($gLang)) {
                $langCode = $this->escapeObj->stringEscape($gLang);
            } else {
                global $L_CODE;
                $langCode = $L_CODE;
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_NEWS_ITEMS_DATA . " WHERE news_id= " . $itmId . " AND lang_code = '$langCode'");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Directory Info */

    public function getItem($itmId = 0, $caTid = 0, $lm = 6, $ft = 0)
    {
        $this->data = null;
        $get = array();
        $itmId = (int)$itmId;
        $caTid = (int)$caTid;
        $ft = (int)$ft;
        $fetch = '';
        if ($itmId != 0 && $caTid == 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS . " WHERE id=" . $itmId);
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        } else if ($itmId == 0 && $caTid != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS . " WHERE category=" . $caTid);
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else {
            $limit = '';
            if ($lm != 0) {
                $lm = (int)$lm;
                $limit = "LIMIT " . $lm;
            }
            $featured = '';
            if ($ft == 1) {
                $featured = "AND featured=$ft";
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS . " WHERE active=1 $featured ORDER BY id DESC $limit");

            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Item */

    public function getItemInfo($itmId = 0, $gLang = '')
    {
        $this->data = null;
        $itmId = (int)$itmId;
        $fetch = '';
        if ($itmId != 0) {
            if (!empty($gLang)) {
                $langCode = $this->escapeObj->stringEscape($gLang);
            } else {
                global $L_CODE;
                $langCode = $L_CODE;
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS_DATA . " WHERE item_id= " . $itmId . " AND lang_code = '$langCode'");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }


    /* Get Item Features */

    public function getItemFeaturesCats($itmId = 0, $catID = 0)
    {
        $this->data = null;
        $get = array();
        $itmId = (int)$itmId;
        $catID = (int)$catID;
        if ($catID != 0 && $itmId != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS_FT . " WHERE item_id=" . $itmId . " AND category=" . $catID);
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        } else if ($itmId != 0 && $catID == 0) {
            $query = $this->getConnection()->query("SELECT DISTINCT category FROM " . DB_STORE_ITEMS_FT . " WHERE item_id=" . $itmId);
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Item Features */

    public function getItemFeatures($itmId = 0, $ftId = 0, $price = false)
    {
        $this->data = null;
        $get = array();
        $itmId = (int)$itmId;
        $ftId = (int)$ftId;
        $fetch = '';
        if ($ftId != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS_FT . " WHERE id=" . $ftId);
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        } else if ($itmId != 0) {
            $where = $price ? 'AND price != 0' : '';
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS_FT . " WHERE item_id=" . $itmId . " $where ORDER BY price ASC");
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Feature Info */

    public function getItemFeaturesInfo($ftID = 0, $gLang = '')
    {
        $this->data = null;
        $ftID = (int)$ftID;
        $fetch = '';
        if ($ftID != 0) {
            if (!empty($gLang)) {
                $langCode = $this->escapeObj->stringEscape($gLang);
            } else {
                global $L_CODE;
                $langCode = $L_CODE;
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE_ITEMS_FT_DATA . " WHERE f_id= " . $ftID . " AND lang_code = '$langCode'");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Item Info */

    public function getDirectoryInfo($locId = 0, $langCode = '', $type = 1)
    {
        $locId = (int)$locId;
        $type = (int)$type;
        if (!empty($langCode)) {
            $langCode = $this->escapeObj->stringEscape($langCode);
            $type = 2;
        } else {
            global $L_CODE;
            $langCode = $L_CODE;
        }
        if ($type == 1) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_DIR_INFO . " WHERE loc_id=:id AND lang_code='all'");
                $stmt->bindParam("id", $locId);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else if ($type == 2) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_DIR_INFO . " WHERE loc_id=:id AND lang_code=:lang");
                $stmt->bindParam("id", $locId);
                $stmt->bindParam("lang", $langCode);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Get Directory Contact Info */

    public function getStore($storeID = 0, $langCode = '', $fetchDir = true)
    {
        $storeID = (int)$storeID;
        if (!empty($langCode)) {
            $langCode = $this->escapeObj->stringEscape($langCode);
        } else {
            global $L_CODE;
            $langCode = $L_CODE;
        }
        $query = $this->getConnection()->query("SELECT * FROM " . DB_STORE . " WHERE store_id=" . $storeID);
        if ($query->num_rows == 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            $get['store'] = $fetch;
            if ($fetchDir) {
                $get['dir'] = $this->getDirectory($fetch['dir_id'], 0, 0, $langCode);
            }
            return $get;
        }
        return false;
    }

    /* Get Store Info */

    public function getDirectory($dirId = 0, $caTid = 0, $parent_id = 0, $gLang = '', $lm = 6, $ft = 0)
    {
        $dirId = (int)$dirId;
        $caTid = (int)$caTid;
        $parent_id = (int)$parent_id;
        $ft = (int)$ft;
        if ($dirId != 0 && $caTid == 0 && $parent_id == 0) {
            if (!empty($gLang)) {
                $langCode = $this->escapeObj->stringEscape($gLang);
            } else {
                global $L_CODE;
                $langCode = $L_CODE;
            }
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_DIR . " WHERE lang_code=:lang AND parent_id=:id");
                $stmt->bindParam("lang", $langCode);
                $stmt->bindParam("id", $dirId);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else if ($dirId == 0 && $parent_id == 0 && $caTid != 0) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_DIR . " WHERE parent_id=0 AND category=:id");
                $stmt->bindParam("id", $caTid);
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else if ($dirId == 0 && $caTid == 0 && $parent_id != 0) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_DIR . " WHERE parent_id=0 AND id=:id");
                $stmt->bindParam("id", $parent_id);
                $stmt->execute();
                return $stmt->fetch(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        } else {
            $limit = null;
            if ($lm != 0) {
                $lm = (int)$lm;
                $limit = "LIMIT " . $lm;
            }
            $featured = null;
            if ($ft == 1) {
                $featured = "AND featured=$ft";
            }
            try {
                $stmt = $this->getPDO()->prepare("SELECT * FROM " . DB_DIR . " WHERE active=1 AND parent_id=0 $featured ORDER BY id DESC $limit");
                $stmt->execute();
                return $stmt->fetchAll(PDO::FETCH_ASSOC);
            } catch (PDOException $ex) {
                return false;
            }
        }
    }

    /* Get Lang Text */
    public function getLangText($keyword = '', $lang_code = '', $type = 1)
    {
        $this->data = null;
        $type = (int)$type;
        $fetch = '';
        $keyword = $this->escapeObj->stringEscape($keyword);
        $lang_code = $this->escapeObj->stringEscape($lang_code);
        if ($type == 1) {
            $query = $this->getConnection()->query("SELECT text FROM " . DB_LANGUAGE_DATA . " WHERE keyword='$keyword' AND lang_code='$lang_code'");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch['text'];
        } else if ($type == 2) {
            $query = $this->getConnection()->query("SELECT text FROM language_$lang_code WHERE keyword='$keyword' AND lang_code='$lang_code'");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch['text'];
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Order Info */
    public function getOrder($ordId = 0)
    {
        $this->data = null;
        $ordId = (int)$ordId;
        $fetch = null;
        if ($ordId != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_ORDERS . " WHERE order_id=$ordId");
            if ($query->num_rows >= 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    /* Get Order Items */
    public function getOrderItems($ordId = 0)
    {
        $this->data = null;
        $ordId = (int)$ordId;
        $get = array();
        if ($ordId != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_ORDER_ITEMS . " WHERE order_id=" . $ordId);
            if ($query->num_rows >= 1) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch;
                }
            }
            $this->data = $get;
        }

        if (!empty($this->data)) return $this->data;
        return false;
    }

    public function getOrderItemFeatures($ftID = 0)
    {
        $ftID = (int)$ftID;
        if ($ftID != 0) {
            try {
                $stmt = $this->getPDO()->prepare("SELECT ft_id FROM " . DB_ORDER_ITEMS_FT . " WHERE p_id = :fid");
                $stmt->bindParam("fid", $ftID);
                $stmt->execute();
                return $stmt->rowCount() > 0 ? $stmt->fetchAll(PDO::FETCH_ASSOC) : false;
            } catch (PDOException $ex) {
                return false;
            }
        }
        return false;
    }

    /* Get FCM Report Info */
    public function getReport($repId = 0)
    {
        $this->data = null;
        $repId = (int)$repId;
        $fetch = '';
        if ($repId != 0) {
            $query = $this->getConnection()->query("SELECT * FROM " . DB_FCM_REP . " WHERE id=$repId");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        }

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }


    /* Get Misc Item Info */
    public function getMiscInfo($keyword = '', $gLang = '')
    {
        $this->data = null;
        $keyword = $this->escapeObj->stringEscape($keyword);
        $fetch = '';
        if (!empty($keyword)) {
            if (!empty($gLang)) {
                $langCode = $this->escapeObj->stringEscape($gLang);
            } else {
                global $L_CODE;
                $langCode = $L_CODE;
            }
            $query = $this->getConnection()->query("SELECT * FROM " . DB_MISC_DATA . " WHERE keyword= '" . $keyword . "' AND lang_code = '$langCode'");
            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
            }
            $this->data = $fetch;
        }
        return !empty($this->data) ? $this->data : false;
    }
}