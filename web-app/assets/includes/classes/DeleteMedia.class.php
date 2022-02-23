<?php

namespace iCms;

class DeleteMedia
{
    private $conn;

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        return $this;
    }

    public function setConnection(\mysqli $conn)
    {
        $this->conn = $conn;
        return $this;
    }

    public function deleteMedia($mediaid = 0)
    {
        $mediaid = (int)$mediaid;
        $querydel = false;
        $done = false;
        if (!empty ($mediaid)) {
            $mediaObj = new Media();
            $mediaObj->setId($mediaid);
            $media = $mediaObj->getRows();
            if (!empty($media['type'])) {
                if (!empty($media['each'])) {
                    foreach ($media['each'] as $photo) {
                        $querydel = $this->getConnection()->query("DELETE FROM " . DB_MEDIA . " WHERE id=" . $photo['id']);
                        if (!empty($photo['url'])) {
                            $dirimages = glob(str_replace(SITE_URL . "/", "", $photo['url']) . "*");
                            foreach ($dirimages as $dirimg) {
                                unlink($dirimg);
                            }
                        }
                    }
                    if ($querydel) {

                        if ($media['type'] === "photo") {
                            $done = true;
                        } else if ($media['type'] === "album") {
                            $queryalbum = $this->getConnection()->query("DELETE FROM " . DB_MEDIA . " WHERE id=" . $mediaid);
                            if ($queryalbum) {
                                $done = true;
                            }
                        }
                    }
                } else {
                    if ($media['type'] === "album") {
                        $queryalbum = $this->getConnection()->query("DELETE FROM " . DB_MEDIA . " WHERE id=" . $mediaid);
                        if ($queryalbum) {
                            $done = true;
                        }
                    }
                }
            }
            if ($done) {
                return true;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }
}