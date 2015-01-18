package ru.itmo.rss;

public class ItemRSS {
    private final String link;
    private final String title;
    private final String description;

    public ItemRSS(String link, String title, String description) {
        this.link = link;
        this.title = title;
        this.description = description;
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

        public ItemRSS createItem() {
            return new ItemRSS(link, title, description);
        }
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
        return String.format("FeedItem{link='%s', title='%s', description='%s'}", link, title, description);
    }


}