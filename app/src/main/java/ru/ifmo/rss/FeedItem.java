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

    public static class Builder {
        private String link;
        private String title;
        private String description;

        public Builder setLink(String link) {
            this.link = link;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder clear() {
            this.link = null;
            this.title = null;
            this.description = null;

            return this;
        }

        public FeedItem createFeedItem() {
            return new FeedItem(link, title, description);
        }
    }
}
