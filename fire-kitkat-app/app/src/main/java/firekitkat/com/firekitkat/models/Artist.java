package firekitkat.com.firekitkat.models;

/***
 * Model class representing an Artist
 */
public class Artist {
    /***
     * Name of the artist
     */
    private String name;
    /***
     * Artist facemap (distances from nose to eyes and mouth
     */
    private FaceMap faceMap;
    /***
     * Youtube Video Id
     */
    private String videoId;
    /***
     * Artist face pic
     */
    private String photoUrl;
    public Artist(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FaceMap getFaceMap() {
        return faceMap;
    }

    public void setFaceMap(FaceMap faceMap) {
        this.faceMap = faceMap;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
