<?php

namespace iCms;

class UI
{
    public static function view()
    {
        global $config;

        $params = func_get_args();
        $file = $params[0];
        $page = 'themes/' . $config['theme'] . '/layout/' . $file . '.phtml';
        $contentOpen = fopen($page, 'r');
        $content = @fread($contentOpen, filesize($page));
        fclose($contentOpen);

        $content = preg_replace_callback(
            '/@([a-zA-Z0-9_]+)@/',

            function ($matches) {
                $matches[1] = strtolower($matches[1]);
                $la = Lang($matches[1], 1);
                return (isset($la) ? $la : "");
            },

            $content
        );

        $content = preg_replace_callback(
            '/iC{{([A-Z0-9_]+)}}/',

            function ($matches) {
                global $themeData;
                $matches[1] = strtolower($matches[1]);
                return (isset($themeData[$matches[1]]) ? $themeData[$matches[1]] : "");
            },

            $content
        );

        return $content;
    }

    public static function html($content = '')
    {
        return htmlspecialchars_decode(stripslashes($content));
    }
}