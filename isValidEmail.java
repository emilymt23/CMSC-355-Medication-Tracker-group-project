public static boolean isValidUser(String user){

    if(user.equalsIgnoreCase("example@email.com"))
{
    System.out.println(user + " already exists. Choose a different email/username");
    return false;
}

else {
    return true;
}


}