<?php

namespace iCms;

class Escape
{
    use \iCmsTrait\Escape;

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

    public function createLinks($content)
    {
        $link_regex = '/(http\:\/\/|https\:\/\/|www\.)([^\ ]+)/i';
        preg_match_all($link_regex, $content, $matches);

        foreach ($matches[0] as $url) {
            $matchUrl = strip_tags($url);
            $bbcode = '[a]' . urlencode($matchUrl) . '[/a]';
            $content = str_replace($url, $bbcode, $content);
        }

        return $content;
    }

    public function getSlug($string)
    {
        $string = utf8_encode($string);
        $string = iconv('UTF-8', 'ASCII//TRANSLIT', $string);
        $string = preg_replace('/[^a-z0-9- ]/i', '', $string);
        $string = str_replace(' ', '-', $string);
        $string = trim($string, '-');
        $string = strtolower($string);

        if (empty($string)) {
            return 'n-a';
        }

        return $string;
    }

    public function slugurl($text)
    {
        $text = $this->stringEscape($text);
        //$text = strtolower(htmlentities($text));
        $text = str_replace(get_html_translation_table(), "-", $text);
        $text = str_replace(array('\\', '/', ' '), "-", $text);
        $text = preg_replace("/[-]+/i", "-", $text);
        return $text;
    }

    public function arabic_query($text)
    {
        $text = $this->stringEscape($text);
        $text = preg_replace("/(أ|إ|ا|آ)/", "(أ|إ|ا|آ)", $text);
        $text = preg_replace("%(ه|ة)%", "(ه|ة)", $text);
        $text = preg_replace("%(ي|ى|ئ)%", "(ي|ى|ئ)", $text);
        $text = preg_replace("%(و|ؤ)%", "(و|ؤ)", $text);
        return $text;
    }

    /* Replace Arabic Char */

    public function registerLang($keyword, $langname, $langcode, $rid, $category)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        $querylang = $this->getConnection()->query("INSERT INTO " . DB_LANGUAGE_DATA . " (rid,lang_code,keyword,text,category) VALUES ($rid,'$langcode','$keyword','$langname','$category')");

        if ($querylang) {
            return true;
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function SerializeData($data)
    {
        $serialized_array = serialize($data);
        if (empty($serialized_array)) {
            return 'n-a';
        }
        return $serialized_array;
    }

    public function UNSerializeData($data)
    {
        $unserialized_array = unserialize($data);
        if (empty($unserialized_array)) {
            return 'n-a';
        }
        return $unserialized_array;
    }

    public function getLinks($content)
    {
        $link_search = '/\[a\](.*?)\[\/a\]/i';

        if (preg_match_all($link_search, $content, $matches)) {
            foreach ($matches[1] as $match) {
                $match_decode = urldecode($match);
                $match_url = $match_decode;

                if (!preg_match("/http(|s)\:\/\//", $match_decode)) {
                    $match_url = 'http://' . $match_url;
                }

                $content = str_replace('[a]' . $match . '[/a]', '<a href="' . strip_tags($match_url) . '" target="_blank" rel="nofollow" class="livepreview">' . $match_decode . '</a>', $content);
            }
        }

        return $content;
    }

    public function CorrectUrl($urlStr)
    {
        $parsed = parse_url($urlStr);
        if (empty($parsed['scheme'])) {
            $urlStr = 'http://' . ltrim($urlStr, '/');
        }
        return $urlStr;
    }

    public function urlToDomain($url)
    {
        return implode(array_slice(explode('/', preg_replace('/https?:\/\/(www\.)?/', '', $url)), 0, 1));
    }
}