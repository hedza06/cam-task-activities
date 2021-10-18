package com.hedza06.camtask.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "variable_store")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
public class VariableStoreEntity implements Serializable {

    private static final long serialVersionUID = 2021101817520000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "process_instance_id", nullable = false)
    private String processInstanceId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "product_id")
    private Long productId;

    public VariableStoreEntity(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
}
