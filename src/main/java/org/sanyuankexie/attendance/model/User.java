package org.sanyuankexie.attendance.model;


import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String dept;
    private String location;
    private String email;
    private String githubId;

}
