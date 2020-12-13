package application.tool.activity.message.object;

import java.util.ArrayList;

public class Post  {
    /***
     * @author
     */
    private String email,title,bodyText,bodyImage,hashTag;

    public Post() {
    }

    public Post(String email, String title, String bodyText, String bodyImage, String hashTag) {
        this.email = email;
        this.title = title;
        this.bodyText = bodyText;
        this.bodyImage = bodyImage;
        this.hashTag = hashTag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getBodyImage() {
        return bodyImage;
    }

    public void setBodyImage(String bodyImage) {
        this.bodyImage = bodyImage;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }
}
