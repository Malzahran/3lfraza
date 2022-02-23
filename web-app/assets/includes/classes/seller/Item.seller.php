<?php

namespace iCmsSeller;


use iCms\DeleteMedia;
use iCms\Escape;
use iCms\Utilities;

class Item
{
    private $conn;
    private $escapeObj;
    private $delObj;
    private $id;

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new Escape();
        $this->delObj = new DeleteMedia();
        return $this;
    }

    public function registerItem($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty ($data['code']) && is_array($data['title']) && is_array($data['desc'])) {
            $code = (int)$data['code'];
            $catID = isset($data['cat']) ? (int)$data['cat'] : 0;
            $inventory = isset($data['inventory']) ? (int)$data['inventory'] : 0;
            $price = isset($data['price']) ? (float)$data['price'] : 0;
            $dscprice = !empty($data['dscprice']) ? (float)$data['dscprice'] : 0;
            $cart = (isset($data['cart']) && $data['cart'] == 1) ? 0 : 1;
            $stock = (isset($data['stock']) && $data['stock'] == 1) ? 0 : 1;
            $soon = (isset($data['soon']) && $data['soon'] == 1) ? 1 : 0;
            $order = !empty($data['order']) ? (int)$data['order'] : 9000;
            global $user;
            $userID = $user['id'];
            $storeID = $user['store_id'];
            $query = $this->getConnection()->query("INSERT INTO " . DB_STORE_ITEMS . " (active,itemsorder,item_code,price,dsc_price,category,inventory,user_id,store_id,cart,stock,soon,time) VALUES (1,$order,$code,'$price','$dscprice',$catID,$inventory,$userID,$storeID,$cart,$stock,$soon," . time() . ")");

            if ($query) {
                $itemID = $this->getConnection()->insert_id;
                $query2 = false;
                foreach ($data['title'] as $key => $val) {
                    $title = $this->escapeObj->stringEscape($val);
                    $desc = $this->escapeObj->postEscape($data['desc'][$key]);
                    $notes = $this->escapeObj->postEscape($data['notes'][$key]);
                    $query2 = $this->getConnection()->query("INSERT INTO " . DB_STORE_ITEMS_DATA . " (item_id,lang_code,title,content,notes) VALUES ($itemID,'$key','$title','$desc','$notes')");
                }
                if ($query2) return $itemID;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function registerFeature($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty($data['item_id']) && !empty($data['category']) && is_array($data['title'])) {
            $itemID = (int)$data['item_id'];
            $cat = (int)$data['category'];
            $price = !empty($data['price']) ? (float)$data['price'] : 0;
            $dscprice = !empty($data['dscprice']) ? (float)$data['dscprice'] : 0;
            $query = $this->getConnection()->query("INSERT INTO " . DB_STORE_ITEMS_FT . " (active,category,price,dsc_price,item_id,time) VALUES (1,$cat,'$price','$dscprice',$itemID," . time() . ")");

            if ($query) {
                $ftID = $this->getConnection()->insert_id;
                $query2 = false;
                foreach ($data['title'] as $key => $val) {
                    $title = $this->escapeObj->stringEscape($val);
                    $query2 = $this->getConnection()->query("INSERT INTO " . DB_STORE_ITEMS_FT_DATA . " (f_id,lang_code,title) VALUES ($ftID,'$key','$title')");
                }
                if ($query2) return $ftID;
            }
        }
        return false;
    }

    public function editFeature($data = array(), $activate = 1)
    {
        if (!empty($this->id)) {
            $ftID = $this->id;
            global $sellerLogged, $adminLogged;
            $cont = false;
            if ($sellerLogged || $adminLogged) $cont = true;
            if (!$cont) return false;
            if (is_array($data['title']) && !empty($data['category'])) {
                $cat = (int)$data['category'];
                $price = !empty($data['price']) ? (float)$data['price'] : 0;
                $dscprice = !empty($data['dscprice']) ? (float)$data['dscprice'] : 0;
                $activate = $activate != 0 ? 1 : 0;
                $query = $this->getConnection()->query("UPDATE " . DB_STORE_ITEMS_FT . " SET active=$activate,category=$cat,price='$price',dsc_price='$dscprice' WHERE id=" . $this->id);

                if ($query) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $query2 = $this->getConnection()->query("UPDATE " . DB_STORE_ITEMS_FT_DATA . " SET title='$title' WHERE f_id=" . $this->id . " AND lang_code='$key'");
                    }
                    if ($query2) return $ftID;
                }
            }
        }
        return false;
    }

    public function featItem()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        $itemFetch = null;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $itemFetch = $utiObj->getItem($this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['featured'] == 1) $feat = 0;
                else $feat = 1;
                $query = $this->getConnection()->query("UPDATE " . DB_STORE_ITEMS . " SET featured = $feat WHERE id=" . $itemFetch['id']);
                if ($query) return true;
            }
        }
        return false;
    }

    public function activeItem()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $itemFetch = $utiObj->getItem($this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['active'] == 1) {
                    $active = 0;
                } else {
                    $active = 1;
                }
                $query = $this->getConnection()->query("UPDATE " . DB_STORE_ITEMS . " SET active = $active WHERE id=" . $itemFetch['id']);
                if ($query) {
                    return true;
                }
            }
        }
        return false;
    }

    public function editItem($data = array(), $activate = 1)
    {
        if (!empty($this->id)) {
            $itemID = $this->id;
            global $sellerLogged, $adminLogged;
            $cont = false;
            if ($sellerLogged || $adminLogged) $cont = true;
            if (!$cont) return false;
            if (!empty ($data['code']) && is_array($data['title']) && is_array($data['desc'])) {
                $code = (int)$data['code'];
                $catID = isset($data['cat']) ? (int)$data['cat'] : 0;
                $inventory = isset($data['inventory']) ? (int)$data['inventory'] : 0;
                $price = isset($data['price']) ? (float)$data['price'] : 0;
                $dscprice = !empty($data['dscprice']) ? (float)$data['dscprice'] : 0;
                $cart = (isset($data['cart']) && $data['cart'] == 1) ? 0 : 1;
                $stock = (isset($data['stock']) && $data['stock'] == 1) ? 0 : 1;
                $soon = (isset($data['soon']) && $data['soon'] == 1) ? 1 : 0;
                $activate = $activate != 0 ? 1 : 0;
                $order = !empty($data['order']) ? (int)$data['order'] : 9000;
                $query = $this->getConnection()->query("UPDATE " . DB_STORE_ITEMS . " SET active=$activate,itemsorder=$order,item_code=$code,price='$price',dsc_price='$dscprice',category=$catID,inventory=$inventory,cart=$cart,stock=$stock,soon=$soon WHERE id=" . $this->id);

                if ($query) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $desc = $this->escapeObj->postEscape($data['desc'][$key]);
                        $notes = $this->escapeObj->postEscape($data['notes'][$key]);
                        $query2 = $this->getConnection()->query("UPDATE " . DB_STORE_ITEMS_DATA . " SET title='$title',content='$desc',notes='$notes' WHERE item_id=" . $this->id . " AND lang_code='$key'");
                    }
                    if ($query2) return $itemID;
                }
            }
        }
        return false;
    }

    public function deleteItem()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        $continue = false;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $itemFetch = $utiObj->getItem($this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['featured_image'] != 0) {
                    $delIMG = $this->delObj->deleteMedia($itemFetch['featured_image']);
                    if ($delIMG) $continue = true;
                } else $continue = true;

                if ($continue) {
                    $queryDel = $this->getConnection()->query("DELETE FROM " . DB_STORE_ITEMS . " WHERE id=" . $this->id);
                    if ($queryDel) return true;
                }
            }
        }
        return false;
    }

    public function deleteFeature()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty($this->id)) {
            $queryDel = $this->getConnection()->query("DELETE FROM " . DB_STORE_ITEMS_FT . " WHERE id=" . $this->id);
            if ($queryDel) {
                $queryDelInfo = $this->getConnection()->query("DELETE FROM " . DB_STORE_ITEMS_FT_DATA . " WHERE f_id=" . $this->id);
                if ($queryDelInfo) return true;
            }
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }
}