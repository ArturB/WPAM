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
    srand(microtime() * 100000);
    $passwordSalt = rand(1,1000000);
    $hashed = $passed_password . $passwordSalt;
    $passwordHash = hash("sha512", $hashed);
    
    $query = "UPDATE User SET PasswordHash = '$passwordHash', PasswordSalt = $passwordSalt WHERE Username = '$passed_username'";
    $query_response = $mysqli->query($query);
    if(! $query_response ) {
        http_response_code(403);
        echo "Invalid query!";
        die();
    }
    else {
        echo "User password changed!";
    }
    
?>

