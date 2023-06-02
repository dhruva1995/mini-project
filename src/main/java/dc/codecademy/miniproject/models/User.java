package dc.codecademy.miniproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_username", columnNames = { "userName" }))
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String roles;

    public User(String userName, String password, String roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }

    public User(Long id) {
        this.id = id;
    }
}
