package dc.codecademy.miniproject.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity

@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "userId" }))
@Data
@NoArgsConstructor
public class PhotoMetadata {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "userId")
    private User user;

    private String name;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    public PhotoMetadata(User createdBy, String name, LocalDateTime createAt, LocalDateTime updateAt) {
        this.user = createdBy;
        this.name = name;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }
}
