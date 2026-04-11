package com.uow.entity;

/**
 * 代表一个完整的用户资料数据。
 * 这个类现在是一个纯粹、干净、无状态的数据对象 (DTO/Domain Object)，
 * 不再包含任何数据访问逻辑，从而实现了非冗余和非复杂的设计。
 */
public class ViewUserProfile {
    private final String profileId;
    private final String roleName;
    private final String status;

    /**
     * 创建一个完整的 ViewUserProfile 对象的构造函数。
     * 所有参数都是必须的，确保对象的完整性。
     * @param profileId 用户资料 ID。
     * @param roleName 角色名称。
     * @param status 状态。
     */
    public ViewUserProfile(String profileId, String roleName, String status) {
        this.profileId = profileId;
        this.roleName = roleName;
        this.status = status;
    }

    // Getters，确保对象是不可变的，状态清晰
    public String getProfileId() { return profileId; }
    public String getRoleName() { return roleName; }
    public String getStatus() { return status; }
}
