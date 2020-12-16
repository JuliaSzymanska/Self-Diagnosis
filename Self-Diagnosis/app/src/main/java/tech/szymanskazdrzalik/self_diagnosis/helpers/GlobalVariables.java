package tech.szymanskazdrzalik.self_diagnosis.helpers;

import java.util.Optional;

import tech.szymanskazdrzalik.self_diagnosis.db.User;

public class GlobalVariables {
    private static final GlobalVariables INSTANCE = new GlobalVariables();

    private User currentUser;

    private GlobalVariables() {
    }

    public static GlobalVariables getInstance() {
        return INSTANCE;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(this.currentUser);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}