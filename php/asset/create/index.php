<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }

    $passed_type = $_GET["type"];
    $passed_url  = $_GET["url"];
    $passed_rule = $_GET["rule"];
    
    $query = "INSERT INTO Asset (Type, Url, Rule) VALUES ('$passed_type', '$passed_url', $passed_rule)";
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

