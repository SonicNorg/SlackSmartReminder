package com.grizzly.studio.slacksmartreminder;


import java.io.Serializable;

/**
 * Created by Norg on 14.03.2016.
 */
public class UserItem implements Serializable {
    private String name;
    private boolean enabled = true;
    //todo private SlackUser
    // TODO SlackUser methods

    public UserItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
