<?php

namespace iCmsSeller;


use iCms\DeleteMedia;
use iCms\Escape;
use iCms\Utilities;

class Promo
{
    private $conn;
    private $escapeObj;
    private $id;

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new Escape();
        return $this;
    }

    public function registerItem($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty ($data['promo']) && !empty($data['p_type']) && !empty($data['p_value'])) {
            $code = $this->escapeObj->stringEscape($data['promo']);
            $amount = (float)$data['p_value'];
            $type = (int)$data['p_type'];
            $query = $this->getConnection()->query("INSERT INTO " . DB_PROMO . " (promo,type,value,active,time) VALUES ('$code',$type,'$amount',1," . time() . ")");

            if ($query) {
                $itemID = $this->getConnection()->insert_id;
                return $itemID;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function activeItem()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $itemFetch = $utiObj->getPromoCode('', $this->id);
            if (!empty($itemFetch['id'])) {
                if ($itemFetch['active'] == 1) {
                    $active = 0;
                } else {
                    $active = 1;
                }
                $query = $this->getConnection()->query("UPDATE " . DB_PROMO . " SET active = $active WHERE id=" . $itemFetch['id']);
                if ($query) {
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