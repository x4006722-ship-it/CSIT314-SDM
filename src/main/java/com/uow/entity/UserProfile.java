package com.uow.entity;

public class UserProfile {
    private String profileId;
    private String roleName;
    private String status;

    public UserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName;
        this.status = status;
    }

    public String getProfileId() { return profileId; }
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}