package cn.catalpaflat.model.po;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "role")
public class RolePO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

}
