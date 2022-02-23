<?php

namespace iCms;


class Misc
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

    public function setConnection(\mysqli $conn)
    {
        $this->conn = $conn;
        return $this;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function registerMisc($data = array())
    {
        if (!empty ($data['keyword']) && !empty ($data['lang_code'])) {
            $keyword = $this->escapeObj->stringEscape($data['keyword']);
            $lang = $this->escapeObj->stringEscape($data['lang_code']);
            $title = (!empty($data['title'])) ? $this->escapeObj->stringEscape($data['title']) : null;
            $content = (!empty($data['content'])) ? $this->escapeObj->postEscape($data['content']) : null;
            $query = $this->getConnection()->query("INSERT INTO " . DB_MISC_DATA . " (keyword,lang_code,title,content) VALUES ('$keyword','$lang','$title','$content')");
            if ($query) {
                $itemId = $this->getConnection()->insert_id;
                return $itemId;
            }
        }
        return false;
    }

    public function editMisc($data = array())
    {
        if (!empty ($data['keyword']) && !empty ($data['lang_code'])) {
            $keyword = $this->escapeObj->stringEscape($data['keyword']);
            $lang = $this->escapeObj->stringEscape($data['lang_code']);
            $title = (!empty($data['title'])) ? $this->escapeObj->stringEscape($data['title']) : null;
            $content = (!empty($data['content'])) ? $this->escapeObj->postEscape($data['content']) : null;
            $query = $this->getConnection()->query("UPDATE " . DB_MISC_DATA . " SET title = '$title',content = '$content' WHERE keyword = '$keyword' AND lang_code = '$lang'");
            if ($query) {
                return true;
            }
        }
        return false;
    }
}