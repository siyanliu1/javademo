package com.example.demo.entity;
import jakarta.persistence.*;


@Entity
@Table(name = "user_permission")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多对一关联 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 多对一关联 Permission
    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    public UserPermission() {}

    // Getter 和 Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Permission getPermission() {
        return permission;
    }
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}