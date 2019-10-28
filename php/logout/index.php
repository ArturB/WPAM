<?php
    
    $username = $_COOKIE["username"];
    if( $username ) {
        session_destroy();
        setcookie("username", "", 100, "/");
        echo("User " . $username . " logged out!");
    }
    else {
        echo "Not logged in!";
    }

?>