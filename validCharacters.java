public static boolean isvalidChar(String user){

    if(user.length() < 5 || user.length() > 50){
        return false;
    }

    if(!user.matches("[A-Z][A-Z-a-z0-9]*")){
        return false;
    }

    return true;
    
    }