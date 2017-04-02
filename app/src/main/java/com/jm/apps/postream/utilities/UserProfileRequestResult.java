package com.jm.apps.postream.utilities;

import com.jm.apps.postream.models.User;

/**
 * Created by Jared12 on 4/2/17.
 */

public class UserProfileRequestResult {
    private User mUser;

    public UserProfileRequestResult(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
