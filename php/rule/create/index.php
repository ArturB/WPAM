<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }

    $passed_text = $_GET["text"];
    
    $query = "INSERT INTO Rule (Text) VALUES ('$passed_text')";
    $query_response = $mysqli->query($query);
    if(! $query_response ) {
        http_response_code(403);
        echo "Rule exists!;
        die();
    }
    else {
        echo $mysqli->insert_id;
    }
    
?>

