<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }
    
    $passed_id     = $_GET["id"];
    $passed_column = $_GET["column"];
    $passed_val    = $_GET["value"];
    
    $query = "UPDATE Voter SET $passed_column = '$passed_val' WHERE id = '$passed_id'";
    $query_response = $mysqli->query($query);
    if(! $query_response ) {
        http_response_code(403);
        echo "Invalid query!";
        die();
    }
    else {
        echo "Voter updated!";
    }
    
?>

