<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }
    
    $passed_query = $_GET["query"];
    
    $query = "SELECT * FROM Voter WHERE " . $passed_query;
    $query_response = $mysqli->query($query);
    
    if(! $query_response ) {
        http_response_code(403);
        echo "Invalid query!";
        die();
    }
    else {
        $result = "[ " . json_encode($query_response->fetch_array(MYSQLI_ASSOC));
        while( $row = $query_response->fetch_array(MYSQLI_ASSOC) ) {
            $result = $result . ", ";
            $result = $result . json_encode($row);
        }
        $result = $result . " ]";
        echo $result;
        die();
    }
    
?>

