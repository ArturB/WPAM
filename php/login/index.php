<?php
    
    $mysqli = new mysqli("s2.brodzki.org", "wpam", "sh5hh5gd4#", "wpam");

    //check connection
    if ($mysqli->connect_errno) {
        http_response_code(403);
        printf("Connect failed: %s\n", $mysqli->connect_error);
        exit();
    }

    $username = $_GET['username'];
    $password = $_GET['password'];
   
    $credentials_response = $mysqli->query("SELECT PasswordHash, PasswordSalt FROM User WHERE Username='$username'");
    
    if(! $credentials_response ) {
        http_response_code(403);
        echo "Invalid username!";
        die();
    }
    
    $credentials = $credentials_response->fetch_array(MYSQLI_ASSOC);
    $hash = $credentials['PasswordHash'];
    $salt = $credentials['PasswordSalt'];
    $passSalt = $password . $salt;
    
    if( hash("sha512", $passSalt) == $hash) {
        session_start();
        setcookie("username", $username, 0, "/");
        //get last login datetime
        $query = "SELECT LastLogin FROM User WHERE Username = '$username'";
        $query_response = $mysqli->query($query);
        $last_login = $query_response->fetch_array(MYSQLI_ASSOC);
        echo $last_login["LastLogin"]; 
        //echo "Success!";
        //update to new date
        $query = "UPDATE User SET LastLogin = NOW() WHERE Username = '$username'";
        $query_response = $mysqli->query($query);
    }
    else {
        http_response_code(403);
        echo "Invalid credentials!";
        die();
    }    
    
?>

