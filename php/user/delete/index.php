<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }
    
    $passed_username = $_GET["username"];
    
    $query = "DELETE FROM User WHERE Username = '$passed_username'";
    $query_response = $mysqli->query($query);
    if(! $query_response ) {
        http_response_code(403);
        echo "User doesn't exist!";
        die();
    }
    else {
        echo "User deleted!";
    }
    
?>


