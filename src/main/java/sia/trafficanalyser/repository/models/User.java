package sia.trafficanalyser.repository.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="users")
public class User {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String username;
    @NotBlank
    @Size(min = 8)
    private String password;
    private String fullname;
    private String organisation;
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;
    private String resetPasswordToken;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_devices",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "device_id"))
    private Set<Device> devices = new HashSet<>();

    public User() {
    }
    public User(String username, String password, String fullname, String organisation, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.organisation = organisation;
        this.phoneNumber = phoneNumber;
        this.resetPasswordToken = null;
    }
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Device> getDevices() { return devices; }

    public void setDevices(Set<Device> devices) { this.devices = devices;}
}
