<?php

namespace iCmsTrait;

trait Escape
{

    public function postEscape($content, $nullvalue = false)
    {
        if (empty($content) && $nullvalue != false) {
            $content = $nullvalue;
        }

        $content = trim($content);
        $content = $this->getConnection()->real_escape_string($content);
        $content = htmlspecialchars($content, ENT_QUOTES);
        $content = str_replace('\\r\\n', '', $content);
        $content = str_replace('\\r', '', $content);
        $content = str_replace('\\n\\n', '', $content);
        $content = str_replace('\\n', '', $content);
        $content = str_replace('\\n', '', $content);
        $content = stripslashes($content);
        return $content;
    }

    public function stringEscape($content, $nullvalue = false)
    {
        if (empty($content) && $nullvalue != false) {
            $content = $nullvalue;
        }

        $content = trim($content);
        $content = $this->getConnection()->real_escape_string($content);
        $content = htmlspecialchars($content, ENT_QUOTES);
        $content = stripslashes($content);

        return $content;
    }
}
