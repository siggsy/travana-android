package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class CommentsApprovalRequest {

    private String message_id;
    private String comment_id;
    private boolean approved;

    public CommentsApprovalRequest(String message_id, String comment_id, boolean approved) {
        this.message_id = message_id;
        this.comment_id = comment_id;
        this.approved = approved;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "CommentsApprovalRequest{" +
                "message_id='" + message_id + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", approved=" + approved +
                '}';
    }
}
