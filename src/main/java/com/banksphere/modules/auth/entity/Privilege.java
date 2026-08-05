package com.banksphere.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
}
