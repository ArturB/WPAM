<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }
    
    $passed_text      = $_GET["address"];
    $passed_latitude  = $_GET["latitude"];
    $passed_longitude = $_GET["longitude"];
    
    $query = "INSERT INTO Voter (Address, Latitude, Longitude) VALUES ('$passed_text', $passed_latitude, $passed_longitude)";
    $query_response = $mysqli->query($query);
    if(! $query_response ) {
        http_response_code(403);
        echo "Invalid query!";
        die();
    }
    else {
        echo $mysqli->insert_id;
    }
    
?>
