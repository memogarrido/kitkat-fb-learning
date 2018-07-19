package firekitkat.com.firekitkat.models;

/***
 * Model class to represent a user of the app
 */
public class User {
    private String name;
    private String photoUrl;

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
