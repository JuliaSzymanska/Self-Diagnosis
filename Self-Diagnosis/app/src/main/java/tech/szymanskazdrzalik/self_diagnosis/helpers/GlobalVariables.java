package tech.szymanskazdrzalik.self_diagnosis.helpers;

import tech.szymanskazdrzalik.self_diagnosis.db.User;

public class GlobalVariables {
    private static final GlobalVariables INSTANCE = new GlobalVariables();

    private User currentUser;

    private GlobalVariables() {
    }

    public static GlobalVariables getInstance() {
        return INSTANCE;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}