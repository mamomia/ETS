<?php

/**
 * @author Salman Tahir
 * @copyright 2016
 */
include('database_connection.php');
$query="select * from employee";
$run=@mysql_query($query);
$json=array();
while($row=@mysql_fetch_assoc($run)){
  $json[]=$row;
  $result=$json;
}
echo json_encode($result);


?>