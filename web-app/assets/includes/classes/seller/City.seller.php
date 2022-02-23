<?php

namespace iCmsSeller;


use iCms\DeleteMedia;
use iCms\Escape;
use iCms\Utilities;

class City
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

    public function register($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) $cont = true;
        if (!$cont) return false;
        if (!empty($data['city_group']) && !empty($data['lat']) && !empty($data['lon']) && !empty($data['shipping']) && !empty($data['radius']) && isset($data['title']['en']) && isset($data['title']['ar'])) {
            $titleEN = $this->escapeObj->stringEscape($data['title']['en']);
            $titleAR = $this->escapeObj->stringEscape($data['title']['ar']);
            $cityName = $titleEN . ' - ' . $titleAR;
            $group = (int)$data['city_group'];
            $lat = (double)$data['lat'];
            $lon = (double)$data['lon'];
            $radius = (double)$data['radius'];
            $shipping = (float)$data['shipping'];
            $query = $this->getConnection()->query("INSERT INTO " . DB_CITY . " (city,city_group,lat,lon,radius,shipping,csid,time) VALUES ('$cityName',$group,'$lat','$lon','$radius','$shipping',2," . time() . ")");
            if ($query) {
                $cityID = $this->getConnection()->insert_id;
                $langSlug = $this->escapeObj->getSlug('city' . $cityID);
                $query2 = $this->getConnection()->query("UPDATE " . DB_CITY . " SET city_name = '$langSlug' WHERE city_id=" . $cityID);
                if ($query2) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $langRegister = $this->escapeObj->registerLang($langSlug, $title, $key, $cityID, 'city');
                    }
                    if (isset($langRegister)) {
                        if ($langRegister) {
                            return $cityID;
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

    public function edit($data = array())
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged) {
            $cont = true;
        }
        if (!$cont) {
            return false;
        }
        if (!empty($this->id) && isset($data['title']['en']) && isset($data['title']['ar']) && !empty($data['city_group']) && !empty($data['lat']) && !empty($data['lon']) && !empty($data['shipping']) && !empty($data['radius'])) {
            $cityID = $this->id;
            $titleEN = $this->escapeObj->stringEscape($data['title']['en']);
            $titleAR = $this->escapeObj->stringEscape($data['title']['ar']);
            $cityName = $titleEN . ' - ' . $titleAR;
            $group = (int)$data['city_group'];
            $lat = (double)$data['lat'];
            $lon = (double)$data['lon'];
            $radius = (double)$data['radius'];
            $shipping = (float)$data['shipping'];

            $query = $this->getConnection()->query("UPDATE " . DB_CITY . " SET city='$cityName',city_group=$group,lat='$lat',lon='$lon',radius='$radius',shipping='$shipping' WHERE city_id=" . $cityID);
            if ($query) {
                $langRegister = false;
                $langSlug = $this->escapeObj->getSlug('city' . $cityID);
                $queryDelLang = $this->getConnection()->query("DELETE FROM " . DB_LANGUAGE_DATA . " WHERE keyword='$langSlug' AND rid=" . $cityID . " AND category='city'");
                if ($queryDelLang) {
                    foreach ($data['title'] as $key => $val) {
                        $title = $this->escapeObj->stringEscape($val);
                        $langRegister = $this->escapeObj->registerLang($langSlug, $title, $key, $cityID, 'city');
                    }
                    if ($langRegister) return $cityID;
                }

            }
        }
        return false;
    }

    public function delete()
    {
        global $sellerLogged, $adminLogged;
        $cont = false;
        if ($sellerLogged || $adminLogged)
            $cont = true;
        if (!$cont) return false;
        if (!empty($this->id)) {
            $utiObj = new Utilities();
            $cityFetch = $utiObj->getCity($this->id);
            if (!empty($cityFetch['city_id'])) {
                $queryDel = $this->getConnection()->query("DELETE FROM " . DB_CITY . " WHERE city_id=" . $this->id);
                if ($queryDel) {
                    $qDelInfo = $this->getConnection()->query("DELETE FROM " . DB_LANGUAGE_DATA . " WHERE keyword='" . $cityFetch['city_name'] . "' AND category='city'");
                    if ($qDelInfo) $done = true;
                }
            }
            if (isset($done) && $done) return true;
        }
        return false;
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }
}