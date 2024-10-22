package com.trident.egovernance.global.nav;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MenuNode {
    private String title;
    private String url;
    private Set<String> accessRoles;
    private List<MenuNode> children;
    public MenuNode(String title, String url, Set<String> accessRoles) {
        this.title = title;
        this.url = url;
        this.accessRoles = accessRoles !=null ? new HashSet<>(accessRoles) : new HashSet<>();
        if(this.accessRoles.isEmpty()){
            this.accessRoles.add("public");
        }
        this.children = new ArrayList<>();
    }

    public void setAccessRoles(Set<String> accessRoles) {
        this.accessRoles = accessRoles != null ? new HashSet<>(accessRoles) : new HashSet<>();
    }
    public void addChild(MenuNode child){
        this.children.add(child);
    }

    public void removeChild(MenuNode childTitle){
        this.children.removeIf(child->child.getTitle().equals(childTitle));
    }

    public void addAccessRole(String role){
        this.accessRoles.add(role);
    }
}
