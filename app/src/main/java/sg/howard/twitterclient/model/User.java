package sg.howard.twitterclient.model;

public class User {

    private final String name;
    private final String screenName;
    private final String profileImageUrl;

    public User(String name, String screenName, String profileImageUrl) {
        this.name = name;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
    }
}
