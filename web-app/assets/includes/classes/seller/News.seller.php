<?php

namespace iCmsSeller;


use iCms\DeleteMedia;
use iCms\Escape;
use iCms\Utilities;

class News
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
        if (is_array($data['title']) && is_array($data['desc'])) {
            global $user;
            $userID = $user['id'];
            $query = $this->getConnection()->query("INSERT INTO " . DB_NEWS_ITEMS . " (active,user_id,time) VALUES (1,$userID," . time() . ")");

            if ($query) {
                $itemID = $this->getConnection()->insert_id;
                $query2 = false;
                foreach ($data['title'] as $key => $val) {
                    $title = $this->escapeObj->stringEscape($val);
                    $desc = $this->escapeObj->postEscape($data['desc'][$key]);
                    $query2 = $this->getConnection()->query("INSERT INTO " . DB_NEWS_ITEMS_DATA . " (news_id,lang_code,title,content) VALUES ($itemID,'$key','$title','$desc')");
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

    public function featItem()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        $itemFetch = null;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $itemFetch = $utiObj->getNews($this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['featured'] == 1) $feat = 0;
                else $feat = 1;
                $query = $this->getConnection()->query("UPDATE " . DB_NEWS_ITEMS . " SET featured = $feat WHERE id=" . $itemFetch['id']);
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
            $itemFetch = $utiObj->getNews($this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['active'] == 1) {
                    $active = 0;
                } else {
                    $active = 1;
                }
                $query = $this->getConnection()->query("UPDATE " . DB_NEWS_ITEMS . " SET active = $active WHERE id=" . $itemFetch['id']);
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
            if (is_array($data['title']) && is_array($data['desc'])) {
                $activate = $activate != 0 ? 1 : 0;
                $query = $this->getConnection()->query("UPDATE " . DB_NEWS_ITEMS . " SET active=$activate WHERE id=" . $this->id);
                $query2 = null;
                if ($query) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $desc = $this->escapeObj->postEscape($data['desc'][$key]);
                        $query2 = $this->getConnection()->query("UPDATE " . DB_NEWS_ITEMS_DATA . " SET title='$title',content='$desc' WHERE news_id=" . $this->id . " AND lang_code='$key'");
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
            $itemFetch = $utiObj->getNews($this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['featured_image'] != 0) {
                    $delIMG = $this->delObj->deleteMedia($itemFetch['featured_image']);
                    if ($delIMG) $continue = true;
                } else $continue = true;

                if ($continue) {
                    $queryDel = $this->getConnection()->query("DELETE FROM " . DB_NEWS_ITEMS_DATA . " WHERE news_id=" . $this->id);
                    if ($queryDel) {
                        $queryDel = $this->getConnection()->query("DELETE FROM " . DB_NEWS_ITEMS . " WHERE id=" . $this->id);
                        if ($queryDel) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }
}