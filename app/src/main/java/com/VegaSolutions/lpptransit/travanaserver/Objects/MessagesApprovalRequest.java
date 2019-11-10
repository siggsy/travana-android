package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class MessagesApprovalRequest {

    private String _id;
    private boolean approve;

    public MessagesApprovalRequest(String _id, boolean approve) {
        this._id = _id;
        this.approve = approve;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    @Override
    public String toString() {
        return "MessagesApprovalRequest{" +
                "_id='" + _id + '\'' +
                ", approve=" + approve +
                '}';
    }
}
