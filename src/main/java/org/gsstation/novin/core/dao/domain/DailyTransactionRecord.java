package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "DAILY_TRANSACTION_RECORD")
public class DailyTransactionRecord extends BaseEntityTrx {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gs_id")
    private String gsId;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "fuel_ttc")
    private Integer fuelTtc;
}
