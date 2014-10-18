package ru.ifmo.rss;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class FeedItem {
    private final String link;
    private final String title;
    private final String description;

    public FeedItem(String link, String title, String description) {
        this.link = link;
        this.title = title;
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
