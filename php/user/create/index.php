<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }
    
    $passed_username = $_GET["username"];
    $passed_password = $_GET["password"];
    $passed_role = $_GET["role"];
    srand(microtime() * 100000);
    $passwordSalt = rand(1,1000000);
    $hashed = $passed_password . $passwordSalt;
    $passwordHash = hash("sha512", $hashed);
    
    $query = "INSERT INTO User (Username, PasswordHash, PasswordSalt, Role) VALUES ('$passed_username', '$passwordHash', $passwordSalt, '$passed_role')";
    $query_response = $mysqli->query($query);
    if(! $query_response ) {
        http_response_code(403);
        echo "User exists!";
        die();
    }
    else {
        echo $mysqli->insert_id;
    }
    
?>

