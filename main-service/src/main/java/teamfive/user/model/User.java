package teamfive.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import teamfive.subscription.model.Subscription;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "User: Поле name не может быть пустым")
    @Size(min = 2, max = 255)
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "User: Поле email не может быть пустым")
    @Email
    @Size(min = 5, max = 255)
    private String email;


    //для избежения циклических ссылок
    @JsonIgnore
    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<Subscription> followers = new ArrayList<>();

}
