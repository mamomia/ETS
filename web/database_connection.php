<?php

/**
 * @author Salman Tahir
 * @copyright 2016
 */

$connection=@mysql_connect("localhost","root","") or die("Error in connection");
$db=@mysql_select_db("ets",$connection) or die("Could not connect to database");
?>