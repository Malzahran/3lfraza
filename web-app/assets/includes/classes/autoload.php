<?php
function autoload($className)
{
    $className = ltrim($className, '\\');
    $fileName = '';
    $namespace = '';
    if ($lastNsPos = strrpos($className, '\\')) {
        $namespace = substr($className, 0, $lastNsPos);
        $className = substr($className, $lastNsPos + 1);
        //$fileName  = str_replace('\\', DIRECTORY_SEPARATOR, $namespace) . DIRECTORY_SEPARATOR;

        if ($namespace == "iCmsTrait") {
            $fileName = "traits/";
        } elseif ($namespace == "iCmsAPI") {
            $fileName = "api/";
        } elseif ($namespace == "iCmsSeller") {
            $fileName = "seller/";
        }
    }

    $fileName .= str_replace('_', DIRECTORY_SEPARATOR, $className);

    if ($namespace == "iCms") {
        $fileName .= ".class.php";
    } elseif ($namespace == "iCmsAPI") {
        $fileName .= ".api.php";
    } elseif ($namespace == "iCmsSeller") {
        $fileName .= ".seller.php";
    } elseif ($namespace == "iCmsTrait") {
        $fileName .= ".trait.php";
    }

    if (!empty($fileName)) {
        require $fileName;
    }
}

spl_autoload_register('autoload');