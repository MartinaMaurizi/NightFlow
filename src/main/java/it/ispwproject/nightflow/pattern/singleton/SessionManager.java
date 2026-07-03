package it.ispwproject.nightflow.pattern.singleton;

import it.ispwproject.nightflow.bean.SessionBean;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.model.User;

public class SessionManager {

    private User loggedUser;
    private SessionBean sessionBean;

    private SessionManager() {}

    private static class Holder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public boolean isClient() {
        return isLoggedIn() && loggedUser.hasRole(Role.CLIENT);
    }

    public boolean isOrganizer() {
        return isLoggedIn() && loggedUser.hasRole(Role.ORGANIZER);
    }


    public void clearSession() {
        this.loggedUser  = null;
        this.sessionBean = null;
    }
}