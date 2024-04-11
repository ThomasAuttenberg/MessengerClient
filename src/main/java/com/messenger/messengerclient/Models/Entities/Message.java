package com.messenger.messengerclient.Models.Entities;

public class Message {

    private long message_id;
    private Long parentmessage_id;
    private String content;
    private long datetime;
    private long author_id;
    private String authorUsername;
    private boolean unread = false;
    private int quotes;

        public long getMessageId() {
            return message_id;
        }

        public long getParentMessageId() {
            return parentmessage_id;
        }

        public long getAuthorId() {
            return author_id;
        }
        public String getContent() {
            return content;
        }

        public int getQuotes() {
            return quotes;
        }

        public long getDatetime() {
            return datetime;
        }
        public String getAuthorUsername(){return authorUsername;}
        public void setAuthorId(long author_id) {
            this.author_id = author_id;
        }

        public void setParentMessageId(long parentmessage_id) {
            this.parentmessage_id = parentmessage_id;
        }
        public void setAuthorUsername(String authorUsername){this.authorUsername = authorUsername;}
        public void setContent(String content) {
            this.content = content;
        }
        public void setMessageId(long id){this.message_id = id;}
        public void setDatetime(long datetime) {
            this.datetime = datetime;
        }
        public void setQuotes(int quotes){
            this.quotes = quotes;
        }
        public void setUnread(){
            unread = true;

        }
}
