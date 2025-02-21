package com.middleware.server.model.PO;

import java.time.LocalDateTime;

public class RolePrivilegePO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.privilege_id
     *
     * @mbg.generated
     */
    private Long privilegeId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.role_id
     *
     * @mbg.generated
     */
    private Long roleId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.creator_id
     *
     * @mbg.generated
     */
    private Long creatorId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.signature
     *
     * @mbg.generated
     */
    private String signature;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.gmt_create
     *
     * @mbg.generated
     */
    private LocalDateTime gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_privilege.gmt_modified
     *
     * @mbg.generated
     */
    private LocalDateTime gmtModified;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.id
     *
     * @return the value of role_privilege.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.id
     *
     * @param id the value for role_privilege.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.privilege_id
     *
     * @return the value of role_privilege.privilege_id
     *
     * @mbg.generated
     */
    public Long getPrivilegeId() {
        return privilegeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.privilege_id
     *
     * @param privilegeId the value for role_privilege.privilege_id
     *
     * @mbg.generated
     */
    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.role_id
     *
     * @return the value of role_privilege.role_id
     *
     * @mbg.generated
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.role_id
     *
     * @param roleId the value for role_privilege.role_id
     *
     * @mbg.generated
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.creator_id
     *
     * @return the value of role_privilege.creator_id
     *
     * @mbg.generated
     */
    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.creator_id
     *
     * @param creatorId the value for role_privilege.creator_id
     *
     * @mbg.generated
     */
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.signature
     *
     * @return the value of role_privilege.signature
     *
     * @mbg.generated
     */
    public String getSignature() {
        return signature;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.signature
     *
     * @param signature the value for role_privilege.signature
     *
     * @mbg.generated
     */
    public void setSignature(String signature) {
        this.signature = signature == null ? null : signature.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.gmt_create
     *
     * @return the value of role_privilege.gmt_create
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.gmt_create
     *
     * @param gmtCreate the value for role_privilege.gmt_create
     *
     * @mbg.generated
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_privilege.gmt_modified
     *
     * @return the value of role_privilege.gmt_modified
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_privilege.gmt_modified
     *
     * @param gmtModified the value for role_privilege.gmt_modified
     *
     * @mbg.generated
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}