<?php

namespace iCmsSeller;


use iCms\DeleteMedia;
use iCms\Escape;
use iCms\Utilities;

class Category
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

    public function registerCategory($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (isset($data['title']['en']) && isset($data['title']['ar'])) {
            $titleEN = $this->escapeObj->stringEscape($data['title']['en']);
            $titleAR = $this->escapeObj->stringEscape($data['title']['ar']);
            $catName = $titleEN . ' - ' . $titleAR;
            $subCat = 0;
            $hasSub = (isset($data['cat']) && $data['cat'] == -2) ? 2 : 0;
            $mainCat = (isset($data['cat']) && ($data['cat'] == -1 || $data['cat'] == -2)) ? 0 : (isset($data['cat']) && $data['cat'] != -1) ? (int)$data['cat'] : 0;
            if (isset($data['subcat'])) {
                if ($data['subcat'] == -1) $hasSub = 1;
                else if ($data['subcat'] == -2) $subCat = 0;
                else $subCat = (int)$data['subcat'];
            }
            $city_group = isset($data['city_group']) ? (int)$data['city_group'] : 0;
            $order = !empty($data['order']) ? (int)$data['order'] : 5000;
            $type = isset($data['ctype']) ? (int)$data['ctype'] : 1;
            global $user;
            $storeID = $user['store_id'];
            $query = $this->getConnection()->query("INSERT INTO " . DB_STORE_CATS . " (category,city_group,type,store_id,cat_order,pid,mid,has_sub,time) VALUES ('$catName',$city_group,$type,$storeID,$order,$mainCat,$subCat,$hasSub," . time() . ")");
            if ($query) {
                $catID = $this->getConnection()->insert_id;
                $langSlug = $this->escapeObj->getSlug('cat' . $catID);
                $query2 = $this->getConnection()->query("UPDATE " . DB_STORE_CATS . " SET category_name = '$langSlug' WHERE category_id=" . $catID);
                if ($query2) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $langRegister = $this->escapeObj->registerLang($langSlug, $title, $key, $catID, 'scategory');
                    }
                    if (isset($langRegister)) {
                        if ($langRegister) {
                            return $catID;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function editCategory($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) {
            $cont = true;
        }
        if (!$cont) {
            return false;
        }
        if (isset($this->id) && isset($data['title']['en']) && isset($data['title']['ar'])) {
            $catID = $this->id;
            $titleEN = $this->escapeObj->stringEscape($data['title']['en']);
            $titleAR = $this->escapeObj->stringEscape($data['title']['ar']);
            $catName = $titleEN . ' - ' . $titleAR;
            $subCat = 0;
            $hasSub = (isset($data['cat']) && $data['cat'] == -2) ? 2 : 0;
            $mainCat = (isset($data['cat']) && ($data['cat'] == -1 || $data['cat'] == -2)) ? 0 : (isset($data['cat']) && $data['cat'] != -1) ? (int)$data['cat'] : 0;
            if (isset($data['subcat'])) {
                if ($data['subcat'] == -1) $hasSub = 1;
                else if ($data['subcat'] == -2) $subCat = 0;
                else $subCat = (int)$data['subcat'];
            }
            $city_group = isset($data['city_group']) ? (int)$data['city_group'] : 0;
            $order = !empty($data['order']) ? (int)$data['order'] : 5000;
            $type = isset($data['ctype']) ? (int)$data['ctype'] : 1;
            $query = $this->getConnection()->query("UPDATE " . DB_STORE_CATS . " SET category='$catName',city_group=$city_group,type=$type,cat_order=$order,pid=$mainCat,mid=$subCat,has_sub=$hasSub WHERE category_id=" . $catID);
            if ($query) {
                $langSlug = $this->escapeObj->getSlug('cat' . $catID);
                $queryDelLang = $this->getConnection()->query("DELETE FROM " . DB_LANGUAGE_DATA . " WHERE keyword='$langSlug' AND rid=" . $catID . " AND category='scategory'");
                if ($queryDelLang) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $langRegister = $this->escapeObj->registerLang($langSlug, $title, $key, $catID, 'scategory');
                    }
                    if ($langRegister) {
                        return $catID;
                    }
                }

            }
        }
        return false;
    }

    public function deleteCategory()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) {
            $cont = true;
        }
        if (!$cont) {
            return false;
        }
        $continue = false;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $catFetch = $utiObj->getStoreCats($this->id);
            if (!empty($catFetch['category_id'])) {
                if ($catFetch['image_id'] != 0) {
                    $delIMG = $this->delObj->deleteMedia($catFetch['image_id']);
                    if ($delIMG) {
                        $continue = true;
                    }
                } else {
                    $continue = true;
                }

                if ($continue == true) {
                    $queryDel = $this->getConnection()->query("DELETE FROM " . DB_STORE_CATS . " WHERE category_id=" . $this->id);
                    if ($queryDel) {
                        $qDelInfo = $this->getConnection()->query("DELETE FROM " . DB_LANGUAGE_DATA . " WHERE keyword='" . $catFetch['category_name'] . "' AND category='scategory'");
                        if ($qDelInfo) {
                            $done = true;
                        }
                    }
                }
            }
            if (isset($done)) {
                if ($done) {
                    return true;
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