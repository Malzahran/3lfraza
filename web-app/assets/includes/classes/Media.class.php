<?php

namespace iCms;

class Media
{
    public $data = array();
    private $id;
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

    public function getMedia()
    {
        $query1 = $this->getConnection()->query("SELECT * FROM " . DB_MEDIA . " WHERE id=" . $this->id);

        if ($query1->num_rows == 1) {
            $media = $query1->fetch_array(MYSQLI_ASSOC);

            if ($media['type'] === "photo") {
                $media['complete_url'] = $media['url'] . '.' . $media['extension'];
                $this->data = array(
                    'type' => 'photo',
                    'num' => 1
                );
                $this->data['each'][] = $media;
            } elseif ($media['type'] === "album") {
                $query2 = $this->getConnection()->query("SELECT * FROM " . DB_MEDIA . " WHERE album_id=" . $media['id'] . " AND active=1 ORDER BY id DESC");
                $this->data = array(
                    'type' => 'album',
                    'num' => $query2->num_rows,
                    'name' => $media['name'],
                    'temp' => $media['temp']
                );

                while ($single_media = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $this->data['each'][] = $single_media;
                }
            }

            return $this->data;
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function importMedia($url = '', $type = '')
    {
        if (empty($url)) {
            return false;
        }
        if ($type == 'facebook') {
            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 5);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            $contents = curl_exec($ch);
            $image = json_decode($contents, true);
            curl_close($ch);
            $url = $image['data']['url'];
        }
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 5);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        $source = curl_exec($ch);
        if (curl_errno($ch)) {
            return false;
        } else {
            curl_close($ch);
        }


        if (!file_exists('photos/' . date('Y'))) {
            mkdir('photos/' . date('Y'), 0777, true);
        }

        if (!file_exists('photos/' . date('Y') . '/' . date('m'))) {
            mkdir('photos/' . date('Y') . '/' . date('m'), 0777, true);
        }

        $photo_dir = 'photos/' . date('Y') . '/' . date('m');
        $name = preg_replace('/([^A-Za-z0-9_\-\.]+)/i', '', $url);
        $url_ext = 'jpg';
        $query = $this->getConnection()->query("INSERT INTO " . DB_MEDIA . " (extension,name,type) VALUES ('$url_ext','$name','photo')");

        if (!$query) {
            return false;
        }

        $sqlId = $this->getConnection()->insert_id;
        $original_file_name = $photo_dir . '/' . generateKey() . '_' . $sqlId . '_' . md5($sqlId);
        $original_file = $original_file_name . '.' . $url_ext;
        $register_cover = @file_put_contents($original_file, $source);

        if ($register_cover) {
            list($width, $height) = getimagesize($original_file);
            $min_size = $width;

            if ($width > $height) {
                $min_size = $height;
            }

            $min_size = floor($min_size);

            if ($min_size > 920) {
                $min_size = 920;
            }

            $imageSizes = array(
                'thumb' => array(
                    'type' => 'crop',
                    'width' => 128,
                    'height' => 128,
                    'name' => $original_file_name . '_thumb'
                ),
                '100x100' => array(
                    'type' => 'crop',
                    'width' => $min_size,
                    'height' => $min_size,
                    'name' => $original_file_name . '_squ'
                ),
                '100x60' => array(
                    'type' => 'crop',
                    'width' => $min_size,
                    'height' => floor($min_size * 0.60),
                    'name' => $original_file_name . '_rec'
                )
            );

            foreach ($imageSizes as $ratio => $data) {
                $save_file = $data['name'] . '.' . $url_ext;
                processMedia($data['type'], $original_file, $save_file, $data['width'], $data['height']);
            }

            $this->getConnection()->query("UPDATE " . DB_MEDIA . " SET url='$original_file_name',temp=0,active=1 WHERE id=$sqlId");

            $this->data = $sqlId;

            return $this->data;
        }
        return false;
    }

    public function getById($id)
    {
        $this->setId($id);
        return $this->getRows();
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function getRows()
    {
        global $config;
        $query1 = $this->getConnection()->query("SELECT * FROM " . DB_MEDIA . " WHERE id=" . $this->id);

        if ($query1->num_rows == 1) {
            $media = $query1->fetch_array(MYSQLI_ASSOC);

            if ($media['type'] === "photo") {
                $media['complete_url'] = $config['site_url'] . '/' . $media['url'] . '.' . $media['extension'];
                $media['squ_url'] = $config['site_url'] . '/' . $media['url'] . '_squ.' . $media['extension'];
                $media['thumb_url'] = $config['site_url'] . '/' . $media['url'] . '_thumb.' . $media['extension'];
                $media['rec_url'] = $config['site_url'] . '/' . $media['url'] . '_rec.' . $media['extension'];

                $this->data = array(
                    'id' => $media['id'],
                    'type' => 'photo',
                    'num' => 1
                );
                $this->data['each'][] = $media;

            } elseif ($media['type'] === "album") {
                $query2 = $this->getConnection()->query("SELECT * FROM " . DB_MEDIA . " WHERE album_id=" . $media['id'] . " AND active=1 ORDER BY id DESC");
                $this->data = array(
                    'id' => $media['id'],
                    'type' => 'album',
                    'num' => $query2->num_rows,
                    'name' => $media['name'],
                    'temp' => $media['temp']
                );

                while ($single_media = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $single_media['complete_url'] = $config['site_url'] . '/' . $single_media['url'] . '.' . $single_media['extension'];
                    $single_media['squ_url'] = $config['site_url'] . '/' . $single_media['url'] . '_squ.' . $single_media['extension'];
                    $single_media['thumb_url'] = $config['site_url'] . '/' . $single_media['url'] . '_thumb.' . $single_media['extension'];
                    $single_media['rec_url'] = $config['site_url'] . '/' . $single_media['url'] . '_rec.' . $single_media['extension'];

                    $this->data['each'][] = $single_media;
                }
            }

            return $this->data;
        }
        return false;
    }
}