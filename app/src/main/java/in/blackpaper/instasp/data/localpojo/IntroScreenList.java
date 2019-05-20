package in.blackpaper.instasp.data.localpojo;



public class IntroScreenList {
    String info;
    int image;
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IntroScreenList(String title, int image, String info) {
        this.info = info;
        this.image = image;
        this.title = title;
    }

    public int getImage() {

        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getInfo() {

        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
